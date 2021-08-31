/*
 * Copyright (c) 2021
 * User: Shuai
 * File: DataLoader.java
 * Date: 2021/08/30 23:31:30
 */

package com.mygoshi;

import com.alibaba.fastjson.JSON;
import com.mygoshi.JSONObjects.BillItem;

import java.io.*;
import java.util.ArrayList;

public class DataLoader {

    /**
     * Load and parse bill list.
     * @param path Path of .CSV file
     * @param databaseID ID of bill list database in Notion
     * @return List of bill items.
     */
    public static ArrayList<String> loadData(String path, String databaseID) {
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
                if(item[0].equals("支出")) {
                    amount = "-" + amount;
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
}
