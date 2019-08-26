package com.km.engine;

import com.km.game.GameController;

public class EngineFactory {
    public static MoveEngine createMoveEngine(GameController controller, EngineType type) {
        MoveEngine engine = null;
        switch (type) {
            case ANN:
                engine = new ANNEngine();
                break;
            case RULE:
                engine = new RuleEngine();
                break;
            case SUPER:
                engine = new SuperEngine();
                break;
            case TREE:
                engine = new TreeSearchEngine();
                break;
        }
        engine.setGameController(controller);
        return engine;
    }
}
