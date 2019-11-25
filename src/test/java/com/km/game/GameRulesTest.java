package com.km.game;

import org.junit.Assert;

public class GameRulesTest {

    @org.junit.Test
    public void getAvailableMoves() {
        GameBoard g = new GameBoard();
        GameRules.initGameBoard(g);
        Assert.assertEquals(4, GameRules.getAvailableMoves(g).size());
    }

    @org.junit.Test
    public void isMoveAvailable() {
        GameBoard g = new GameBoard();
        GameRules.initGameBoard(g);
        Assert.assertFalse(GameRules.isMoveAvailable(new Move(3, 2), g).isEmpty());
        Assert.assertFalse(GameRules.isMoveAvailable(new Move(2, 3), g).isEmpty());
        Assert.assertFalse(GameRules.isMoveAvailable(new Move(5, 4), g).isEmpty());
        Assert.assertFalse(GameRules.isMoveAvailable(new Move(4, 5), g).isEmpty());
        Assert.assertTrue(GameRules.isMoveAvailable(new Move(4, 2), g).isEmpty());
        Assert.assertTrue(GameRules.isMoveAvailable(new Move(5, 3), g).isEmpty());
        Assert.assertTrue(GameRules.isMoveAvailable(new Move(2, 4), g).isEmpty());
        Assert.assertTrue(GameRules.isMoveAvailable(new Move(3, 5), g).isEmpty());
    }
}
