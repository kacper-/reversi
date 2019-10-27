package com.km.game;

import com.km.Config;
import com.km.engine.EngineType;
import com.km.nn.NetTest;
import com.km.nn.NetUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

public class GameRunnerTest {

    @BeforeClass
    public static void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(GameRunnerTest.class.getClassLoader().getResourceAsStream(Config.FILE_NAME));
        Config.setProperties(properties);
    }

    @Test
    public void startWarGame() {
        GameRunner runner = new GameRunner();
        runner.startWarGame(EngineType.RANDOM, EngineType.RULE, 10);
        while (!runner.isWarFinished());
    }
}