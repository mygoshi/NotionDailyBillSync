/*
 * Copyright (c) 2021
 * User: Shuai
 * File: DataLoader.java
 * Date: 2021/08/30 23:31:30
 */

package com.mygoshi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mygoshi.JSONObjects.BillItem;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DataLoader {

    private String secretKey;
    private String notionVersion;
    private String databaseID;
    private String college;
    private String ecardAuth;

    public DataLoader() {
        loadConfig();
    }

    /**
     * Load and parse bill list.
     * @param path Path of .CSV file
     * @return List of bill items.
     */
    public ArrayList<String> loadBillData(String path) {
        ArrayList<String[]> billDataRaw = new ArrayList<>();
        ArrayList<BillItem> billData = new ArrayList<>();
        try {
            File billFile = new File(path);
            String charset = "UTF-8";
            if (path.contains("alipay")) charset = "GBK";
            InputStreamReader ir = new InputStreamReader(new FileInputStream(billFile), charset);
            BufferedReader br = new BufferedReader(ir);
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                billDataRaw.add(row);
            }
        }
        catch (FileNotFoundException e) {
            Log.ERROR("Cannot find the .CSV file. Please check your input file path");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // AliPay bill list
        if (billDataRaw.get(0)[0].contains("支付宝")) {
            // Remove headers
            billDataRaw.remove(0);
            billDataRaw.remove(0);
            for(String[] item : billDataRaw) {
                // Skip headers
                if(item.length < 10) {
                    break;
                }
                if(item[5].trim().equals("0.00")) {
                    continue;
                }
                String name = item[3].trim();
                String amount = item[5].trim();
                if(item[0].trim().equals("支出")) {
                    amount = "-" + amount;
                }
                else if(item[0].trim().equals("其他")){
                    continue;
                }
                String time = item[10].trim() + "+08";
                String from = "支付宝";
                String remarks = "";
                String account = item[4].trim();
                billData.add(new BillItem(name, amount, time, from, remarks, account, databaseID));
            }
        }
        // Wechat Pay bill list
        else {
            for (String[] item : billDataRaw) {
                // Skip headers
                if (item.length < 10) {
                    continue;
                }
                if (item[0].equals("交易时间")) {
                    continue;
                }
                String name = item[2] + " - " + item[3].substring(1, item[3].length() - 1);
                if(name.contains("一卡通充值")) {
                    // Skip depositing bill (included in ecard bill).
                    continue;
                }
                String amount = item[5].substring(1);
                if(item[4].equals("支出")) {
                    amount = "-" + amount;
                }
                String time = item[0] + "+08";
                String from = "微信";
                String remarks = item[10].substring(1, item[10].length() - 1).equals("/") ? "" : item[10].substring(1, item[10].length() - 1);
                String account = item[6].equals("/") ? "零钱" : item[6];
                billData.add(new BillItem(name, amount, time, from, remarks, account,databaseID));
            }
        }
        ArrayList<String> billItemList = new ArrayList<>();
        for (BillItem item : billData) {
            billItemList.add(JSON.toJSONString(item));
        }
        return billItemList;
    }

    /**
     * Load local config.
     */
    private void loadConfig() {
        StringBuilder sb = new StringBuilder();
        try{
            File config = new File("config.json");
            Reader reader = new InputStreamReader(new FileInputStream(config), StandardCharsets.UTF_8);
            int ch;
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
        } catch (IOException e) {
            Log.ERROR("Cannot find 'config.json'.");
        }
        parseConfig(sb.toString());
    }

    private void parseConfig(String config) {
        JSONObject configJson = JSON.parseObject(config);
        this.secretKey = configJson.getString("secret_key");
        this.notionVersion = configJson.getString("notion_version");
        this.databaseID = configJson.getString("database_id");
        this.college = configJson.getString("college");
        this.ecardAuth = configJson.getString("ecard_auth");
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getNotionVersion() {
        return notionVersion;
    }

    public String getDatabaseID() {
        return databaseID;
    }

    public String getCollege() {
        return college;
    }

    public String getEcardAuth() {
        return ecardAuth;
    }
}
