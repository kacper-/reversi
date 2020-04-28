package com.km.game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameRules {
    private static Move[] vectors = new Move[]{
            new Move(-1, -1),
            new Move(-1, 1),
            new Move(-1, 0),
            new Move(0, -1),
            new Move(0, 1),
            new Move(1, -1),
            new Move(1, 1),
            new Move(1, 0)
    };

    public static Set<Move> getAvailableMoves(GameBoard gameBoard) {
        Set<Move> moves = new HashSet<>();
        for (int i = 0; i < GameBoard.S2; i++) {
            if (gameBoard.getValue(i) == Slot.EMPTY) {
                Move move = new Move(GameBoard.getI(i), GameBoard.getJ(i));
                if (isMoveAvailableFast(move, gameBoard)) {
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

    static boolean isMoveAvailableFast(Move move, GameBoard gameBoard) {
        for (Move m : getProperNeighbours(move, gameBoard)) {
            if (isDirectionValid(move, m, gameBoard)) {
                return true;
            }
        }
        return false;
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
        if (gameBoard.count(Slot.EMPTY) == 0)
            return true;
        GameBoard g1 = gameBoard.copy();
        int moves = getAvailableMoves(g1).size();
        g1.setTurn(g1.getTurn().opposite());
        moves += getAvailableMoves(g1).size();
        return moves == 0;
    }

    static void updateBoard(Move move, GameBoard gameBoard) {
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

    public static List<Move> getCorners() {
        Move[] moves = new Move[]{
                new Move(0, 0),
                new Move(0, 7),
                new Move(7, 0),
                new Move(7, 7)
        };
        return Arrays.asList(moves);
    }

    public static List<Move> getSemiCorners() {
        Move[] moves = new Move[]{
                new Move(1, 1),
                new Move(1, 6),
                new Move(6, 1),
                new Move(6, 6)
        };
        return Arrays.asList(moves);
    }

    public static Move toSimpleMove(Move m) {
        return new Move(m.getI(), m.getJ());
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
