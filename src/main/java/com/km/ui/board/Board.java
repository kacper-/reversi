package com.km.ui.board;

import com.km.engine.EngineType;
import com.km.game.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Board extends Canvas implements MouseListener, UIListener {
    private static final int BOARD_OFFSET = 25;
    private static final int SQUARE_SIZE = 50;
    private static final int BOARD_SIZE = 8;
    private static final int AVAILABLE_MOVE_FACTOR = 6;
    private boolean showAvailable = true;
    private GameRunner gameRunner;

    public Board(GameRunner gameRunner) {
        this.gameRunner = gameRunner;
        this.gameRunner.setUiListener(this);
        this.addMouseListener(this);
        int i = (2 * BOARD_OFFSET) + (BOARD_SIZE * SQUARE_SIZE);
        setPreferredSize(new Dimension(i, i));
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
    }

    private void renderBoard(Graphics g) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Slot s = gameRunner.getGameController().getGameBoard().getValue(i, j);
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
                            for (Move m : gameRunner.getAvailableMoves()) {
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

    private int getAvailableMoveIndicatorSize() {
        return SQUARE_SIZE / AVAILABLE_MOVE_FACTOR;
    }

    private int getAvailableMoveIndicatorOffset() {
        return (SQUARE_SIZE * (AVAILABLE_MOVE_FACTOR - 1)) / (AVAILABLE_MOVE_FACTOR * 2);
    }

    private Move positionToMove(int x, int y) {
        int i = (x - BOARD_OFFSET) / SQUARE_SIZE;
        int j = (y - BOARD_OFFSET) / SQUARE_SIZE;
        return new Move(Math.min(i, GameBoard.SIZE - 1), Math.min(j, GameBoard.SIZE - 1), gameRunner.getGameController().getGameBoard().getTurn());
    }

    public void startNewGame(Slot s, EngineType type) {
        gameRunner.startNewGame(s, type);
    }

    public void startBatchTrain(int cycleCount, int trainCycleLen, int testCycleLen) {
        showAvailable = false;
        gameRunner.startBatchTrain(cycleCount, trainCycleLen, testCycleLen);
    }

    public void startWarGame(EngineType typeB, EngineType typeW, int count) {
        showAvailable = false;
        gameRunner.startWarGame(typeB, typeW, count);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        showAvailable = false;
        Move move = positionToMove(mouseEvent.getX(), mouseEvent.getY());
        gameRunner.playerMove(move);
        showAvailable = true;
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

    @Override
    public void refreshUI() {
        repaint();
    }
}
