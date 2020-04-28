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
            System.out.println("app\terror parsing args");
            printUsage();
            System.exit(0);
        }
    }

    private static void printUsage() {
        System.out.println("\nUSAGE : \n");
        System.out.println("---");
        System.out.println("for ui mode :");
        System.out.println("\t\tui");
        System.out.println("for war mode :");
        System.out.println("\t\twar EngineType EngineType");
        System.out.println("for batch mode :");
        System.out.println("\t\tbatch\n");
        System.out.println("EngineType = [ MC | ANN3MRC | ANN4MRC | ANNMMRC | RULE | RANDOM | SUPER3M | SUPER4M | BATCH ]");
        System.out.println("for data mode :");
        System.out.println("\t\tdata");
        System.out.println("for train mode :");
        System.out.println("\t\ttrain");
        System.out.println("---\n");
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
    }
}
