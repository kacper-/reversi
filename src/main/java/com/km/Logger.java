package com.km;

public class Logger {
    public static final int DEBUG = 1;
    public static final int TRACE = 2;
    public static final int INFO = 3;
    public static final int IMPORTANT = 4;
    public static final int ERROR = 5;

    private static boolean on = true;
    private static int level = INFO;

    public static void setLevel(int logLevel) {
        level = logLevel;
    }

    public static int getLevel() {
        return level;
    }

    public static void setOn() {
        on = true;
    }

    public static void setOff() {
        on = false;
    }

    public static void debug(String s) {
        if (on && level <= DEBUG) {
            System.out.println(s);
        }
    }

    public static void trace(String s) {
        if (on && level <= TRACE) {
            System.out.println(s);
        }
    }

    public static void info(String s) {
        if (on && level <= INFO) {
            System.out.println(s);
        }
    }

    public static void important(String s) {
        if (on && level <= IMPORTANT) {
            System.out.println(s);
        }
    }

    public static void error(String s) {
        if (on && level <= ERROR) {
            System.err.println(s);
        }
    }
}
