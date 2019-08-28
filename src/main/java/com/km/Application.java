package com.km;

import com.km.nn.NetUtil;
import com.km.ui.frame.MainFrame;

import javax.swing.*;
import java.awt.*;

public class Application {
    private final static int WIDTH = 720;
    private final static int HEIGHT = 480;

    public static void main(String... args) {
        validateArgs(args);
        MainFrame frame = new MainFrame();
        frame.createComponents();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(frame.getExtendedState());
        frame.setVisible(true);
        frame.setSize(new Dimension(WIDTH, HEIGHT));
    }

    private static void validateArgs(String... args) {
        if (args == null || args.length == 0) {
            Logger.debug("No ANN path!");
            NetUtil.setFilePath(null);
            return;
        }
        NetUtil.setFilePath(args[0]);
    }
}
