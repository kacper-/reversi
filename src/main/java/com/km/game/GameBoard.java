package com.km.game;

public class GameBoard {
    public static final int SIZE = 8;
    private Slot[][] board = new Slot[SIZE][SIZE];
    private Slot turn;

    public Slot getTurn() {
        return turn;
    }

    void setTurn(Slot turn) {
        this.turn = turn;
    }

    void empty() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = Slot.EMPTY;
            }
        }
        turn = Slot.EMPTY;
    }

    int count(Slot s) {
        int c = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == s)
                    c++;
            }
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
        char[] flat = new char[SIZE * SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                flat[(j * SIZE) + i] = translate(board[i][j]).getSymbol();
            }
        }
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
        board[i][j] = s;
    }

    public Slot getValue(int i, int j) {
        return board[i][j];
    }

    GameBoard copy() {
        GameBoard g = new GameBoard();
        g.board = copyBoard();
        g.turn = turn;
        return g;
    }

    private Slot[][] copyBoard() {
        Slot[][] b = new Slot[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, b[i], 0, SIZE);
        }
        return b;
    }
}
