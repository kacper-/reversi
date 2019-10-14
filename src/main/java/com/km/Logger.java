package com.km;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Logger {
    private static final String SERVER_MESSAGE_PATH = "http://wesolowo.pl:8080/msg-1.0-SNAPSHOT/api/message";
    private static boolean on = true;
    private static boolean webLog = false;
    private static LogLevel level = LogLevel.DEBUG;

    public static boolean isWebLog() {
        return webLog;
    }

    public static void setWebLog(boolean webLog) {
        if (!Logger.webLog)
            Logger.info(String.format("logger\tsetting web log to [%s]", Boolean.toString(webLog)));
        Logger.webLog = webLog;
        if (!Logger.webLog)
            Logger.info(String.format("logger\tsetting web log to [%s]", Boolean.toString(webLog)));
    }

    public static void setLevel(LogLevel logLevel) {
        Logger.info(String.format("logger\tsetting level to [%s]", logLevel.name()));
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
        if (on && level.ordinal() <= logLevel.ordinal()) {
            if (logLevel == LogLevel.ERROR)
                System.err.println(s);
            else
                System.out.println(s);
            if (webLog)
                message(String.format("[%s]\t%s", logLevel, s));
        }
    }

    private static void message(String message) {
        HttpURLConnection conn = null;
        String json = String.format("{\"type\": \"%s\", \"value\": \"%s\"}", "REVERSI", message.replaceAll("\t", "    "));
        try {
            URL url = new URL(SERVER_MESSAGE_PATH);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", String.valueOf(json.getBytes().length));
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(json);
            wr.flush();
            wr.close();
            conn.connect();
            if (conn.getResponseCode() > 204)
                System.err.println("\tweb log error");
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null)
                conn.disconnect();
        }
    }
}
