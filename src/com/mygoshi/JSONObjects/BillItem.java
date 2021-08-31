/*
 * Copyright (c) 2021
 * User: Shuai
 * File: BillItem.java
 * Date: 2021/08/30 22:21:30
 */

package com.mygoshi.JSONObjects;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class BillItem {
    @JSONField(name = "properties")
    private BillProperties properties;

    @JSONField(name = "parent")
    private JSONObject parent;

    public BillItem(String name, String amount, String time, String from, String remarks, String account,
                    String databaseID) {
        properties = new BillProperties(name, amount, time, from, remarks, account);
        parent = new JSONObject();
        parent.put("database_id", databaseID);
    }

    public BillProperties getProperties() {
        return properties;
    }

    public JSONObject getParent() {
        return parent;
    }
}