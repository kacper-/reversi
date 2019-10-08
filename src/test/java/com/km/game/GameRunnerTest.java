package com.km.game;

import com.km.engine.EngineType;
import com.km.nn.NetUtil;
import org.junit.Test;

public class GameRunnerTest {

    @Test
    public void startWarGame() {
        NetUtil.setFilePath("net");
        GameRunner runner = new GameRunner();
        runner.startWarGame(EngineType.RANDOM, EngineType.RULE, 100);
        while (!runner.isWarFinished());
    }
}