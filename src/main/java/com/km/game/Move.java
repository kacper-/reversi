package com.km.game;

public class Move {
    private int i;
    private int j;

    public Move(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;

        Move move = (Move) o;

        if (getI() != move.getI()) return false;
        return getJ() == move.getJ();
    }

    @Override
    public int hashCode() {
        int result = getI();
        result = 31 * result + getJ();
        return result;
    }

    @Override
    public String toString() {
        return i + "_" + j;
    }

    Move add(Move v) {
        return new Move(i + v.i, j + v.j);
    }

    Move vector(Move m) {
        return new Move(m.i - i, m.j - j);
    }
}
