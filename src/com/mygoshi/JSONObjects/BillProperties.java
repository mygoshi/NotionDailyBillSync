/*
 * Copyright (c) 2021
 * User: Shuai
 * File: BillProperties.java
 * Date: 2021/08/30 22:20:30
 */

package com.mygoshi.JSONObjects;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class BillProperties {

    @JSONField (name = "项目")
    private JSONObject name;

    @JSONField (name = "金额")
    private JSONObject amount;

    // @TODO Update tags.
//    @JSONField (name = "标签")
//    private String tags;

    @JSONField (name = "时间")
    private JSONObject time;

    @JSONField (name = "来源")
    private JSONObject from;

    @JSONField (name = "备注")
    private JSONObject remarks;

    @JSONField (name = "支付账户")
    private JSONObject account;

    public BillProperties(String name, String amount, String time, String from, String remarks, String account) {
        this.name = new JSONObject();
        this.name.put("title", JSON.parseArray(String.format("[{\"text\":{\"content\":\"%s\"}},]", name.replace('"', '\''))));

        this.amount = new JSONObject();
        this.amount.put("number", Double.parseDouble(amount));

        this.time = new JSONObject();
        this.time.put("date", JSON.parseObject(String.format("{\"start\":\"%s\"}", time)));

        this.from = new JSONObject();
        this.from.put("rich_text", JSON.parseArray(String.format("[{\"text\":{\"content\":\"%s\"}},]", from)));

        this.remarks = new JSONObject();
        this.remarks.put("rich_text", JSON.parseArray(String.format("[{\"text\":{\"content\":\"%s\"}},]", remarks.replace('"', '\''))));

        this.account = new JSONObject();
        this.account.put("rich_text", JSON.parseArray(String.format("[{\"text\":{\"content\":\"%s\"}},]", account)));
    }

    public JSONObject getName() {
        return name;
    }

    public JSONObject getAmount() {
        return amount;
    }

    public JSONObject getTime() {
        return time;
    }

    public JSONObject getFrom() {
        return from;
    }

    public JSONObject getRemarks() {
        return remarks;
    }

    public JSONObject getAccount() {
        return account;
    }
}
