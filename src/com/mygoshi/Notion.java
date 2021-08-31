/*
 * Copyright (c) 2021
 * User: Shuai
 * File: Notion.java
 * Date: 2021/08/30 23:45:30
 */

package com.mygoshi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Notion {
    private final String secretKey;
    private final String notionVersion;
    private final String databaseID;

    public Notion() {
        String configText = readConfig();
        JSONObject configJson = JSON.parseObject(configText);
        this.secretKey = configJson.getString("secret_key");
        this.notionVersion = configJson.getString("notion_version");
        this.databaseID = configJson.getString("database_id");
    }

    /**
     * Push a record to Notion database.
     * @param billData Data to upload in JSON format.
     */
    public void pushBill(String billData){
        final HttpPost httpPost = new HttpPost("https://api.notion.com/v1/pages");
        Log.INFO("Uploading bill data please wait for a while...");
        httpPost.setHeader("Authorization", "Bearer " + secretKey);
        httpPost.setHeader("Notion-Version", notionVersion);
        httpPost.setHeader("Content-Type", "application/json");
        try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpEntity content = new StringEntity(billData, StandardCharsets.UTF_8);
            httpPost.setEntity(content);
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getCode() == 200) {
                Log.INFO("Upload success!");
            }
            else if (response.getCode() == 400) {
                Log.ERROR("Upload failed. Please check the structure of the .CSV file.");
            }
            else if (response.getCode() == 401) {
                Log.ERROR("Secret key incorrect.");
            }
            else if (response.getCode() == 404) {
                Log.ERROR("Page not found.");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read local config.
     * @return Config in String format.
     */
    private String readConfig() {
        try{
            File config = new File("config.json");
            Reader reader = new InputStreamReader(new FileInputStream(config), StandardCharsets.UTF_8);
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();
            return sb.toString();

        } catch (IOException e) {
            Log.ERROR("Cannot find 'config.json'.");
            return "";
        }
    }

    public String getDatabaseID() {
        return databaseID;
    }

}
