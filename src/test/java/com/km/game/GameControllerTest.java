package com.km.game;

import com.km.engine.EngineType;
import org.junit.Assert;
import org.junit.Test;

public class GameControllerTest {

    @Test
    public void createInitialBoard() {
        GameController controller = new GameController();
        controller.startNewGame(Slot.BLACK, EngineType.TREE);
        Score score = controller.getScore();
        Assert.assertEquals(2, score.getBlack());
        Assert.assertEquals(2, score.getWhite());
        Assert.assertNull(score.getWinner());
    }

    @Test
    public void smokeTest() {
        GameController controller = new GameController();
        controller.startNewGame(Slot.WHITE, EngineType.TREE);
        Score score = controller.getScore();
        Assert.assertEquals(4, score.getBlack());
        Assert.assertEquals(1, score.getWhite());
        Assert.assertNull(score.getWinner());
    }

}