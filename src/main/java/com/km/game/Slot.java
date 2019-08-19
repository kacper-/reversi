package com.km.game;

public enum Slot {
    EMPTY('E'),
    BLACK('B'),
    WHITE('W');

    private Character symbol;

    Slot(Character symbol) {
        this.symbol = symbol;
    }

    public Character getSymbol() {
        return symbol;
    }

    public Slot opposite() {
        switch (this) {
            case BLACK:
                return WHITE;
            case WHITE:
                return BLACK;
        }
        return EMPTY;
    }
}
