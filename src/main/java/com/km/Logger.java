package com.km;

public class Logger {
    private static boolean on = true;

    public static void setDebugOn() {
        on = true;
    }

    public static void setDebugOff() {
        on = false;
    }

    public static void debug(String s) {
        if (on) {
            System.out.println(s);
        }
    }
}
