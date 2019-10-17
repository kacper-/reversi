package com.km.game;

import com.km.Config;
import com.km.engine.EngineType;
import com.km.nn.NetUtil;
import org.junit.Test;

import java.util.Properties;

public class GameRunnerTest {

    @Test
    public void startWarGame() {
        Config.setProperties(new Properties());
        GameRunner runner = new GameRunner();
        runner.startWarGame(EngineType.RANDOM, EngineType.RULE, 100);
        while (!runner.isWarFinished());
    }
}