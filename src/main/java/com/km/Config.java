package com.km;

import com.km.nn.NetVersion;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
    public static String VERSION = "0.9";
    private static String fileName = "reversi.conf";
    private static String filePath = Paths.get(".").toAbsolutePath().normalize().toString() + File.separator;
    private static boolean headless = false;
    private static LogLevel level = LogLevel.INFO;
    private static int testLen = 100;
    private static int cycleCount = 300;
    private static int simCount = 12;
    private static double net2LearningFactor = 0.01d;
    private static double net3LearningFactor = 0.001d;
    private static double net4LearningFactor = 0.001d;
    private static NetVersion batchNetVersion = NetVersion.NET4;
    private static String batchNetFile = "BATCH";
    private static int simCountL1 = 10000;
    private static int simCountL2 = 2500;
    private static int simCountL3 = 200;
    private static int simCountL4 = 10;
    private static int simL2 = 53;
    private static int simL3 = 57;
    private static int simL4 = 60;
    private static int width = 950;
    private static int height = 1000;
    private static String engineANN3RCfile = "ANN3RC";
    private static String engineANN4RCfile = "ANN4RC";

    public static String getEngineANN3RCfile() {
        if (properties.getProperty("engine.ANN3RC.file") != null)
            return properties.getProperty("engine.ANN3RC.file");
        else
            return engineANN3RCfile;
    }

    public static String getEngineANN4RCfile() {
        if (properties.getProperty("engine.ANN4RC.file") != null)
            return properties.getProperty("engine.ANN4RC.file");
        else
            return engineANN4RCfile;
    }

    private static Properties properties;

    public static String getFileName() {
        return fileName;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static boolean isHeadless() {
        if (properties.getProperty("headless") != null)
            return properties.getProperty("headless").equalsIgnoreCase("true");
        else
            return headless;
    }

    public static LogLevel getLevel() {
        if (properties.getProperty("level") != null)
            return LogLevel.valueOf(properties.getProperty("level").toUpperCase());
        else
            return level;
    }

    public static int getTestLen() {
        if (properties.getProperty("test.len") != null)
            return Integer.parseInt(properties.getProperty("test.len"));
        else
            return testLen;
    }

    public static int getCycleCount() {
        if (properties.getProperty("cycle.count") != null)
            return Integer.parseInt(properties.getProperty("cycle.count"));
        else
            return cycleCount;
    }

    public static int getSimCount() {
        if (properties.getProperty("sim.count") != null)
            return Integer.parseInt(properties.getProperty("sim.count"));
        else
            return simCount;
    }

    public static double getNet2LearningFactor() {
        if (properties.getProperty("net2.learningfactor") != null)
            return Double.parseDouble(properties.getProperty("net2.learningfactor"));
        else
            return net2LearningFactor;
    }

    public static double getNet3LearningFactor() {
        if (properties.getProperty("net3.learningfactor") != null)
            return Double.parseDouble(properties.getProperty("net3.learningfactor"));
        else
            return net3LearningFactor;
    }

    public static double getNet4LearningFactor() {
        if (properties.getProperty("net4.learningfactor") != null)
            return Double.parseDouble(properties.getProperty("net4.learningfactor"));
        else
            return net4LearningFactor;
    }

    public static NetVersion getBatchNetVersion() {
        if (properties.getProperty("batch.net.version") != null)
            return NetVersion.valueOf(properties.getProperty("batch.net.version").toUpperCase());
        else
            return batchNetVersion;
    }

    public static String getBatchNetFile() {
        if (properties.getProperty("batch.net.file") != null)
            return properties.getProperty("batch.net.file");
        else
            return batchNetFile;
    }

    public static int getSimCountL1() {
        if (properties.getProperty("sim.count.l1") != null)
            return Integer.parseInt(properties.getProperty("sim.count.l1"));
        else
            return simCountL1;
    }

    public static int getSimCountL2() {
        if (properties.getProperty("sim.count.l2") != null)
            return Integer.parseInt(properties.getProperty("sim.count.l2"));
        else
            return simCountL2;
    }

    public static int getSimCountL3() {
        if (properties.getProperty("sim.count.l3") != null)
            return Integer.parseInt(properties.getProperty("sim.count.l3"));
        else
            return simCountL3;
    }

    public static int getSimCountL4() {
        if (properties.getProperty("sim.count.l4") != null)
            return Integer.parseInt(properties.getProperty("sim.count.l4"));
        else
            return simCountL4;
    }

    public static int getSimL2() {
        if (properties.getProperty("sim.l2") != null)
            return Integer.parseInt(properties.getProperty("sim.l2"));
        else
            return simL2;
    }

    public static int getSimL3() {
        if (properties.getProperty("sim.l3") != null)
            return Integer.parseInt(properties.getProperty("sim.l3"));
        else
            return simL3;
    }

    public static int getSimL4() {
        if (properties.getProperty("sim.l4") != null)
            return Integer.parseInt(properties.getProperty("sim.l4"));
        else
            return simL4;
    }

    public static int getWidth() {
        if (properties.getProperty("width") != null)
            return Integer.parseInt(properties.getProperty("width"));
        else
            return width;
    }

    public static int getHeight() {
        if (properties.getProperty("height") != null)
            return Integer.parseInt(properties.getProperty("height"));
        else
            return height;
    }

    public static void setProperties(Properties properties) {
        Config.properties = properties;
        for (Object o : properties.keySet()) {
            Logger.trace(String.format("app\tkey=[%s] value=[%s]", o.toString(), properties.getProperty(o.toString())));
        }
    }
}
