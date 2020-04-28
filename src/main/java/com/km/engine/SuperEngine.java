package com.km.engine;

import com.km.game.GameController;
import com.km.game.Move;
import com.km.nn.NetVersion;

import java.util.Set;

public class SuperEngine implements MoveEngine {
    private final RuleEngine rule = new RuleEngine();
    private ANNEngine ann;

    SuperEngine(NetVersion version, String dataFile) {
        switch (version) {
            case NET3M:
                ann = new ANNEngine(NetVersion.NET3M, EngineType.ANN3MRC.name(), dataFile);
                break;
            case NET4M:
                ann = new ANNEngine(NetVersion.NET4M, EngineType.ANN4MRC.name(), dataFile);
                break;
        }
    }

    @Override
    public void setGameController(GameController controller) {
        rule.setGameController(controller);
        ann.setGameController(controller);
    }

    @Override
    public Move chooseMove(Set<Move> moves) {
        double score;
        double bestScore = -Double.MAX_VALUE;
        Move best = null;
        for (Move m : moves) {
            score = rule.scoreMove(m) + ann.scoreMove(m);
            if (bestScore < score) {
                bestScore = score;
                best = m;
            }
        }
        return best;
    }
}
