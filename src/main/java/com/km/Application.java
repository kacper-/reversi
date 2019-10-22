package com.km;

import com.km.ui.frame.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.util.Properties;

public class Application {
    public static void main(String... args) {
        String configFile = Config.getFilePath() + Config.getFileName();
        loadConfig(configFile);
        if (Config.isHeadless())
            runHeadless(args);
        else
            runFrame();
    }

    private static void runHeadless(String[] args) {
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
        Logger.important("for war mode :");
        Logger.important("\t\tWAR EngineType EngineType");
        Logger.important("for batch mode :");
        Logger.important("\t\tBATCH\n");
        Logger.important("EngineType = [ MC | ANN3RC | ANN4RC | RULE | RANDOM | SUPER3 | SUPER4 | BATCH ]");
        Logger.important("---\n");
    }

    private static void runFrame() {
        MainFrame frame = new MainFrame();
        frame.createComponents();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(frame.getExtendedState());
        frame.setVisible(true);
        frame.setSize(new Dimension(Config.getWidth(), Config.getHeight()));
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
        Logger.important(String.format("app\tconfig loaded from [%s]", configFile));
    }
}
