package com.km.nn;

import com.km.Config;
import com.km.Logger;
import com.km.entities.Nodes;
import com.km.game.DBSlot;
import com.km.repos.GameService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NetUtil {
    private static final double PRECISION = 0.1d;
    private Net net;
    private NetVersion version;
    private int trainCount;
    private String fileName;

    public NetUtil(NetVersion version, String name) {
        this.version = version;
        this.fileName = Config.getFilePath() + name;
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
            case NET4:
                return new Net4();
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
        return (n.getLoses() + n.getWins()) >= Config.getSimCount();
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
        int result = verify(nodes);
        Logger.info(String.format("net\ttraining accuracy : [%.2f] -> [%d %%] after [%d] iterations", PRECISION, result, trainCount));
        save();
        return result;
    }

    private int verify(List<Nodes> nodes) {
        int count = 0;
        int result = 0;
        double actual;
        double expected;
        for (int i = 0; i < nodes.size(); i++) {
            if (!validate(nodes.get(i)))
                continue;
            actual = process(nodes.get(i).getBoard());
            expected = expected(nodes.get(i));
            if (inRange(expected, actual))
                result++;
            count++;
        }
        result = (100 * result) / count;
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
}
