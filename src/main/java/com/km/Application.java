package com.km;

import java.io.FileInputStream;
import java.util.Properties;

public class Application {
    public static void main(String... args) {
        String configFile = Config.FILE_PATH + Config.FILE_NAME;
        loadConfig(configFile);
        runWrapper(args);
    }

    private static void runWrapper(String[] args) {
        GameRunnerWrapper wrapper = new GameRunnerWrapper();
        if (wrapper.parseArgs(args)) {
            wrapper.run();
        } else {
            Logger.error("app\terror parsing args");
            printUsage();
            System.exit(0);
        }
    }

    private static void printUsage() {
        Logger.important("\nUSAGE : \n");
        Logger.important("---");
        Logger.important("for ui mode :");
        Logger.important("\t\tui");
        Logger.important("for war mode :");
        Logger.important("\t\twar EngineType EngineType");
        Logger.important("for batch mode :");
        Logger.important("\t\tbatch\n");
        Logger.important("EngineType = [ MC | ANN3MRC | ANN4MRC | ANNMMRC | RULE | RANDOM | SUPER3M | SUPER4M | BATCH ]");
        Logger.important("for data mode :");
        Logger.important("\t\tdata");
        Logger.important("for train mode :");
        Logger.important("\t\ttrain");
        Logger.important("---\n");
    }

    private static void loadConfig(String configFile) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(configFile));
        } catch (Exception e) {
            System.out.println(String.format("Error reading config from [%s]", configFile));
            System.exit(0);
        }
        Config.setProperties(properties);
        Logger.trace(String.format("app\tconfig loaded from [%s]", configFile));
    }
}
