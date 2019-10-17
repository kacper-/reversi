package com.km.game;

import com.km.Config;
import com.km.engine.EngineType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

public class GameControllerTest {

    @Test
    public void createInitialBoard() {
        Config.setProperties(new Properties());
        GameController controller = new GameController();
        controller.startNewGame(Slot.BLACK, EngineType.MC);
        Score score = controller.getScore();
        Assert.assertEquals(2, score.getBlack());
        Assert.assertEquals(2, score.getWhite());
        Assert.assertNull(score.getWinner());
    }

    @Test
    public void smokeTest() {
        Config.setProperties(new Properties());
        GameController controller = new GameController();
        controller.startNewGame(Slot.WHITE, EngineType.MC);
        Score score = controller.getScore();
        Assert.assertEquals(4, score.getBlack());
        Assert.assertEquals(1, score.getWhite());
        Assert.assertNull(score.getWinner());
    }

}