/*
 * Copyright (c) 2021
 * User: Shuai
 * File: Log.java
 * Date: 2021/09/09 21:09:09
 */

package com.mygoshi;

public class Log {
    //@TODO Print with color.
    public static void INFO(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void WARN(String message) {
        System.out.println("[WARNING] " + message);
    }

    public static void ERROR(String message) {
        System.out.println("[ERROR] " + message);
        System.exit(-1);
    }
}
