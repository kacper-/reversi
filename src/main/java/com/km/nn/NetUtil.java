package com.km.nn;

import com.km.Logger;
import com.km.entities.Nodes;
import com.km.game.DBSlot;
import com.km.repos.GameService;

import java.io.*;

public class NetUtil {
    private static final double TRAIN_T1 = 0.04d;
    private static final int TRAIN_T2 = 4;
    private static final int TRAIN_T3 = 5;
    private static String filePath;
    private static Net net;
    private static int trainCount;

    public static void setFilePath(String filePath) {
        NetUtil.filePath = filePath;
    }

    private static void save() {
        Logger.debug(String.format("net\tsaving file [%s]", filePath));
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(net);
            objectOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void load() {
        Logger.debug(String.format("net\tloading file [%s]", filePath));
        if (!new File(filePath).exists()) {
            Logger.debug(String.format("net\tfile [%s] does not exist, creating new", filePath));
            net = new Net();
            save();
        } else {
            try {
                FileInputStream fileIn = new FileInputStream(filePath);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                net = (Net) objectIn.readObject();
                objectIn.close();
            } catch (Exception e) {
                Logger.debug(String.format("net\terror loading file [%s]", filePath));
            }
        }
    }

    private static void train(Nodes start, Nodes end) {
        if (!validate(start, end))
            return;
        net.teach(translate(end.getBoard()), expected(end));
        trainCount++;
    }

    private static boolean validate(Nodes start, Nodes end) {
        if (Math.abs(ratio(start) - ratio(end)) < TRAIN_T1)
            return false;
        if ((start.getLoses() + start.getWins()) < TRAIN_T2)
            return false;
        return (end.getLoses() + end.getWins()) >= TRAIN_T2;
    }

    private static double expected(Nodes end) {
        return (2d * ratio(end)) - 1d;
    }

    private static double ratio(Nodes n) {
        double wins = n.getWins();
        double loses = n.getLoses();
        return 1 - (wins / (wins + loses));
    }

    public static void runTraining() {
        load();
        trainCount = 0;
        Logger.debug("net\ttraining started...");
        GameService.visitMovesRandomly(NetUtil::train, TRAIN_T3);
        Logger.debug(String.format("net\ttraining finished after [%d] iterations", trainCount));
        save();
    }

    public static double process(String n) {
        if (net == null)
            load();
        double[] input = translate(n);
        return net.process(input);
    }

    private static double[] translate(String n) {
        double[] input = new double[Net.SIZE];
        for (int i = 0; i < n.length(); i++) {
            DBSlot slot = DBSlot.fromSymbol(n.charAt(i));
            switch (slot) {
                case EMPTY:
                    input[i] = 0;
                    break;
                case CURRENT:
                    input[i] = 1;
                    break;
                case OTHER:
                    input[i] = -1;
                    break;
            }
        }
        return input;
    }
}
