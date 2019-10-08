package com.km.game;

import com.km.LogLevel;
import com.km.Logger;
import com.km.engine.EngineType;
import com.km.nn.NetUtil;

import java.util.Random;
import java.util.Set;

public class GameRunner {
    private ScoreListener scoreListener;
    private UIListener uiListener;
    private GameController controller;
    private int warScoreB = 0;
    private int warScoreW = 0;

    public boolean isWarFinished() {
        return warFinished;
    }

    public boolean isBatchFinished() {
        return batchFinished;
    }

    private volatile boolean warFinished = false;
    private volatile boolean batchFinished = false;

    public boolean isPredefinedFinished() {
        return predefinedFinished;
    }

    private volatile boolean predefinedFinished = false;

    public GameController getGameController() {
        if (controller == null) {
            controller = new GameController();
            controller.getGameBoard().empty();
        }
        return controller;
    }

    public Set<Move> getAvailableMoves() {
        return GameRules.getAvailableMoves(getGameController().getGameBoard());
    }

    public void startBatchTrain(int cycleCount, int trainCycleLen, int testCycleLen) {
        batchFinished = false;
        new Thread(() -> {
            NetUtil.clear();
            LogLevel level = Logger.getLevel();
            Logger.info(String.format("board\tbatch train : cycles count [%d] : train cycle len [%d] : test cycle len [%d]", cycleCount, trainCycleLen, testCycleLen));
            Logger.setLevel(LogLevel.IMPORTANT);
            for (int i = 0; i < cycleCount; i++) {
                Logger.important(String.format("board\tcycle [%d] of [%d]", i + 1, cycleCount));
                runTrainingCycle(i, trainCycleLen);
                runWars(EngineType.ANN, EngineType.RANDOM, testCycleLen);
            }
            Logger.setLevel(level);
            Logger.info("board\tbatch train finished");
            notifyOnUI();
            batchFinished = true;
        }).start();
    }

    private void runTrainingCycle(int i, int trainCycleLen) {
        if (new Random().nextBoolean())
            runWars(EngineType.MC, EngineType.RANDOM, trainCycleLen);
        else
            runWars(EngineType.RANDOM, EngineType.MC, trainCycleLen);
    }

    public void startWarGame(EngineType typeB, EngineType typeW, int count) {
        warFinished = false;
        new Thread(() -> {
            runWars(typeB, typeW, count);
            notifyOnUI();
        }).start();
    }

    private void runWars(EngineType typeB, EngineType typeW, int count) {
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
        if (typeB == EngineType.MC || typeW == EngineType.MC)
            NetUtil.runTraining();
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


}
