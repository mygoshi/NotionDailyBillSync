/*
 * Copyright (c) 2021
 * User: Shuai
 * File: Notion.java
 * Date: 2021/09/04 11:19:04
 */

package mygoshi;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Notion {

    /**
     * Push a record to Notion database.
     * @param billData Data to upload in JSON format.
     * @param secretKey Secret Key of Notion Integration.
     * @param notionVersion Version of Notion.
     */
    public void pushBill(String billData, String secretKey, String notionVersion){
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

}
