/*
 * Copyright (c) 2021
 * User: Shuai
 * File: Main.java
 * Date: 2021/08/31 09:18:31
 */

package com.mygoshi;

import com.mygoshi.EcardQueryTJU.EcardQueryTJU;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0 || args[0].equals("-h")) {
            showHelp();
        }
        else if (args[0].equals("-f")) {
            if (args.length < 2) {
                Log.ERROR("Please type the .CSV file path.");
            }
            pushFromFile(args[1]);
        }
        else if (args[0].equals("--ecard")) {
            if (args.length < 3) {
                Log.ERROR("Please type both start time and end time.");
            }
            pushEcardBill(args[1], args[2]);
        }
    }

    private static void showHelp() {
        // @TODO Help information.
        System.out.println("Help information is missing...");
        System.exit(0);
    }

    private static void pushFromFile(String path) {
        Notion notion = new Notion();
        DataLoader loader = new DataLoader();
        ArrayList<String> billList = loader.loadBillData(path);
        int size = billList.size();
        int index = 1;
        for (String billListItem : billList) {
            Log.INFO("Processing " + index++ +" of " + size + ".");
            notion.pushBill(billListItem, loader.getSecretKey(), loader.getNotionVersion());
        }
    }

    private static void pushEcardBill(String startTime, String stopTime) {
        Notion notion = new Notion();
        DataLoader loader = new DataLoader();
        ArrayList<String> billList = EcardQueryTJU.getEcardBill(loader.getEcardAuth(),
                startTime, stopTime, loader.getDatabaseID());
        int size = billList.size();
        int index = 1;
        for (String billListItem : billList) {
            Log.INFO("Processing " + index++ +" of " + size + ".");
            notion.pushBill(billListItem, loader.getSecretKey(), loader.getNotionVersion());
        }
    }
}
