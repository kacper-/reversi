package com.km;

import com.km.nn.NetVersion;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
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
    private static long simTime = 1000;
    private static int simCountL1 = 10000;
    private static int simCountL2 = 2500;
    private static int simCountL3 = 200;
    private static int simCountL4 = 10;
    private static int simL2 = 53;
    private static int simL3 = 57;
    private static int simL4 = 60;
    private static int simHb = 100;
    private static int cores = Runtime.getRuntime().availableProcessors() < 3 ? 1 : Runtime.getRuntime().availableProcessors() - 2;
    private static int width = 950;
    private static int height = 1000;
    private static String engineANN2file = "NET2";
    private static String engineANN3file = "NET3";
    private static String engineANN4file = "NET4";
    private static String engineANN3RCfile = "ANN3RC";
    private static String engineANN4RCfile = "ANN4RC";

    public static String getEngineANN2file() {
        if (properties.contains("engine.ANN2.file"))
            return properties.getProperty("engine.ANN2.file");
        else
            return engineANN2file;
    }

    public static String getEngineANN3file() {
        if (properties.contains("engine.ANN3.file"))
            return properties.getProperty("engine.ANN3.file");
        else
            return engineANN3file;
    }

    public static String getEngineANN4file() {
        if (properties.contains("engine.ANN4.file"))
            return properties.getProperty("engine.ANN4.file");
        else
            return engineANN4file;
    }

    public static String getEngineANN3RCfile() {
        if (properties.contains("engine.ANN3RC.file"))
            return properties.getProperty("engine.ANN3RC.file");
        else
            return engineANN3RCfile;
    }

    public static String getEngineANN4RCfile() {
        if (properties.contains("engine.ANN4RC.file"))
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

    // TODO implement headless mode
    public static boolean isHeadless() {
        if (properties.contains("headless"))
            return properties.getProperty("headless").equalsIgnoreCase("true");
        else
            return headless;
    }

    public static LogLevel getLevel() {
        if (properties.contains("level"))
            return LogLevel.valueOf(properties.getProperty("level").toUpperCase());
        else
            return level;
    }

    public static int getTestLen() {
        if (properties.contains("test.len"))
            return Integer.parseInt(properties.getProperty("test.len"));
        else
            return testLen;
    }

    public static int getCycleCount() {
        if (properties.contains("cycle.count"))
            return Integer.parseInt(properties.getProperty("cycle.count"));
        else
            return cycleCount;
    }

    public static int getSimCount() {
        if (properties.contains("sim.count"))
            return Integer.parseInt(properties.getProperty("sim.count"));
        else
            return simCount;
    }

    public static double getNet2LearningFactor() {
        if (properties.contains("net2.learningfactor"))
            return Double.parseDouble(properties.getProperty("net2.learningfactor"));
        else
            return net2LearningFactor;
    }

    public static double getNet3LearningFactor() {
        if (properties.contains("net3.learningfactor"))
            return Double.parseDouble(properties.getProperty("net3.learningfactor"));
        else
            return net3LearningFactor;
    }

    public static double getNet4LearningFactor() {
        if (properties.contains("net4.learningfactor"))
            return Double.parseDouble(properties.getProperty("net4.learningfactor"));
        else
            return net4LearningFactor;
    }

    public static NetVersion getBatchNetVersion() {
        if (properties.contains("batch.net.version"))
            return NetVersion.valueOf(properties.getProperty("batch.net.version").toUpperCase());
        else
            return batchNetVersion;
    }

    public static String getBatchNetFile() {
        if (properties.contains("batch.net.file"))
            return properties.getProperty("batch.net.file");
        else
            return batchNetFile;
    }

    public static long getSimTime() {
        if (properties.contains("sim.time"))
            return Integer.parseInt(properties.getProperty("sim.time"));
        else
            return simTime;
    }

    public static int getSimCountL1() {
        if (properties.contains("sim.count.l1"))
            return Integer.parseInt(properties.getProperty("sim.count.l1"));
        else
            return simCountL1;
    }

    public static int getSimCountL2() {
        if (properties.contains("sim.count.l2"))
            return Integer.parseInt(properties.getProperty("sim.count.l2"));
        else
            return simCountL2;
    }

    public static int getSimCountL3() {
        if (properties.contains("sim.count.l3"))
            return Integer.parseInt(properties.getProperty("sim.count.l3"));
        else
            return simCountL3;
    }

    public static int getSimCountL4() {
        if (properties.contains("sim.count.l4"))
            return Integer.parseInt(properties.getProperty("sim.count.l4"));
        else
            return simCountL4;
    }

    public static int getSimL2() {
        if (properties.contains("sim.l2"))
            return Integer.parseInt(properties.getProperty("sim.l2"));
        else
            return simL2;
    }

    public static int getSimL3() {
        if (properties.contains("sim.l3"))
            return Integer.parseInt(properties.getProperty("sim.l3"));
        else
            return simL3;
    }

    public static int getSimL4() {
        if (properties.contains("sim.l4"))
            return Integer.parseInt(properties.getProperty("sim.l4"));
        else
            return simL4;
    }

    public static int getSimHb() {
        if (properties.contains("sim.hb"))
            return Integer.parseInt(properties.getProperty("sim.hb"));
        else
            return simHb;
    }

    public static int getCores() {
        if (properties.contains("cores"))
            return Integer.parseInt(properties.getProperty("cores"));
        else
            return cores;
    }

    public static int getWidth() {
        if (properties.contains("width"))
            return Integer.parseInt(properties.getProperty("width"));
        else
            return width;
    }

    public static int getHeight() {
        if (properties.contains("height"))
            return Integer.parseInt(properties.getProperty("height"));
        else
            return height;
    }

    public static void setProperties(Properties properties) {
        Config.properties = properties;
    }
}
