package com.km.engine;

import com.km.Config;
import com.km.game.GameController;
import com.km.nn.NetVersion;

public class EngineFactory {
    public static MoveEngine createMoveEngine(GameController controller, EngineType type) {
        MoveEngine engine = null;
        switch (type) {
            case ANN3MRC:
                engine = new ANNEngine(NetVersion.NET3M, Config.getEngineANN3MRCfile(), Config.getDataFile());
                break;
            case ANN4MRC:
                engine = new ANNEngine(NetVersion.NET4M, Config.getEngineANN4MRCfile(), Config.getDataFile());
                break;
            case ANNMMRC:
                engine = new ANNEngine(NetVersion.NETMM, Config.getEngineANNMMRCfile(), Config.getDataFile());
                break;
            case RULE:
                engine = new RuleEngine();
                break;
            case SUPER3M:
                engine = new SuperEngine(NetVersion.NET3M, Config.getDataFile());
                break;
            case SUPER4M:
                engine = new SuperEngine(NetVersion.NET4M, Config.getDataFile());
                break;
            case MC:
                engine = new TreeSearchEngine();
                break;
            case RANDOM:
                engine = new RandomEngine();
                break;
            case BATCH:
                engine = new ANNEngine(Config.getBatchNetVersion(), Config.getBatchNetFile(), Config.getDataFile());
                break;
        }
        engine.setGameController(controller);
        return engine;
    }
}
