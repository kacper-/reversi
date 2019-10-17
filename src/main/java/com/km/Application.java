package com.km;

import com.km.ui.frame.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.util.Properties;

public class Application {
    public static void main(String... args) {
        loadConfig(args);
        MainFrame frame = new MainFrame();
        frame.createComponents();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(frame.getExtendedState());
        frame.setVisible(true);
        frame.setSize(new Dimension(Config.getWidth(), Config.getHeight()));
    }

    private static void loadConfig(String... args) {
        String configFile = Config.getFilePath() + Config.getFileName();
        if (args.length > 0)
            configFile = args[0];
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
