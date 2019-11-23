package com.km.nn;

import com.km.Config;
import com.km.Logger;
import com.km.entities.Nodes;
import com.km.game.DBSlot;
import com.km.repos.GameService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NetUtil {
    private static final double PRECISION = 0.1d;
    private static final int SEGMENTS = 8;
    private Net net;
    private NetVersion version;
    private int trainCount;
    private String fileName;

    public NetUtil(NetVersion version, String name) {
        this.version = version;
        this.fileName = Config.FILE_PATH + name;
    }

    public void clear() {
        Logger.info(String.format("net\tclearing file [%s]", fileName));
        net = createInstance();
        save();
    }

    Net createInstance() {
        switch (version) {
            case NET2:
                return new Net2();
            case NET3:
                return new Net3();
            case NET3M:
                return new NetM(NetVersion.NET3);
            case NET4:
                return new Net4();
            case NET4M:
                return new NetM(NetVersion.NET4);
        }
        return null;
    }

    private void save() {
        Logger.trace(String.format("net\tsaving file [%s]", fileName));
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(net);
            objectOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load() {
        Logger.trace(String.format("net\tloading file [%s]", fileName));
        if (!new File(fileName).exists()) {
            Logger.important(String.format("net\tfile [%s] does not exist, creating new", fileName));
            clear();
        } else {
            try {
                FileInputStream fileIn = new FileInputStream(fileName);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                net = (Net) objectIn.readObject();
                objectIn.close();
            } catch (Exception e) {
                Logger.error(String.format("net\terror loading file [%s]", fileName));
            }
        }
    }

    private void train(Nodes n) {
        if (!validate(n))
            return;
        net.teach(translate(n.getBoard()), expected(n));
        trainCount++;
    }

    private double expected(Nodes n) {
        return net.expected(new double[]{n.getWins(), n.getLoses()});
    }

    private boolean validate(Nodes n) {
        int sum = n.getLoses() + n.getWins();
        if (n.getCount(DBSlot.EMPTY) == 0)
            return false;
        return sum >= Config.getSimCount() && Math.abs(n.getLoses() - n.getWins()) > Config.getSimDiff();
    }

    public int[] runTraining() {
        load();
        trainCount = 0;
        Logger.trace("net\ttraining started...");
        List<Nodes> nodes = new ArrayList<>(GameService.getNodes());
        int count = nodes.size();
        for (int i = 0; i < count; i++) {
            train(nodes.get(ThreadLocalRandom.current().nextInt(count)));
        }
        int[] result = verify(nodes);
        Logger.info(String.format("net\ttraining accuracy : [%.2f] -> [%d %%, %d %%, %d %%, %d %%, %d %%, %d %%, %d %%, %d %%,] after [%d] iterations", PRECISION, result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7], trainCount));
        save();
        return result;
    }

    private int[] verify(List<Nodes> nodes) {
        int[] count = new int[SEGMENTS];
        int[] result = new int[SEGMENTS];
        double actual, expected;
        int idx;
        for (int i = 0; i < nodes.size(); i++) {
            if (!validate(nodes.get(i)))
                continue;
            actual = process(nodes.get(i).getBoard());
            expected = expected(nodes.get(i));
            idx = (net.getSize() - nodes.get(i).getCount(DBSlot.EMPTY)) / SEGMENTS;
            if (inRange(expected, actual))
                result[idx]++;
            count[idx]++;
        }
        for (int i = 0; i < SEGMENTS; i++) {
            if (count[i] > 0)
                result[i] = (100 * result[i]) / count[i];
        }
        return result;
    }

    private boolean inRange(double expected, double actual) {
        double down = 1 - PRECISION;
        double up = 1 + PRECISION;
        if (expected > 0) {
            return ((down * expected) < actual && (up * expected) > actual);
        } else {
            return ((down * expected) > actual && (up * expected) < actual);
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

    public String report() {
        return net.report();
    }
}
