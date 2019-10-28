package com.km.engine;

import com.km.Config;
import com.km.game.GameController;
import com.km.nn.NetVersion;
// TODO add SUPER3M with NET3M net and ANN3MRC with NET3M
public class EngineFactory {
    public static MoveEngine createMoveEngine(GameController controller, EngineType type) {
        MoveEngine engine = null;
        switch (type) {
            case ANN3RC:
                engine = new ANNEngine(NetVersion.NET3, Config.getEngineANN3RCfile());
                break;
            case ANN4RC:
                engine = new ANNEngine(NetVersion.NET4, Config.getEngineANN4RCfile());
                break;
            case RULE:
                engine = new RuleEngine();
                break;
            case SUPER3:
                engine = new SuperEngine(NetVersion.NET3);
                break;
            case SUPER4:
                engine = new SuperEngine(NetVersion.NET4);
                break;
            case MC:
                engine = new TreeSearchEngine();
                break;
            case RANDOM:
                engine = new RandomEngine();
                break;
            case BATCH:
                engine = new ANNEngine(Config.getBatchNetVersion(), Config.getBatchNetFile());
                break;
        }
        engine.setGameController(controller);
        return engine;
    }
}
