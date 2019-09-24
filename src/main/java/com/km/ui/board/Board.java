package com.km.ui.board;

import com.km.engine.EngineType;
import com.km.game.*;

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
    private boolean warReady = true;

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

    public void startWarGame(EngineType typeB, EngineType typeW, int count) {
        showAvailable = false;
        runWars(typeB, typeW, count);
    }

    private void runWars(EngineType typeB, EngineType typeW, int count) {
        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                try {
                    runWar(typeB, typeW);
                    while (!warReady)
                        Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void runWar(EngineType typeB, EngineType typeW) {
        getGameController().startWarGame(typeB, typeW);
        repaint();
        warReady = false;
        new Thread(() -> {
            while (!getGameController().isFinished()) {
                getGameController().makeWarMove();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                repaint();
            }
            warReady = true;
        }).start();
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
                    Thread.sleep(250);
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
