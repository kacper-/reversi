package com.km.game;

import com.km.engine.EngineType;
import org.junit.Test;

public class GameRunnerTest {

    @Test
    public void startWarGame() {
        GameRunner runner = new GameRunner();
        runner.startWarGame(EngineType.RANDOM, EngineType.RULE, 100);
        while (!runner.isWarFinished());
    }
}