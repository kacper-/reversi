package com.km.game;

import com.km.Logger;
import com.km.engine.EngineFactory;
import com.km.engine.EngineType;
import com.km.engine.MoveEngine;
import com.km.repos.GameService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameController {
    private boolean gameSaved = false;
    private GameBoard gameBoard = new GameBoard();
    private List<HistoryItem> historyBlack;
    private List<HistoryItem> historyWhite;
    private MoveEngine moveEngine;
    private MoveEngine randomEngine;
    private boolean simulation;
    private boolean warMode = false;
    private GameController controllerB;
    private GameController controllerW;

    public GameController() {
        randomEngine = EngineFactory.createMoveEngine(this, EngineType.RANDOM);
    }

    public GameController copy() {
        GameController controller = new GameController();
        controller.gameSaved = gameSaved;
        controller.gameBoard = gameBoard.copy();
        controller.historyBlack = historyBlack.stream().map(HistoryItem::copy).collect(Collectors.toList());
        controller.historyWhite = historyWhite.stream().map(HistoryItem::copy).collect(Collectors.toList());
        controller.moveEngine = moveEngine;
        controller.randomEngine = randomEngine;
        controller.simulation = simulation;
        return controller;
    }

    public boolean isSimulation() {
        return simulation;
    }

    void startNewGame(Slot human, EngineType type) {
        simulation = false;
        warMode = false;
        Logger.info(String.format("game\t[%s] plays [%s]", type.name(), human.opposite().name()));
        GameService.clear();
        prepareMoveEngine(type);
        GameRules.initGameBoard(gameBoard);
        initHistory();
        if (gameBoard.getTurn() != human) {
            makeMove();
        }
        gameSaved = false;
    }

    void startWarGame(EngineType typeB, EngineType typeW) {
        warMode = true;
        controllerB = new GameController();
        controllerB.startNewGame(Slot.WHITE, typeB);
        controllerW = new GameController();
        controllerW.startNewGame(Slot.BLACK, typeW);
        gameBoard = controllerB.gameBoard;
        gameSaved = true;
    }

    void makeWarMove() {
        if (controllerB.getGameBoard().getTurn() == Slot.WHITE) {
            controllerW.makeMove();
            gameBoard = controllerW.gameBoard;
        } else {
            controllerB.makeMove();
            gameBoard = controllerB.gameBoard;
        }
        controllerW.gameBoard = gameBoard;
        controllerB.gameBoard = gameBoard;
    }

    private boolean isWarFinished() {
        boolean b = controllerB.isFinished();
        boolean w = controllerW.isFinished();
        if (b != w) {
            Logger.error("board paradox");
            System.exit(0);
        }
        return b;
    }

    public void prepareSimulationGame(GameController copyFrom, EngineType type) {
        warMode = false;
        prepareMoveEngine(type);
        this.gameBoard = copyFrom.gameBoard.copy();
        initHistoryForSimulation(copyFrom.historyBlack, copyFrom.historyWhite);
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

    private void initHistory() {
        historyBlack = new ArrayList<>();
        historyWhite = new ArrayList<>();
        historyBlack.add(HistoryItem.fromGB(gameBoard));
    }

    boolean makePlayerMove(Move move) {
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

    public void nextTurn() {
        gameBoard.setTurn(gameBoard.getTurn().opposite());
    }

    public void updateBoard(Move move) {
        HistoryItem parent = HistoryItem.fromGB(gameBoard);
        GameRules.updateBoard(move, gameBoard);
        if (gameBoard.getTurn() == Slot.BLACK) {
            historyBlack.add(HistoryItem.fromGBWithParent(gameBoard, parent));
        } else {
            historyWhite.add(HistoryItem.fromGBWithParent(gameBoard, parent));
        }
        nextTurn();
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public void makeMove() {
        makeMoveInternal(moveEngine);
    }

    private void makeMoveInternal(MoveEngine engine) {
        Set<Move> moves = GameRules.getAvailableMoves(gameBoard);
        if (!moves.isEmpty()) {
            Move move = engine.chooseMove(moves);
            updateBoard(move);
        } else {
            nextTurn();
        }
    }

    public void makeRandomMove() {
        makeMoveInternal(randomEngine);
    }

    public boolean isFinished() {
        if (warMode)
            return isWarFinished();
        boolean finished = GameRules.isGameFinished(gameBoard);
        if (finished && !gameSaved) {
            gameSaved = true;
        }
        return finished;
    }

    public void updateRepos() {
        Score score = getScore();
        GameService.updateScores(historyWhite, historyBlack, getWins(score, Slot.WHITE), getLoses(score, Slot.WHITE), getWins(score, Slot.BLACK), getLoses(score, Slot.BLACK));
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
        if (GameRules.isGameFinished(gameBoard)) {
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
