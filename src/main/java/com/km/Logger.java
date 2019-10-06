package com.km;

public class Logger {
    private static boolean on = true;
    private static LogLevel level = LogLevel.INFO;

    public static void setLevel(LogLevel logLevel) {
        Logger.info(String.format("logger\tsetting level to [%s]",logLevel.name()));
        level = logLevel;
    }

    public static LogLevel getLevel() {
        return level;
    }

    public static void setOn() {
        on = true;
    }

    public static void setOff() {
        on = false;
    }

    public static void debug(String s) {
        if (on && level.ordinal() <= LogLevel.DEBUG.ordinal()) {
            System.out.println(s);
        }
    }

    public static void trace(String s) {
        if (on && level.ordinal() <= LogLevel.TRACE.ordinal()) {
            System.out.println(s);
        }
    }

    public static void info(String s) {
        if (on && level.ordinal() <= LogLevel.INFO.ordinal()) {
            System.out.println(s);
        }
    }

    public static void important(String s) {
        if (on && level.ordinal() <= LogLevel.IMPORTANT.ordinal()) {
            System.out.println(s);
        }
    }

    public static void error(String s) {
        if (on && level.ordinal() <= LogLevel.ERROR.ordinal()) {
            System.err.println(s);
        }
    }
}
