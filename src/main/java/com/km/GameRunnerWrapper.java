package com.km;

import com.km.engine.EngineType;
import com.km.game.GameRunner;

class GameRunnerWrapper {
    private Command command;
    private EngineType warOpp1;
    private EngineType warOpp2;
    private int count;

    boolean parseArgs(String... args) {
        try {
            command = Command.valueOf(args[0].toUpperCase());
            switch (command) {
                case WAR:
                    warOpp1 = EngineType.valueOf(args[1].toUpperCase());
                    warOpp2 = EngineType.valueOf(args[2].toUpperCase());
                    count = Config.getTestLen();
                    break;
                case BATCH:
                    count = Config.getCycleCount();
                    break;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    void run() {
        GameRunner runner = new GameRunner();
        switch (command) {
            case WAR:
                runner.startWarGame(warOpp1, warOpp2, count);
                while (!runner.isWarFinished()) ;
                break;
            case BATCH:
                runner.startBatchTrain(count);
                while (!runner.isBatchFinished()) ;
                break;
        }
    }

    enum Command {
        WAR,
        BATCH
    }
}
