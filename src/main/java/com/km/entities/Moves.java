package com.km.entities;

public class Moves {
    private int snodeId;
    private int enodeId;

    public Moves(int snodeId, int enodeId) {
        this.snodeId = snodeId;
        this.enodeId = enodeId;
    }

    public int getSnode() {
        return snodeId;
    }

    public int getEnode() {
        return enodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Moves moves = (Moves) o;

        if (snodeId != moves.snodeId) return false;
        return enodeId == moves.enodeId;

    }

    @Override
    public int hashCode() {
        int result = snodeId;
        result = 31 * result + enodeId;
        return result;
    }
}
