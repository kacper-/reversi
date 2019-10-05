package com.km.nn;

import com.km.Logger;
import com.km.entities.Nodes;
import com.km.game.DBSlot;
import com.km.repos.GameService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NetUtil {
    private static final int SIM_COUNT = 10;
    static final boolean USE_DECAY = false;
    static final boolean USE_DROPOUT = true;
    public static final int CYCLE_COUNT = 50;
    public static final int TRAIN_CYCLE_LEN = 3;
    public static final int TEST_CYCLE_LEN = 1000;
    private static String filePath;
    private static Net net;
    private static int trainCount;

    public static void setFilePath(String filePath) {
        NetUtil.filePath = filePath;
    }

    public static void clear() {
        Logger.info(String.format("net\tclearing file [%s]", filePath));
        net = new Net(USE_DECAY, USE_DROPOUT);
        save();
    }

    private static void save() {
        Logger.trace(String.format("net\tsaving file [%s]", filePath));
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
        Logger.trace(String.format("net\tloading file [%s]", filePath));
        if (!new File(filePath).exists()) {
            Logger.trace(String.format("net\tfile [%s] does not exist, creating new", filePath));
            net = new Net(USE_DECAY, USE_DROPOUT);
            save();
        } else {
            try {
                FileInputStream fileIn = new FileInputStream(filePath);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                net = (Net) objectIn.readObject();
                objectIn.close();
            } catch (Exception e) {
                Logger.error(String.format("net\terror loading file [%s]", filePath));
            }
        }
    }

    private static void train(Nodes n, int i, int count) {
        if (!validate(n))
            return;
        net.teach(translate(n.getBoard()), expected(n), i, count);
        trainCount++;
    }

    private static boolean validate(Nodes n) {
        return (n.getLoses() + n.getWins()) >= SIM_COUNT;
    }

    private static double expected(Nodes end) {
        return (2d * Math.sqrt(ratio(end))) - 1d;
    }

    private static double ratio(Nodes n) {
        double wins = n.getWins();
        double loses = n.getLoses();
        return wins / (wins + loses);
    }

    public static void runTraining() {
        load();
        trainCount = 0;
        Logger.trace("net\ttraining started...");
        List<Nodes> nodes = new ArrayList<>(GameService.getNodes());
        int count = nodes.size();
        for (int i = 0; i < count; i++) {
            train(nodes.get(new Random().nextInt(count)), i, count);
        }
        Logger.info(String.format("net\ttraining finished after [%d] iterations", trainCount));
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
