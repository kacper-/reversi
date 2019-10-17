package com.km;

public class Logger {
    private static boolean on = true;
    private static boolean override = false;
    private static LogLevel level;

    public static void setOn() {
        on = true;
    }

    public static void setOff() {
        on = false;
    }

    public static void setLevel(LogLevel level) {
        override = true;
        Logger.level = level;
    }

    public static void setDefaultLevel() {
        override = false;
    }

    public static void debug(String s) {
        send(s, LogLevel.DEBUG);
    }

    public static void trace(String s) {
        send(s, LogLevel.TRACE);
    }

    public static void info(String s) {
        send(s, LogLevel.INFO);
    }

    public static void important(String s) {
        send(s, LogLevel.IMPORTANT);
    }

    public static void error(String s) {
        send(s, LogLevel.ERROR);
    }

    private static void send(String s, LogLevel logLevel) {
        if (on && getLevelOrdinal() <= logLevel.ordinal()) {
            if (logLevel == LogLevel.ERROR)
                System.err.println(s);
            else
                System.out.println(s);
        }
    }

    private static int getLevelOrdinal() {
        if (override)
            return level.ordinal();
        else
            return Config.getLevel().ordinal();
    }
}
