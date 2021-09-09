/*
 * Copyright (c) 2021
 * User: Shuai
 * File: EcardQueryTJU.java
 * Date: 2021/09/04 15:41:04
 */

package mygoshi.EcardQueryTJU;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import mygoshi.JSONObjects.BillItem;
import mygoshi.Log;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EcardQueryTJU {
    public static ArrayList<String> getEcardBill(String ecardAuth, String startDate, String endDate, String databaseID) {
        ArrayList<String> billData = new ArrayList<>();
        double breakfastBill = 0, launchBill = 0, dinnerBill = 0;
        Date breakfastTime = null, launchTime = null, dinnerTime = null;
        SimpleDateFormat decodeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat encodeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String billDataRaw = getEcardBillRaw(ecardAuth, startDate, endDate);
        JSONObject billJson = JSON.parseObject(billDataRaw);
        JSONArray billDataArray = billJson.getObject("data", JSONObject.class)
                                          .getObject("data", JSONObject.class)
                                          .getJSONArray("list");
        int date = Integer.parseInt(endDate.substring(6));
        for(Object billItem : billDataArray) {
            JSONObject item = (JSONObject) billItem;
            double amount = Double.parseDouble(item.getString("amount"));
            if(amount > 0) {
                // Skip when depositing.
                continue;
            }
            Date time;
            try {
                time = decodeFormat.parse(item.getString("paytime"));
                int hour = time.getHours();
                int billDate = time.getDate();
                if (billDate != date) {
                    addBill(databaseID, billData, breakfastBill, launchBill, dinnerBill, breakfastTime,
                            launchTime, dinnerTime, encodeFormat);
                    breakfastBill = 0;
                    launchBill = 0;
                    dinnerBill = 0;
                    date = billDate;
                }
                if(hour > 6 && hour < 10) { // breakfast
                    breakfastBill += amount;
                    breakfastTime = time;
                }
                else if(hour >= 11 && hour < 13) {  // launch
                    launchBill += amount;
                    launchTime = time;
                }
                else if(hour >= 17 && hour < 19) {  // dinner
                    dinnerBill += amount;
                    dinnerTime = time;
                }
                else {
                    billData.add(JSON.toJSONString(new BillItem(item.getString("billname"), amount + "",
                            encodeFormat.format(time) + "+08",
                            "校园卡流水", "",
                            "校园卡余额", databaseID)));
                }
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
        addBill(databaseID, billData, breakfastBill, launchBill, dinnerBill, breakfastTime,
                launchTime, dinnerTime, encodeFormat);
        return billData;
    }

    private static void addBill(String databaseID, ArrayList<String> billData, double breakfastBill,
                                double launchBill, double dinnerBill, Date breakfastTime, Date launchTime,
                                Date dinnerTime, SimpleDateFormat encodeFormat) {
        if(breakfastBill != 0) {
            billData.add(JSON.toJSONString(new BillItem("早餐", breakfastBill + "",
                    encodeFormat.format(breakfastTime) + "+08",
                    "校园卡流水", "早饭",
                    "校园卡余额", databaseID)));
        }
        if(launchBill != 0) {
            billData.add(JSON.toJSONString(new BillItem("午餐", launchBill + "",
                    encodeFormat.format(launchTime) + "+08",
                    "校园卡流水", "午饭",
                    "校园卡余额", databaseID)));
        }
        if(dinnerBill != 0) {
            billData.add(JSON.toJSONString(new BillItem("晚餐", dinnerBill + "",
                    encodeFormat.format(dinnerTime) + "+08",
                    "校园卡流水", "晚饭",
                    "校园卡余额", databaseID)));
        }
    }

    private static String getEcardBillRaw(String ecardAuth, String startDate, String endDate) {
        HttpPost httpPost = new HttpPost("https://ecard.tju.edu.cn/openservice/miniprogram/getbilldata");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI MiniProgramEnv/Windows WindowsWechat");
        httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
        httpPost.setHeader("sw-Authorization", ecardAuth);
        try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String postData = String.format("{" +
                    "\"startdate\":\"%s\"," +
                    "\"enddate\":\"%s\"," +
                    "\"pageno\":1," +
                    "\"pagesize\":1000}", startDate, endDate);
            HttpEntity content = new StringEntity(postData, StandardCharsets.UTF_8);
            httpPost.setEntity(content);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getCode() == 200) {
                Log.INFO("Successfully got the bill data.");
            }
            if (response.getCode() == 403) {
                Log.ERROR("Please update \"ecard_auth\" in \"config.json\".");
            }
            return EntityUtils.toString(response.getEntity());
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
