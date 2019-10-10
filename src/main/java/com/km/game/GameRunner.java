package com.km.game;

import com.km.LogLevel;
import com.km.Logger;
import com.km.engine.EngineType;
import com.km.nn.NetUtil;

import java.util.*;

public class GameRunner {
    private static final int TEST_LEN = 100;
    private static final int HIST_SIZE = 10;
    private ScoreListener scoreListener;
    private UIListener uiListener;
    private GameController controller;
    private int warScoreB = 0;
    private int warScoreW = 0;
    private List<List<Integer>> progress;
    private int[] histogram = new int[HIST_SIZE];
    private NetUtil netUtil = new NetUtil(NetUtil.NET_VERSION);
    private volatile boolean warFinished = false;
    private volatile boolean batchFinished = false;

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

    public Set<Move> getAvailableMoves() {
        return GameRules.getAvailableMoves(getGameController().getGameBoard());
    }

    public void startBatchTrain(int cycleCount) {
        batchFinished = false;
        netUtil.clear();
        new Thread(() -> {
            progress = new ArrayList<>();
            clearHistogram();
            LogLevel level = Logger.getLevel();
            Logger.info(String.format("batch\tbatch train : cycles count [%d]", cycleCount));
            Logger.setLevel(LogLevel.IMPORTANT);
            int avg = 0;
            for (int i = 0; i < cycleCount; i++) {
                Logger.important(String.format("batch\tcycle [%d] of [%d]", i + 1, cycleCount));
                int acc = runTrainingCycle();
                int wins = runWars(EngineType.ANN3, EngineType.RANDOM, TEST_LEN);
                progress.add(Arrays.asList(acc, wins));
                avg += wins;
                histogram[wins / 10]++;
                notifyOnTrainProgress();
            }
            Logger.setLevel(level);
            Logger.info(String.format("batch\ttraining finished with avg : [%d]", avg / cycleCount));
            printHistogram();
            notifyOnUI();
            batchFinished = true;
        }).start();
    }

    private void printHistogram() {
        for (int i = 0; i < HIST_SIZE; i++)
            Logger.info(String.format("batch\thistogram [%d] -> [%d]", i * HIST_SIZE, histogram[i]));
    }

    private int runTrainingCycle() {
        if (new Random().nextBoolean())
            return runWar(EngineType.MC, EngineType.RANDOM);
        else
            return runWar(EngineType.RANDOM, EngineType.MC);
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
            Logger.info(String.format("board\tstarting war game [%d] of [%d]", i + 1, count));
            runWar(typeB, typeW);
            notifyOnWarScore(i + 1, warScoreB, warScoreW);
            Logger.info(String.format("board\tcurrent war score [%s] [%d] : [%s] [%d]", typeB.name(), warScoreB, typeW.name(), warScoreW));
        }
        Logger.important(String.format("board\tfinal war score [%s] [%d] : [%s] [%d]", typeB.name(), warScoreB, typeW.name(), warScoreW));
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

    private int runWar(EngineType typeB, EngineType typeW) {
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
        if (typeB == EngineType.MC || typeW == EngineType.MC) {
            return netUtil.runTraining();
        }
        return 0;
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
        scoreListener.setTrainProgress(progress);
    }
}
