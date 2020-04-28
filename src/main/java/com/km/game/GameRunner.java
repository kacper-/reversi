package com.km.game;

import com.km.Config;
import com.km.engine.EngineType;
import com.km.nn.NetUtil;
import com.km.repos.GameService;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameRunner {
    private static final int HIST_SIZE = 11;
    private final int[] histogram = new int[HIST_SIZE];
    private ScoreListener scoreListener;
    private UIListener uiListener;
    private GameController controller;
    private int warScoreB = 0;
    private int warScoreW = 0;
    private List<List<Integer>> progress;
    private NetUtil netUtil;
    private volatile boolean warFinished = false;
    private volatile boolean batchFinished = false;
    private volatile boolean dataFinished = false;
    private volatile boolean trainFinished = false;

    public GameController getGameController() {
        if (controller == null) {
            controller = new GameController();
            controller.getGameBoard().empty();
        }
        return controller;
    }

    private void clearHistogram() {
        for (int i = 0; i < HIST_SIZE; i++)
            histogram[i] = 0;
    }

    public boolean isWarFinished() {
        return warFinished;
    }

    public boolean isBatchFinished() {
        return batchFinished;
    }

    public boolean isDataFinished() {
        return dataFinished;
    }

    public boolean isTrainFinished() {
        return trainFinished;
    }

    public Set<Move> getAvailableMoves() {
        return GameRules.getAvailableMoves(getGameController().getGameBoard());
    }

    public void startData(int cycleCount) {
        dataFinished = false;
        netUtil = new NetUtil(Config.getBatchNetVersion(), Config.getBatchNetFile(), Config.getDataFile());
        netUtil.clearRepo();
        new Thread(() -> {
            progress = new ArrayList<>();
            System.out.println(String.format("data\tdata mode : cycles count [%d]", cycleCount));
            for (int i = 0; i < cycleCount; i++) {
                long start = new Date().getTime();
                runDataCycle();
                long middle = new Date().getTime();
                netUtil.updateRepo(GameService.getNodes());
                long end = new Date().getTime();
                System.out.println(String.format("%d g_size=%d\tg_time=%d\tr_time=%d", i + 1, netUtil.getLastCount(), (middle - start) / 1000, (end - middle) / 1000));
            }
            netUtil.saveRepo();
            System.out.println("data\tdata mode finished");
            dataFinished = true;
        }).start();
    }

    private void runDataCycle() {
        EngineType[] engineTypes = Config.getBatchTrainEngines();
        EngineType opp = engineTypes[ThreadLocalRandom.current().nextInt(engineTypes.length)];
        if (ThreadLocalRandom.current().nextBoolean())
            runWar(EngineType.MC, opp);
        else
            runWar(opp, EngineType.MC);
    }

    public void startTraining() {
        trainFinished = false;
        netUtil = new NetUtil(Config.getBatchNetVersion(), Config.getBatchNetFile(), Config.getDataFile());
        if (Config.isBatchClear())
            netUtil.clear();
        netUtil.loadRepo();
        new Thread(() -> {
            long start, stop;
            progress = new ArrayList<>();
            clearHistogram();
            System.out.println(String.format("train\ttrain mode : cycles count [%d]", netUtil.getNodesListCount()));
            int avg = 0;
            netUtil.load();
            for (int i = 0; i < netUtil.getNodesListCount(); i++) {
                start = new Date().getTime();
                int[] acc = netUtil.runTrainingFromLocalData(i);
                int wins = runWars(EngineType.BATCH, EngineType.RANDOM, Config.getTestLen());
                progress.add(Arrays.asList(acc[0], acc[1], acc[2], acc[3], acc[4], acc[5], acc[6], acc[7], wins));
                avg += wins;
                histogram[wins / 10]++;
                notifyOnTrainProgress();
                stop = new Date().getTime();
                System.out.println(String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", i + 1, wins, acc[0], acc[1], acc[2], acc[3], acc[4], acc[5], acc[6], acc[7], (stop - start) / 1000));
            }
            netUtil.save();
            System.out.println(String.format("train\ttraining finished with avg : [%d]", avg / netUtil.getNodesListCount()));
            report();
            trainFinished = true;
        }).start();
    }

    public void startBatchTrain(int cycleCount) {
        batchFinished = false;
        netUtil = new NetUtil(Config.getBatchNetVersion(), Config.getBatchNetFile(), Config.getDataFile());
        if (Config.isBatchClear())
            netUtil.clear();
        new Thread(() -> {
            progress = new ArrayList<>();
            clearHistogram();
            System.out.println(String.format("batch\tbatch train : cycles count [%d]", cycleCount));
            int avg = 0;
            for (int i = 0; i < cycleCount; i++) {
                long start = new Date().getTime();
                int[] acc = runTrainingCycle();
                int wins = runWars(EngineType.BATCH, EngineType.RANDOM, Config.getTestLen());
                progress.add(Arrays.asList(acc[0], acc[1], acc[2], acc[3], acc[4], acc[5], acc[6], acc[7], wins));
                avg += wins;
                histogram[wins / 10]++;
                notifyOnTrainProgress();
                long stop = new Date().getTime();
                System.out.println(String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d", i + 1, wins, acc[0], acc[1], acc[2], acc[3], acc[4], acc[5], acc[6], acc[7], (stop - start) / 1000));
            }
            System.out.println(String.format("batch\ttraining finished with avg : [%d]", avg / cycleCount));
            report();
            notifyOnUI();
            batchFinished = true;
        }).start();
    }

    private void report() {
        printHistogram();
        System.out.println("NET report : ");
        netUtil.report();
    }

    private void printHistogram() {
        System.out.println("Histogram : ");
        for (int i = 0; i < HIST_SIZE; i++)
            System.out.println(String.format("batch\thistogram [%d] -> [%d]", i * 10, histogram[i]));
    }

    private int[] runTrainingCycle() {
        EngineType[] engineTypes = Config.getBatchTrainEngines();
        EngineType opp = engineTypes[ThreadLocalRandom.current().nextInt(engineTypes.length)];
        if (ThreadLocalRandom.current().nextBoolean())
            runWar(EngineType.MC, opp);
        else
            runWar(opp, EngineType.MC);
        return netUtil.runTraining();
    }

    public void startWarGame(EngineType typeB, EngineType typeW, int count) {
        warFinished = false;
        new Thread(() -> {
            runWars(typeB, typeW, count);
            notifyOnUI();
        }).start();
    }

    private int runWars(EngineType typeB, EngineType typeW, int count) {
        warScoreW = 0;
        warScoreB = 0;
        for (int i = 0; i < count; i++) {
            runWar(typeB, typeW);
            notifyOnWarScore(i + 1, warScoreB, warScoreW);
        }
        warFinished = true;
        return Math.max(warScoreB, warScoreW);
    }

    public void playerMove(Move move) {
        if (!getGameController().isFinished()) {
            if (GameRules.getAvailableMoves(getGameController().getGameBoard()).contains(move)) {
                new Thread(() -> {
                    if (getGameController().makePlayerMove(move)) {
                        notifyOnUI();
                    }
                }).start();
                notifyOnUI();
            }
        }
    }

    private void runWar(EngineType typeB, EngineType typeW) {
        getGameController().startWarGame(typeB, typeW);
        while (!getGameController().isFinished()) {
            getGameController().makeWarMove();
            if (typeB == EngineType.MC || typeW == EngineType.MC)
                notifyOnUI();
        }
        if (getGameController().getScore().getWinner() == Slot.BLACK)
            warScoreB++;
        else
            warScoreW++;
    }

    public void startNewGame(Slot s, EngineType type) {
        getGameController().startNewGame(s, type);
        notifyOnUI();
    }


    public void setScoreListener(ScoreListener scoreListener) {
        this.scoreListener = scoreListener;
    }

    public void setUiListener(UIListener uiListener) {
        this.uiListener = uiListener;
    }

    private void notifyOnUI() {
        notifyOnScore();
        if (uiListener != null)
            uiListener.refreshUI();
    }

    private void notifyOnScore() {
        if (scoreListener != null && controller != null)
            scoreListener.setScore(controller.getScore());
    }

    private void notifyOnWarScore(int count, int black, int white) {
        if (scoreListener != null)
            scoreListener.setWarScore(count, black, white);
    }

    private void notifyOnTrainProgress() {
        if (scoreListener != null)
            scoreListener.setTrainProgress(progress);
    }
}
