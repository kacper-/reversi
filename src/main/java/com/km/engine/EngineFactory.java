package com.km.engine;

import com.km.game.GameController;
import com.km.nn.NetVersion;

public class EngineFactory {
    public static MoveEngine createMoveEngine(GameController controller, EngineType type) {
        MoveEngine engine = null;
        switch (type) {
            case ANN2:
                engine = new ANNEngine(NetVersion.NET2);
                break;
            case ANN3:
                engine = new ANNEngine(NetVersion.NET3);
                break;
            case ANN3RC:
                engine = new ANNEngine(NetVersion.NET3, EngineType.ANN3RC.name());
                break;
            case ANN4:
                engine = new ANNEngine(NetVersion.NET4);
                break;
            case ANN4RC:
                engine = new ANNEngine(NetVersion.NET4, EngineType.ANN4RC.name());
                break;
            case RULE:
                engine = new RuleEngine();
                break;
            case SUPER:
                engine = new SuperEngine();
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
