package com.km.game;

public class Move {
    private int i;
    private int j;
    private Slot s;

    public Move(int i, int j, Slot s) {
        this.i = i;
        this.j = j;
        this.s = s;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    Slot getS() {
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (i != move.i) return false;
        if (j != move.j) return false;
        return s == move.s;
    }

    @Override
    public int hashCode() {
        int result = i;
        result = 31 * result + j;
        result = 31 * result + (s != null ? s.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (s == null) {
            return "NULL_" + i + "_" + j;
        } else {
            return s.getSymbol() + "_" + i + "_" + j;
        }
    }

    Move add(Move v) {
        return new Move(i + v.i, j + v.j, s);
    }

    Move vector(Move m) {
        return new Move(m.i - i, m.j - j, m.s);
    }
}
