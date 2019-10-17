package com.km.engine;

import com.km.Config;
import com.km.game.GameController;
import com.km.nn.NetVersion;

public class EngineFactory {
    public static MoveEngine createMoveEngine(GameController controller, EngineType type) {
        MoveEngine engine = null;
        switch (type) {
            case ANN2:
                engine = new ANNEngine(NetVersion.NET2, Config.getEngineANN2file());
                break;
            case ANN3:
                engine = new ANNEngine(NetVersion.NET3, Config.getEngineANN3file());
                break;
            case ANN3RC:
                engine = new ANNEngine(NetVersion.NET3, Config.getEngineANN3RCfile());
                break;
            case ANN4:
                engine = new ANNEngine(NetVersion.NET4, Config.getEngineANN4file());
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
        }
        engine.setGameController(controller);
        return engine;
    }
}
