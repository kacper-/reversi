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
    public static final int CYCLE_COUNT = 300;
    public static final NetVersion NET_VERSION = NetVersion.NET3;
    private static final int SIM_COUNT = 12;
    private static String filePath;
    private Net net;
    private NetVersion version;
    private int trainCount;

    public NetUtil(NetVersion version) {
        this.version = version;
    }

    public static void setFilePath(String filePath) {
        NetUtil.filePath = filePath;
    }

    public void clear() {
        Logger.info(String.format("net\tclearing file [%s]", filePath + version.name()));
        net = createInstance();
        save();
    }

    Net createInstance() {
        switch (version) {
            case NET2:
                return new Net2();
            case NET3:
                return new Net3();
            case NET4:
                return new Net4();
        }
        return null;
    }

    private void save() {
        Logger.trace(String.format("net\tsaving file [%s]", filePath + version.name()));
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath + version.name());
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(net);
            objectOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(String name) {
        Logger.trace(String.format("net\tloading file [%s]", filePath + name));
        if (!new File(filePath + version.name()).exists()) {
            Logger.trace(String.format("net\tfile [%s] does not exist, creating new", filePath + name));
            clear();
        } else {
            try {
                FileInputStream fileIn = new FileInputStream(filePath + name);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                net = (Net) objectIn.readObject();
                objectIn.close();
            } catch (Exception e) {
                Logger.error(String.format("net\terror loading file [%s]", filePath + name));
            }
        }
    }

    private void load() {
        load(version.name());
    }

    private void train(Nodes n) {
        if (!validate(n))
            return;
        net.teach(translate(n.getBoard()), new double[]{expected(n)});
        trainCount++;
    }

    private double expected(Nodes n) {
        double wins = n.getWins();
        double loses = n.getLoses();
        return net.expected(new double[]{wins, loses});
    }

    private boolean validate(Nodes n) {
        return (n.getLoses() + n.getWins()) >= SIM_COUNT;
    }

    public int runTraining() {
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

    private int verify(List<Nodes> nodes) {
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

    private boolean inRange(double expected, double actual, double precision) {
        double down = 1 - precision;
        double up = 1 + precision;
        if (expected > 0) {
            return (down * expected) < actual && (up * expected) > actual;
        } else {
            return (down * expected) > actual && (up * expected) < actual;
        }
    }

    public double process(String n) {
        if (net == null)
            load();
        double[] input = translate(n);
        return net.process(input);
    }

    private double[] translate(String n) {
        double[] input = new double[net.getSize()];
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
