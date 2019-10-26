package com.km.game;

import com.km.Config;
import com.km.engine.EngineType;
import com.km.nn.NetTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

public class GameControllerTest {

    @BeforeClass
    public static void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(GameControllerTest.class.getClassLoader().getResourceAsStream(Config.FILE_NAME));
        Config.setProperties(properties);
    }

    @Test
    public void createInitialBoard() {
        GameController controller = new GameController();
        controller.startNewGame(Slot.BLACK, EngineType.MC);
        Score score = controller.getScore();
        Assert.assertEquals(2, score.getBlack());
        Assert.assertEquals(2, score.getWhite());
        Assert.assertNull(score.getWinner());
    }

    @Test
    public void smokeTest() {
        GameController controller = new GameController();
        controller.startNewGame(Slot.WHITE, EngineType.MC);
        Score score = controller.getScore();
        Assert.assertEquals(4, score.getBlack());
        Assert.assertEquals(1, score.getWhite());
        Assert.assertNull(score.getWinner());
    }

}