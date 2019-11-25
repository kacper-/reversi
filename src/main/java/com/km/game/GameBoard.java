package com.km.game;

public class GameBoard {
    public static final int SIZE = 8;
    public static final int S2 = 64;
    private Slot[] board = new Slot[S2];
    private Slot turn;

    private static int index(int i, int j) {
        return (j << 3) | i;
    }

    public static int getI(int i) {
        return i & 7;
    }

    public static int getJ(int i) {
        return i >> 3;
    }

    public Slot getTurn() {
        return turn;
    }

    void setTurn(Slot turn) {
        this.turn = turn;
    }

    void empty() {
        for (int i = 0; i < S2; i++)
            board[i] = Slot.EMPTY;
        turn = Slot.EMPTY;
    }

    int count(Slot s) {
        int c = 0;
        for (int i = 0; i < S2; i++) {
            if (board[i] == s)
                c++;
        }
        return c;
    }

    @Override
    public String toString() {
        String output = toDBString();
        output += "_T" + turn.getSymbol();
        return output;
    }

    public String toDBString() {
        char[] flat = new char[S2];
        for (int i = 0; i < S2; i++)
            flat[i] = translate(board[i]).getSymbol();
        return String.valueOf(flat);
    }

    private DBSlot translate(Slot s) {
        if (s == Slot.EMPTY) {
            return DBSlot.EMPTY;
        } else {
            if (s == turn) {
                return DBSlot.CURRENT;
            } else {
                return DBSlot.OTHER;
            }
        }
    }

    void setValue(Slot s, int i, int j) {
        board[index(i, j)] = s;
    }

    public Slot getValue(int i, int j) {
        return board[index(i, j)];
    }

    Slot getValue(int i) {
        return board[i];
    }

    GameBoard copy() {
        GameBoard g = new GameBoard();
        g.board = copyBoard();
        g.turn = turn;
        return g;
    }

    private Slot[] copyBoard() {
        Slot[] b = new Slot[S2];
        System.arraycopy(board, 0, b, 0, S2);
        return b;
    }
}
