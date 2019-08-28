package com.km.game;

import com.km.Logger;
import com.km.engine.EngineFactory;
import com.km.engine.EngineType;
import com.km.engine.MoveEngine;
import com.km.nn.NetUtil;
import com.km.repos.GameService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class GameController {
    private boolean gameSaved = false;
    private GameBoard gameBoard = new GameBoard();
    private List<HistoryItem> historyBlack;
    private List<HistoryItem> historyWhite;
    private MoveEngine moveEngine;
    private int simCount = 0;
    private int wins = 0;
    private int loses = 0;
    private boolean simulation;
    private GameController controllerB;
    private GameController controllerW;

    public GameController copy() {
        GameController controller = new GameController();
        controller.gameSaved = gameSaved;
        controller.gameBoard = gameBoard.copy();
        controller.historyBlack = historyBlack.stream().map(HistoryItem::copy).collect(Collectors.toList());
        controller.historyWhite = historyWhite.stream().map(HistoryItem::copy).collect(Collectors.toList());
        controller.moveEngine = moveEngine;
        controller.simCount = simCount;
        controller.wins = wins;
        controller.loses = loses;
        controller.simulation = simulation;
        return controller;
    }

    public boolean isSimulation() {
        return simulation;
    }

    public void startNewGame(Slot human, EngineType type) {
        simulation = false;
        if (type == EngineType.TREE)
            Logger.debug(String.format("game\tpreparing for [%d] cores game", Tuning.CORES));
        Logger.debug(String.format("game\thuman plays [%s]", human.name()));
        Logger.debug(String.format("game\topponent type [%s]", type.name()));
        GameService.clear();
        prepareMoveEngine(type);
        GameRules.initGameBoard(gameBoard);
        initHistory();
        if (gameBoard.getTurn() != human) {
            makeMove();
        }
        gameSaved = false;
    }

    public void startWarGame(EngineType typeB, EngineType typeW) {
        controllerB = new GameController();
        controllerB.startNewGame(Slot.WHITE, typeB);
        controllerW = new GameController();
        controllerW.startNewGame(Slot.BLACK, typeW);
        gameBoard = controllerB.gameBoard;
        gameSaved = true;
    }

    public void makeWarMove() {
        if (controllerB.getGameBoard().getTurn() == Slot.WHITE) {
            controllerW.gameBoard = controllerB.gameBoard;
            controllerW.makeMove();
            gameBoard = controllerW.gameBoard;
        } else {
            controllerB.gameBoard = controllerW.gameBoard;
            controllerB.makeMove();
            gameBoard = controllerB.gameBoard;
        }
    }

    private void prepareSimulationGame(GameBoard gameBoard, List<HistoryItem> historyBlack, List<HistoryItem> historyWhite) {
        prepareMoveEngine(EngineType.TREE);
        this.gameBoard = gameBoard;
        initHistoryForSimulation(historyBlack, historyWhite);
        gameSaved = false;
        simulation = true;
    }

    private void prepareMoveEngine(EngineType type) {
        moveEngine = EngineFactory.createMoveEngine(this, type);
    }

    private void initHistoryForSimulation(List<HistoryItem> historyBlack, List<HistoryItem> historyWhite) {
        this.historyBlack = new ArrayList<>();
        this.historyWhite = new ArrayList<>();
        if (!historyBlack.isEmpty())
            this.historyBlack.add(historyBlack.get(historyBlack.size() - 1).copy());
        if (!historyWhite.isEmpty())
            this.historyWhite.add(historyWhite.get(historyWhite.size() - 1).copy());
    }

    private void runSimulations() {
        if (simulation || EngineType.TREE != moveEngine.getType())
            return;
        Logger.debug("sim\tstarting simulations");
        simCount = 1;
        wins = 0;
        loses = 0;
        long start = new Date().getTime();
        while (continueSim(start, simCount)) {
            Logger.setDebugOff();
            startSimulationPool();
            Logger.setDebugOn();
        }
        Logger.debug(String.format("sim\t[%d] simulations with result = [%d, %d] success ratio = [%.2f]", simCount - 1, wins, loses, ((float) wins) / (float) (loses + wins)));
    }

    private void startSimulationPool() {
        ExecutorService executor = Executors.newFixedThreadPool(Tuning.CORES);
        int taskCount = Tuning.CORES * Tuning.SIM_HB;
        for (int i = 0; i < taskCount; i++) {
            executor.execute(this::startSingleSimulation);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    private void startSingleSimulation() {
        GameController controller = new GameController();
        controller.prepareSimulationGame(gameBoard.copy(), historyBlack, historyWhite);
        while (!controller.isFinished()) {
            controller.makeMove();
        }
        updateSimResults(controller.getScore());
    }

    private void updateSimResults(Score s) {
        if (s.getWinner() == gameBoard.getTurn())
            wins++;
        else
            loses++;
        simCount++;
    }

    private boolean continueSim(long start, int count) {
        long stop = new Date().getTime();
        if (count > Tuning.SIM_COUNT_L1 || (stop - start) > Tuning.SIM_TIME)
            return false;
        Score s = getScore();
        int gameProgress = s.getBlack() + s.getWhite();
        if (gameProgress > Tuning.SIM_L2 && count > Tuning.SIM_COUNT_L2)
            return false;
        if (gameProgress > Tuning.SIM_L3 && count > Tuning.SIM_COUNT_L3)
            return false;
        return !(gameProgress > Tuning.SIM_L4 && count > Tuning.SIM_COUNT_L4);
    }

    private void initHistory() {
        historyBlack = new ArrayList<>();
        historyWhite = new ArrayList<>();
        historyBlack.add(HistoryItem.fromGB(gameBoard));
    }

    public boolean makePlayerMove(Move move) {
        Set<Move> directions = GameRules.isMoveAvailable(move, gameBoard);
        if (directions.isEmpty()) {
            return false;
        } else {
            updateBoard(move);
            if (!GameRules.getAvailableMoves(gameBoard).isEmpty()) {
                makeMove();
            } else {
                nextTurn();
            }
            return true;
        }
    }

    private void nextTurn() {
        gameBoard.setTurn(gameBoard.getTurn().opposite());
    }

    public void updateBoard(Move move) {
        HistoryItem parent = HistoryItem.fromGB(gameBoard);
        Logger.debug(String.format("turn\t[%s] move = [%d, %d]", gameBoard.getTurn().name(), move.getI(), move.getJ()));
        GameRules.updateBoard(move, gameBoard);
        if (gameBoard.getTurn() == Slot.BLACK) {
            historyBlack.add(HistoryItem.fromGBWithParent(gameBoard, parent));
        } else {
            historyWhite.add(HistoryItem.fromGBWithParent(gameBoard, parent));
        }
        Logger.debug(String.format("score\t[%d, %d]", getScore().getBlack(), getScore().getWhite()));
        nextTurn();
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    private void makeMove() {
        Set<Move> moves = GameRules.getAvailableMoves(gameBoard);
        if (!moves.isEmpty()) {
            runSimulations();
            Move move = moveEngine.chooseMove(moves);
            updateBoard(move);
            if (!isFinished() && GameRules.getAvailableMoves(gameBoard).isEmpty()) {
                nextTurn();
                makeMove();
            }
        }
    }

    public boolean isFinished() {
        boolean finished = GameRules.isGameFinished(gameBoard);
        if (finished && !gameSaved) {
            gameSaved = true;
            Score score = getScore();
            GameService.updateScores(historyWhite, historyBlack, getWins(score, Slot.WHITE), getLoses(score, Slot.WHITE), getWins(score, Slot.BLACK), getLoses(score, Slot.BLACK));
            if (!simulation && EngineType.TREE == moveEngine.getType()) {
                NetUtil.runTraining();
                printStats(score);
            }
        }
        return finished;
    }

    private void printStats(Score score) {
        Logger.debug("");
        Logger.debug("game\tFINISHED");
        Logger.debug(String.format("game\tBLACK = [%d] WHITE = [%d]", score.getBlack(), score.getWhite()));
        Logger.debug("db\tstatistics:");
        GameService.printStats();
    }

    private int getWins(Score score, Slot s) {
        if (score.getWinner().opposite() == s)
            return 0;
        if (score.getWinner() == Slot.EMPTY)
            return 1;
        return 2 + (Math.abs(score.getBlack() - score.getWhite()) / 10);
    }

    private int getLoses(Score score, Slot s) {
        return getWins(new Score(score.getWhite(), score.getBlack(), score.getWinner().opposite()), s);
    }

    public Score getScore() {
        return createScore(gameBoard.count(Slot.WHITE), gameBoard.count(Slot.BLACK));
    }

    private Score createScore(int white, int black) {
        Slot s = null;
        if (isFinished()) {
            if (white == black) {
                s = Slot.EMPTY;
            } else {
                if (white > black) {
                    s = Slot.WHITE;
                } else {
                    s = Slot.BLACK;
                }
            }
        }
        return new Score(black, white, s);
    }
}
