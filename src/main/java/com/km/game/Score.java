package com.km.game;

public class Score {
    private int black;
    private int white;
    private Slot winner;

    Score(int black, int white, Slot winner) {
        this.black = black;
        this.white = white;
        this.winner = winner;
    }

    public int getBlack() {
        return black;
    }

    @Override
    public String toString() {
        String output = "B=" + black + "_W=" + white;
        if (winner != null) {
            output += "_W" + winner.getSymbol();
        }
        return output;
    }

    public int getWhite() {
        return white;
    }

    public Slot getWinner() {
        return winner;
    }
}
