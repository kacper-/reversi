package com.km.game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameRules {
    private static Move[] vectors = new Move[]{
            new Move(-1, -1, null),
            new Move(-1, 1, null),
            new Move(-1, 0, null),
            new Move(0, -1, null),
            new Move(0, 1, null),
            new Move(1, -1, null),
            new Move(1, 1, null),
            new Move(1, 0, null)
    };

    public static Set<Move> getAvailableMoves(GameBoard gameBoard) {
        Set<Move> moves = new HashSet<>();
        for (int i = 0; i < GameBoard.SIZE; i++) {
            for (int j = 0; j < GameBoard.SIZE; j++) {
                Move move = new Move(i, j, gameBoard.getTurn());
                if (!isMoveAvailable(move, gameBoard).isEmpty()) {
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    static Set<Move> isMoveAvailable(Move move, GameBoard gameBoard) {
        Set<Move> directions = new HashSet<>();
        if (gameBoard.getValue(move.getI(), move.getJ()) != Slot.EMPTY) {
            return directions;
        }
        for (Move m : getProperNeighbours(move, gameBoard)) {
            if (isDirectionValid(move, m, gameBoard)) {
                directions.add(move.vector(m));
            }
        }
        return directions;
    }

    private static boolean isDirectionValid(Move move, Move m, GameBoard gameBoard) {
        Move v = move.vector(m);
        Move newM = m.add(v);
        if (isPositionValid(newM.getI(), newM.getJ())) {
            Slot s = gameBoard.getValue(newM.getI(), newM.getJ());
            if (s == gameBoard.getTurn()) {
                return true;
            }
            if (s.opposite() == gameBoard.getTurn()) {
                return isDirectionValid(m, newM, gameBoard);
            }
        }
        return false;
    }

    private static Set<Move> getProperNeighbours(Move move, GameBoard gameBoard) {
        Set<Move> moves = new HashSet<>();
        for (Move m : getNeighborhood(move)) {
            if (gameBoard.getValue(m.getI(), m.getJ()).opposite() == gameBoard.getTurn()) {
                moves.add(m);
            }
        }
        return moves;
    }

    private static Set<Move> getNeighborhood(Move move) {
        Set<Move> moves = new HashSet<>();
        for (Move v : vectors) {
            Move m = move.add(v);
            if (isPositionValid(m.getI(), m.getJ())) {
                moves.add(m);
            }
        }
        return moves;
    }

    private static boolean isPositionValid(int i, int j) {
        return i >= 0 && i < GameBoard.SIZE && j >= 0 && j < GameBoard.SIZE;
    }

    static boolean isGameFinished(GameBoard gameBoard) {
        GameBoard g1 = gameBoard.copy();
        GameBoard g2 = gameBoard.copy();
        g2.setTurn(g1.getTurn().opposite());
        return getAvailableMoves(g1).isEmpty() && getAvailableMoves(g2).isEmpty();
    }

    public static void updateBoard(Move move, GameBoard gameBoard) {
        Set<Move> directions = GameRules.isMoveAvailable(move, gameBoard);
        for (Move d : directions) {
            gameBoard.setValue(gameBoard.getTurn(), move.getI(), move.getJ());
            Move next = move.add(d);
            while (gameBoard.getValue(next.getI(), next.getJ()).opposite() == gameBoard.getTurn()) {
                gameBoard.setValue(gameBoard.getTurn(), next.getI(), next.getJ());
                next = next.add(d);
            }
        }
    }

    static List<Move> getCorners() {
        Move[] moves = new Move[]{
                new Move(0, 0, null),
                new Move(0, 7, null),
                new Move(7, 0, null),
                new Move(7, 7, null)
        };
        return Arrays.asList(moves);
    }

    static List<Move> getSemiCorners() {
        Move[] moves = new Move[]{
                new Move(1, 1, null),
                new Move(1, 6, null),
                new Move(6, 1, null),
                new Move(6, 6, null)
        };
        return Arrays.asList(moves);
    }

    static void initGameBoard(GameBoard gameBoard) {
        gameBoard.empty();
        gameBoard.setValue(Slot.WHITE, 3, 3);
        gameBoard.setValue(Slot.WHITE, 4, 4);
        gameBoard.setValue(Slot.BLACK, 3, 4);
        gameBoard.setValue(Slot.BLACK, 4, 3);
        gameBoard.setTurn(Slot.BLACK);
    }
}
