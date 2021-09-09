/*
 * Copyright (c) 2021
 * User: Shuai
 * File: Log.java
 * Date: 2021/08/31 10:42:31
 */

package mygoshi;

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
