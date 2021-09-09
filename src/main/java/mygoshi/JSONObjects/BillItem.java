/*
 * Copyright (c) 2021
 * User: Shuai
 * File: BillItem.java
 * Date: 2021/09/03 11:21:03
 */

package mygoshi.JSONObjects;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class BillItem {
    @JSONField(name = "properties")
    private final BillProperties properties;

    @JSONField(name = "parent")
    private final JSONObject parent;

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
