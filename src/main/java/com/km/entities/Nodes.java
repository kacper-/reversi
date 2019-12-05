package com.km.entities;

import com.km.game.DBSlot;

import java.io.Serializable;

public class Nodes implements Serializable {
    private int id;
    private String board;
    private int wins;
    private int loses;

    public Nodes(int id, String board, int wins, int loses) {
        this.id = id;
        this.board = board;
        this.wins = wins;
        this.loses = loses;
    }

    public Nodes(String board, int wins, int loses) {
        this.board = board;
        this.wins = wins;
        this.loses = loses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nodes nodes = (Nodes) o;

        if (id != nodes.id) return false;
        if (wins != nodes.wins) return false;
        if (loses != nodes.loses) return false;
        return board.equals(nodes.board);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + board.hashCode();
        result = 31 * result + wins;
        result = 31 * result + loses;
        return result;
    }

    public String getBoard() {
        return board;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

    public int getId() {
        return id;
    }

    public int getCount(DBSlot s) {
        int count = 0;
        for (int i = 0; i < board.length(); i++) {
            if (board.charAt(i) == s.getSymbol())
                count++;
        }
        return count;
    }
}
