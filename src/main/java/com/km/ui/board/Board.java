package com.km.ui.board;

import com.km.Logger;
import com.km.engine.EngineType;
import com.km.game.*;
import com.km.nn.NetUtil;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Board extends Canvas implements MouseListener {
    private static final int BOARD_OFFSET = 25;
    private static final int SQUARE_SIZE = 50;
    private static final int BOARD_SIZE = 8;
    private static final int AVAILABLE_MOVE_FACTOR = 6;
    private GameController controller;
    private boolean showAvailable = true;
    private ScoreListener scoreListener;
    private int warScoreB = 0;
    private int warScoreW = 0;

    public Board(ScoreListener scoreListener) {
        this.scoreListener = scoreListener;
        this.addMouseListener(this);
        int i = (2 * BOARD_OFFSET) + (BOARD_SIZE * SQUARE_SIZE);
        setPreferredSize(new Dimension(i, i));
    }

    private GameController getGameController() {
        if (controller == null)
            controller = new GameController();
        return controller;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        for (int i = 0; i <= BOARD_SIZE; i++) {
            g.drawLine(BOARD_OFFSET, BOARD_OFFSET + (i * SQUARE_SIZE), BOARD_OFFSET + (SQUARE_SIZE * BOARD_SIZE), BOARD_OFFSET + (i * SQUARE_SIZE));
            g.drawLine(BOARD_OFFSET + (i * SQUARE_SIZE), BOARD_OFFSET, BOARD_OFFSET + (i * SQUARE_SIZE), BOARD_OFFSET + (SQUARE_SIZE * BOARD_SIZE));
        }
        renderBoard(g);
        renderScore();
    }

    private void renderScore() {
        if (controller != null)
            scoreListener.setScore(controller.getScore());
    }

    private void renderBoard(Graphics g) {
        if (controller != null) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    Slot s = getGameController().getGameBoard().getValue(i, j);
                    switch (s) {
                        case BLACK:
                            g.setColor(Color.BLACK);
                            g.fillOval(BOARD_OFFSET + (i * SQUARE_SIZE), BOARD_OFFSET + (j * SQUARE_SIZE), SQUARE_SIZE, SQUARE_SIZE);
                            break;
                        case WHITE:
                            g.setColor(Color.WHITE);
                            g.fillOval(BOARD_OFFSET + (i * SQUARE_SIZE), BOARD_OFFSET + (j * SQUARE_SIZE), SQUARE_SIZE, SQUARE_SIZE);
                            break;
                        case EMPTY:
                            if (showAvailable) {
                                for (Move m : GameRules.getAvailableMoves(getGameController().getGameBoard())) {
                                    if (m.getJ() == j && m.getI() == i) {
                                        g.setColor(Color.GRAY);
                                        int offset = getAvailableMoveIndicatorOffset();
                                        int size = getAvailableMoveIndicatorSize();
                                        g.fillOval(offset + BOARD_OFFSET + (i * SQUARE_SIZE), offset + BOARD_OFFSET + (j * SQUARE_SIZE), size, size);
                                    }
                                }
                            }
                    }
                }
            }
        }
    }

    private int getAvailableMoveIndicatorSize() {
        return SQUARE_SIZE / AVAILABLE_MOVE_FACTOR;
    }

    private int getAvailableMoveIndicatorOffset() {
        return (SQUARE_SIZE * (AVAILABLE_MOVE_FACTOR - 1)) / (AVAILABLE_MOVE_FACTOR * 2);
    }

    private Move positionToMove(int x, int y) {
        int i = (x - BOARD_OFFSET) / SQUARE_SIZE;
        int j = (y - BOARD_OFFSET) / SQUARE_SIZE;
        return new Move(Math.min(i, GameBoard.SIZE - 1), Math.min(j, GameBoard.SIZE - 1), getGameController().getGameBoard().getTurn());
    }

    public void startNewGame(Slot s, EngineType type) {
        getGameController().startNewGame(s, type);
        repaint();
    }

    public void startBatchTrain(int cycleCount, int trainCycleLen, int testCycleLen) {
        showAvailable = false;
        new Thread(() -> {
            NetUtil.clear();
            int level = Logger.getLevel();
            Logger.info(String.format("board\tbatch train : cycles count [%d] : train cycle len [%d] : test cycle len [%d]", cycleCount, trainCycleLen, testCycleLen));
            Logger.setLevel(Logger.IMPORTANT);
            for (int i = 0; i < cycleCount; i++) {
                runWars(EngineType.TREE, EngineType.RANDOM, trainCycleLen);
                runWars(EngineType.ANN, EngineType.RANDOM, testCycleLen);
            }
            Logger.setLevel(level);
            Logger.info("board\tbatch train finished");
            repaint();
        }).start();
    }

    public void startWarGame(EngineType typeB, EngineType typeW, int count) {
        showAvailable = false;
        new Thread(() -> {
            runWars(typeB, typeW, count);
            repaint();
        }).start();
    }

    private void runWars(EngineType typeB, EngineType typeW, int count) {
        warScoreW = 0;
        warScoreB = 0;
        for (int i = 0; i < count; i++) {
            Logger.info(String.format("board\tstarting war game [%d] of [%d]", i + 1, count));
            runWar(typeB, typeW);
            scoreListener.setWarScore(i + 1, warScoreB, warScoreW);
            Logger.info(String.format("board\tcurrent war score [%s] [%d] : [%s] [%d]", typeB.name(), warScoreB, typeW.name(), warScoreW));
        }
        Logger.important(String.format("board\tfinal war score [%s] [%d] : [%s] [%d]", typeB.name(), warScoreB, typeW.name(), warScoreW));
    }

    private void runWar(EngineType typeB, EngineType typeW) {
        getGameController().startWarGame(typeB, typeW);
        while (!getGameController().isFinished()) {
            getGameController().makeWarMove();
            if (typeB == EngineType.TREE || typeW == EngineType.TREE)
                repaint();
        }
        if (getGameController().getScore().getWinner() == Slot.BLACK)
            warScoreB++;
        else
            warScoreW++;
        getGameController().afterWarGame();
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (!getGameController().isFinished()) {
            Move move = positionToMove(mouseEvent.getX(), mouseEvent.getY());
            if (GameRules.getAvailableMoves(getGameController().getGameBoard()).contains(move)) {
                new Thread(() -> {
                    if (getGameController().makePlayerMove(move)) {
                        showAvailable = true;
                        repaint();
                    }
                }).start();
                try {
                    showAvailable = false;
                    Thread.sleep(100);
                    repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
