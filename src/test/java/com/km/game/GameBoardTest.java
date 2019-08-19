package com.km.game;

import org.junit.Assert;

public class GameBoardTest {

    @org.junit.Test
    public void toDBString() {
        GameBoard g = new GameBoard();
        g.empty();
        String s = g.toDBString();
        Assert.assertEquals(64, s.length());
        Assert.assertEquals(0, g.count(Slot.BLACK));
        Assert.assertEquals(0, g.count(Slot.WHITE));
    }
}