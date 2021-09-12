/*
 * Copyright (c) 2021
 * User: Shuai
 * File: Main.java
 * Date: 2021/09/09 22:17:09
 */

package com.mygoshi;

import com.mygoshi.EcardQueryTJU.EcardQueryTJU;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter your choice:\n" +
                    "1. Sync Wechat / Alipay bill.\n" +
                    "2. Sync student card bill.\n" +
                    "3. Exit.");
            int choice = scanner.nextInt();
            if(choice == 1) {
                pushFromFile();
            }
            else if(choice == 2) {
                pushEcardBill();
            }
            else if(choice == 3) {
                System.out.println("See you, fucking little bitch~");
                System.exit(0);
            }
        }
    }

    private static void showHelp() {
        // @TODO Help information.
        System.out.println("Help information is missing...");
        System.exit(0);
    }

    private static void pushFromFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please paste the file name here...");
        String path = scanner.nextLine();
        if (!(path.endsWith(".csv") || path.endsWith(".CSV"))) {
            path += ".csv";
        }
        File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
        String desktopPath = desktopDir.getAbsolutePath();
        path = desktopPath + "\\" + path;
        Notion notion = new Notion();
        DataLoader loader = new DataLoader();
        ArrayList<String> billList = loader.loadBillData(path);
        int size = billList.size();
        if (size == 0) {
            System.out.println("No bill found.");
        }
        int index = 1;
        for (String billListItem : billList) {
            Log.INFO("Processing " + index++ +" of " + size + ".");
            notion.pushBill(billListItem, loader.getSecretKey(), loader.getNotionVersion());
        }
    }

    private static void pushEcardBill() {
        Scanner scanner = new Scanner(System.in);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        String defaultDate = "" + calendar.get(Calendar.YEAR) +
                (calendar.get(Calendar.MONTH) + 1 < 10 ? "0" : "") + (calendar.get(Calendar.MONTH) + 1) +
                (calendar.get(Calendar.DATE) < 10 ? "0" : "") + calendar.get(Calendar.DATE);
        System.out.println("Please type start date...(Default: " + defaultDate + ")");
        String startTime = scanner.nextLine();
        if (startTime.equals("")) {
            startTime = defaultDate;
        }
        System.out.println("Please type end date...(Default: " + defaultDate + ")");
        String stopTime = scanner.nextLine();
        if (stopTime.equals("")) {
            stopTime = defaultDate;
        }
        System.out.println("Please type the auth token...");
        String authToken = scanner.nextLine();
        Notion notion = new Notion();
        DataLoader loader = new DataLoader();
        ArrayList<String> billList = EcardQueryTJU.getEcardBill(authToken,
                startTime, stopTime, loader.getDatabaseID());
        int size = billList.size();
        int index = 1;
        for (String billListItem : billList) {
            Log.INFO("Processing " + index++ +" of " + size + ".");
            notion.pushBill(billListItem, loader.getSecretKey(), loader.getNotionVersion());
        }
    }
}
