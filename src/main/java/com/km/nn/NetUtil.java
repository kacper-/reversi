package com.km.nn;

import com.km.Config;
import com.km.Logger;
import com.km.entities.Nodes;
import com.km.game.DBSlot;
import com.km.repos.GameService;
import com.km.repos.Repo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NetUtil {
    private static final double PRECISION = 0.1d;
    private Net net;
    private NetVersion version;
    private int trainCount;
    private String fileName;
    private String repoFileName;
    private Repo repo;
    private List<Nodes> localNodes;

    public NetUtil(NetVersion version, String name, String repoFileName) {
        this.version = version;
        this.fileName = Config.FILE_PATH + name;
        this.repoFileName = Config.FILE_PATH + repoFileName;
    }

    public void clear() {
        Logger.info(String.format("net\tclearing file [%s]", fileName));
        net = createInstance();
        save();
    }

    public void setLocalNodes() {
        this.localNodes = new ArrayList<>(getNodesMap().values());
        Nodes n;
        double[] x = new double[2];
        for (int i = 0; i < 100; i++) {
            n = localNodes.get(i);
            x[0] = n.getWins();
            x[1] = n.getLoses();
            System.out.println(String.format("%d %d %.2f %s", n.getWins(), n.getLoses(), net.expected(x), n.getBoard()));
        }
    }

    private Net createInstance() {
        switch (version) {
            case NET3M:
                return new NetM(NetVersion.NET3);
            case NET4M:
                return new NetM(NetVersion.NET4);
            case NETMM:
                return new NetM(NetVersion.NETMM);
            default:
                throw new IllegalArgumentException();
        }
    }

    public void save() {
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

    public void load() {
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
        trainNoValidate(n);
    }

    private void trainNoValidate(Nodes n) {
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
        for (Nodes node : nodes) train(node);
        int[] result = verify(nodes);
        Logger.info(String.format("net\ttraining accuracy : [%.2f] -> [%d %%, %d %%, %d %%, %d %%, %d %%, %d %%, %d %%, %d %%,] after [%d] iterations", PRECISION, result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7], trainCount));
        save();
        return result;
    }

    public int[] runTrainingFromLocalData(int from, int to) {
        trainCount = 0;
        List<Nodes> tmpNodes = new ArrayList<>();
        for (int i = from; i < to; i++) {
            trainNoValidate(localNodes.get(i));
            tmpNodes.add(localNodes.get(i));
        }
        return verify(tmpNodes);
    }

    private int[] verify(List<Nodes> nodes) {
        int[] count = new int[net.getSegments()];
        int[] result = new int[net.getSegments()];
        double actual, expected;
        int idx;
        for (Nodes node : nodes) {
            if (!validate(node))
                continue;
            actual = process(node.getBoard());
            expected = expected(node);
            idx = (net.getSize() - node.getCount(DBSlot.EMPTY)) / net.getSegments();
            if (inRange(expected, actual))
                result[idx]++;
            count[idx]++;
        }
        for (int i = 0; i < net.getSegments(); i++) {
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

    public void report() {
        Logger.info(String.format("trainCount = [%d]", trainCount));
        int[] r = net.report();
        for (int i = 0; i < net.getSegments(); i++)
            Logger.info(String.format("[%d] -> [%d]", i, r[i]));
    }

    public void saveRepo() {
        Logger.trace(String.format("net\tsaving data file [%s]", repoFileName));
        try {
            FileOutputStream fileOut = new FileOutputStream(repoFileName);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(repo);
            objectOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadRepo() {
        Logger.trace(String.format("net\tloading data file [%s]", repoFileName));
        if (!new File(repoFileName).exists()) {
            Logger.important(String.format("net\tdata file [%s] does not exist, creating new", repoFileName));
            clearRepo();
            saveRepo();
        } else {
            try {
                FileInputStream fileIn = new FileInputStream(repoFileName);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                repo = (Repo) objectIn.readObject();
                objectIn.close();
                Logger.important(String.format("net\tdata file size [%d]", repo.getNodesMap().size()));
            } catch (Exception e) {
                Logger.error(String.format("net\terror loading data file [%s]", repoFileName));
            }
        }
    }

    public void updateRepo(List<Nodes> nodes) {
        for (Nodes n : nodes) {
            if (validate(n))
                repo.addNodesList(n);
        }
    }

    public Map<String, Nodes> getNodesMap() {
        return repo.getNodesMap();
    }

    public void clearRepo() {
        repo = new Repo();
    }
}
