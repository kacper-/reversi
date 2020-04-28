package com.km.entities;

import java.util.Objects;

public class Moves {
    private Nodes snode;
    private Nodes enode;

    public Moves(Nodes snode, Nodes enode) {
        this.snode= snode;
        this.enode = enode;
    }

    public Nodes getSnode() {
        return snode;
    }

    public Nodes getEnode() {
        return enode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Moves moves = (Moves) o;
        return snode.equals(moves.snode) &&
                enode.equals(moves.enode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(snode, enode);
    }
}
