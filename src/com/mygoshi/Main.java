/*
 * Copyright (c) 2021
 * User: Shuai
 * File: Main.java
 * Date: 2021/08/31 09:18:31
 */

package com.mygoshi;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Notion notion = new Notion();
        ArrayList<String> billList = DataLoader.loadData(args[0], notion.getDatabaseID());
        for (String billListItem : billList) {
            notion.pushBill(billListItem);
        }
    }
}
