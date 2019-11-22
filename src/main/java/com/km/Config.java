package com.km;

import com.km.engine.EngineType;
import com.km.nn.NetVersion;
import com.km.nn.ac.ACType;
import com.km.nn.dropout.DOType;

import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
    public static final String VERSION = "0.9";
    public static final String FILE_NAME = "reversi.conf";
    public static final String FILE_PATH = Paths.get(".").toAbsolutePath().normalize().toString() + File.separator;
    private static Properties properties;

    public static String getEngineANN3RCfile() {
        if (properties.getProperty("engine.ANN3RC.file") != null)
            return properties.getProperty("engine.ANN3RC.file");
        else
            throw new IllegalArgumentException();
    }

    public static String getEngineANN3MRCfile() {
        if (properties.getProperty("engine.ANN3MRC.file") != null)
            return properties.getProperty("engine.ANN3MRC.file");
        else
            throw new IllegalArgumentException();
    }

    public static String getEngineANN4RCfile() {
        if (properties.getProperty("engine.ANN4RC.file") != null)
            return properties.getProperty("engine.ANN4RC.file");
        else
            throw new IllegalArgumentException();
    }

    public static String getEngineANN4MRCfile() {
        if (properties.getProperty("engine.ANN4MRC.file") != null)
            return properties.getProperty("engine.ANN4MRC.file");
        else
            throw new IllegalArgumentException();
    }

    public static boolean isHeadless() {
        if (properties.getProperty("headless") != null)
            return properties.getProperty("headless").equalsIgnoreCase("true");
        else
            throw new IllegalArgumentException();
    }

    public static LogLevel getLevel() {
        if (properties.getProperty("level") != null)
            return LogLevel.valueOf(properties.getProperty("level").toUpperCase());
        else
            throw new IllegalArgumentException();
    }

    public static int getTestLen() {
        if (properties.getProperty("test.len") != null)
            return Integer.parseInt(properties.getProperty("test.len"));
        else
            throw new IllegalArgumentException();
    }

    public static int getCycleCount() {
        if (properties.getProperty("cycle.count") != null)
            return Integer.parseInt(properties.getProperty("cycle.count"));
        else
            throw new IllegalArgumentException();
    }

    public static int getSimCount() {
        if (properties.getProperty("sim.count") != null)
            return Integer.parseInt(properties.getProperty("sim.count"));
        else
            throw new IllegalArgumentException();
    }

    public static int getSimDiff() {
        if (properties.getProperty("sim.diff") != null)
            return Integer.parseInt(properties.getProperty("sim.diff"));
        else
            throw new IllegalArgumentException();
    }

    public static double getNet2LearningFactor() {
        if (properties.getProperty("net2.learningfactor") != null)
            return Double.parseDouble(properties.getProperty("net2.learningfactor"));
        else
            throw new IllegalArgumentException();
    }

    public static double getNet3LearningFactor() {
        if (properties.getProperty("net3.learningfactor") != null)
            return Double.parseDouble(properties.getProperty("net3.learningfactor"));
        else
            throw new IllegalArgumentException();
    }

    public static double getNet4LearningFactor() {
        if (properties.getProperty("net4.learningfactor") != null)
            return Double.parseDouble(properties.getProperty("net4.learningfactor"));
        else
            throw new IllegalArgumentException();
    }

    public static ACType getNetActivationFunction() {
        if (properties.getProperty("net.activation") != null)
            return ACType.valueOf(properties.getProperty("net.activation"));
        else
            throw new IllegalArgumentException();
    }

    public static DOType getDropOutFunction() {
        if (properties.getProperty("dropout.function") != null)
            return DOType.valueOf(properties.getProperty("dropout.function"));
        else
            throw new IllegalArgumentException();
    }

    public static EngineType[] getBatchTrainEngines() {
        if (properties.getProperty("batch.train.engines") != null) {
            String cs = properties.getProperty("batch.train.engines").toUpperCase();
            String[] s = cs.split(",");
            EngineType[] engineTypes = new EngineType[s.length];
            for (int i = 0; i < s.length; i++) {
                engineTypes[i] = EngineType.valueOf(s[i].trim());
            }
            return engineTypes;
        } else
            throw new IllegalArgumentException();
    }

    public static NetVersion getBatchNetVersion() {
        if (properties.getProperty("batch.net.version") != null)
            return NetVersion.valueOf(properties.getProperty("batch.net.version").toUpperCase());
        else
            throw new IllegalArgumentException();
    }

    public static String getBatchNetFile() {
        if (properties.getProperty("batch.net.file") != null)
            return properties.getProperty("batch.net.file");
        else
            throw new IllegalArgumentException();
    }

    public static boolean isBatchClear() {
        if (properties.getProperty("batch.clear") != null)
            return Boolean.parseBoolean(properties.getProperty("batch.clear"));
        else
            throw new IllegalArgumentException();
    }

    public static int getSimCountL0() {
        if (properties.getProperty("sim.count.l0") != null)
            return Integer.parseInt(properties.getProperty("sim.count.l0"));
        else
            throw new IllegalArgumentException();
    }

    public static int getSimCountL1() {
        if (properties.getProperty("sim.count.l1") != null)
            return Integer.parseInt(properties.getProperty("sim.count.l1"));
        else
            throw new IllegalArgumentException();
    }

    public static int getSimCountL2() {
        if (properties.getProperty("sim.count.l2") != null)
            return Integer.parseInt(properties.getProperty("sim.count.l2"));
        else
            throw new IllegalArgumentException();
    }

    public static int getSimCountL3() {
        if (properties.getProperty("sim.count.l3") != null)
            return Integer.parseInt(properties.getProperty("sim.count.l3"));
        else
            throw new IllegalArgumentException();
    }

    public static int getSimCountL4() {
        if (properties.getProperty("sim.count.l4") != null)
            return Integer.parseInt(properties.getProperty("sim.count.l4"));
        else
            throw new IllegalArgumentException();
    }


    public static int getSimCountL5() {
        if (properties.getProperty("sim.count.l5") != null)
            return Integer.parseInt(properties.getProperty("sim.count.l5"));
        else
            throw new IllegalArgumentException();
    }

    public static int getSimCountL6() {
        if (properties.getProperty("sim.count.l6") != null)
            return Integer.parseInt(properties.getProperty("sim.count.l6"));
        else
            throw new IllegalArgumentException();
    }

    public static int getSimCountL7() {
        if (properties.getProperty("sim.count.l7") != null)
            return Integer.parseInt(properties.getProperty("sim.count.l7"));
        else
            throw new IllegalArgumentException();
    }

    public static int getWidth() {
        if (properties.getProperty("width") != null)
            return Integer.parseInt(properties.getProperty("width"));
        else
            throw new IllegalArgumentException();
    }

    public static int getHeight() {
        if (properties.getProperty("height") != null)
            return Integer.parseInt(properties.getProperty("height"));
        else
            throw new IllegalArgumentException();
    }

    public static void setProperties(Properties properties) {
        Config.properties = properties;
        for (Object o : properties.keySet()) {
            Logger.trace(String.format("app\tkey=[%s] value=[%s]", o.toString(), properties.getProperty(o.toString())));
        }
    }
}
