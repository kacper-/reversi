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
    private static final boolean USE_DROPOUT = true;
    static final NetVersion DEFAULT_NET_VER = NetVersion.NET3;
    public static final int CYCLE_COUNT = 200;
    private static String filePath;
    private static Net net;
    private static int trainCount;

    public static void setFilePath(String filePath) {
        NetUtil.filePath = filePath;
    }

    public static void clear() {
        Logger.info(String.format("net\tclearing file [%s]", filePath));
        net = createNet(DEFAULT_NET_VER);
        save();
    }

    static Net createNet(NetVersion version) {
        return createInstance(version);
    }

    private static Net createInstance(NetVersion version) {
        switch (version) {
            case NET2:
                return new Net2(USE_DROPOUT);
            case NET3:
                return new Net3(USE_DROPOUT);
            case NET4:
                return new Net4(USE_DROPOUT);
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

    private static void train(Nodes n) {
        if (!validate(n))
            return;
        net.teach(translate(n.getBoard()), expected(n));
        trainCount++;
    }

    private static double expected(Nodes n) {
        double wins = n.getWins();
        double loses = n.getLoses();
        if (loses > wins) {
            return -(1d - (wins / loses));
        } else {
            return 1d - (loses / wins);
        }
    }

    private static boolean validate(Nodes n) {
        return (n.getLoses() + n.getWins()) >= SIM_COUNT;
    }

    public static int runTraining() {
        load();
        trainCount = 0;
        Logger.trace("net\ttraining started...");
        List<Nodes> nodes = new ArrayList<>(GameService.getNodes());
        int count = nodes.size();
        for (int i = 0; i < count; i++) {
            train(nodes.get(new Random().nextInt(count)));
        }
        Logger.info(String.format("net\ttraining finished after [%d] iterations", trainCount));
        int result = verify(nodes);
        save();
        return result;
    }

    private static int verify(List<Nodes> nodes) {
        int count = 0;
        int result = 0;
        double actual, expected;
        for (int i = 0; i < nodes.size(); i++) {
            if (!validate(nodes.get(i)))
                continue;
            actual = process(nodes.get(i).getBoard());
            expected = expected(nodes.get(i));
            if (inRange(expected, actual, 0.1))
                result++;
            count++;
        }
        result = (100 * result) / count;
        Logger.important(String.format("net\ttraining accuracy : 0.10 -> [%d %%]", result));
        return result;
    }

    private static boolean inRange(double expected, double actual, double precision) {
        double down = 1 - precision;
        double up = 1 + precision;
        if (expected > 0) {
            return (down * expected) < actual && (up * expected) > actual;
        } else {
            return (down * expected) > actual && (up * expected) < actual;
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
