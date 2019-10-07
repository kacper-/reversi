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
    private static final int SIM_COUNT = 12;
    static final boolean USE_DECAY = true;
    static final boolean USE_DROPOUT = true;
    public static final int CYCLE_COUNT = 50;
    public static final int TRAIN_CYCLE_LEN = 1;
    public static final int TEST_CYCLE_LEN = 1000;
    private static String filePath;
    private static Net net;
    private static int trainCount;

    public static void setFilePath(String filePath) {
        NetUtil.filePath = filePath;
    }

    public static void clear() {
        Logger.info(String.format("net\tclearing file [%s]", filePath));
        net = createNet();
        save();
    }

    public static Net createNet() {
        return createInstance(NetVersion.NET3);
    }

    public static Net createNet(NetVersion version) {
        return createInstance(version);
    }

    private static Net createInstance(NetVersion version) {
        switch (version) {
            case NET2:
                return new Net2(USE_DECAY, USE_DROPOUT);
            case NET3:
                return new Net3(USE_DECAY, USE_DROPOUT);
            case NET4:
                return new Net4(USE_DECAY, USE_DROPOUT);
        }
        return null;
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
            clear();
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

    private static void train(Nodes n, double decay) {
        if (!validate(n))
            return;
        net.teach(translate(n.getBoard()), ratio(n), decay);
        trainCount++;
    }

    private static boolean validate(Nodes n) {
        return (n.getLoses() + n.getWins()) >= SIM_COUNT;
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
        int dCount = count + (count / 10);
        double decay;
        for (int i = 0; i < count; i++) {
            decay = ((double) (dCount - i) / (double) dCount);
            train(nodes.get(new Random().nextInt(count)), decay);
        }
        Logger.info(String.format("net\ttraining finished after [%d] iterations", trainCount));
        verify(nodes);
        save();
    }

    private static void verify(List<Nodes> nodes) {
        int count = 0;
        int result = 0;
        double actual, expected;
        for (int i = 0; i < nodes.size(); i++) {
            if (!validate(nodes.get(i)))
                continue;
            actual = process(nodes.get(i).getBoard());
            expected = ratio(nodes.get(i));
            if (inRange(expected, actual))
                result++;
            count++;
        }
        Logger.info(String.format("net\ttraining accuracy [%d %%]", (100 * result) / count));
    }

    private static boolean inRange(double expected, double actual) {
        if (expected > 0) {
            return (0.75 * expected) < actual && (1.25 * expected) > actual;
        } else {
            return (0.75 * expected) > actual && (1.25 * expected) < actual;
        }
    }

    public static double process(String n) {
        if (net == null)
            load();
        double[] input = translate(n);
        return net.process(input);
    }

    private static double[] translate(String n) {
        double[] input = new double[Net4.SIZE];
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
