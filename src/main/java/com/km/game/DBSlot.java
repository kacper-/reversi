package com.km.game;

public enum DBSlot {
    EMPTY('E'),
    CURRENT('C'),
    OTHER('O');

    private Character symbol;

    DBSlot(Character symbol) {
        this.symbol = symbol;
    }

    public static DBSlot fromSymbol(Character c) {
        switch (c) {
            case 'C':
                return CURRENT;
            case 'O':
                return OTHER;
            default:
                return EMPTY;
        }
    }

    public Character getSymbol() {
        return symbol;
    }

    public DBSlot opposite() {
        switch (this) {
            case CURRENT:
                return OTHER;
            case OTHER:
                return CURRENT;
        }
        return EMPTY;
    }

    @Override
    public String toString() {
        return symbol.toString();
    }
}
