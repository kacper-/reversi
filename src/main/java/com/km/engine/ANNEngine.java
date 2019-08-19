package com.km.engine;

import com.km.game.GameController;
import com.km.game.Move;
import com.km.nn.NetUtil;

import java.util.Set;

public class ANNEngine implements MoveEngine {
    private GameController controller;

    @Override
    public void setGameController(GameController controller) {
        this.controller = controller;
    }

    @Override
    public Move chooseMove(Set<Move> moves) {
        double score;
        double bestScore = Double.MIN_VALUE;
        Move best = null;
        for (Move m : moves) {
            score = scoreMove(m);
            if (bestScore < score) {
                bestScore = score;
                best = m;
            }
        }
        return best;
    }

    private double scoreMove(Move m) {
        // TODO zaimplementować ruchy zgodnie z ANN
        //NetUtil.process();
        return 0;
    }

    @Override
    public EngineType getType() {
        return EngineType.ANN;
    }
}
