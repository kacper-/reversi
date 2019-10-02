package com.km.engine;

import com.km.Logger;
import com.km.game.GameController;
import com.km.game.GameRules;
import com.km.game.Move;

import java.util.Set;

public class RuleEngine implements MoveEngine {
    private GameController controller;

    @Override
    public void setGameController(GameController controller) {
        this.controller = controller;
    }

    @Override
    public Move chooseMove(Set<Move> moves) {
        double score;
        double bestScore = -Double.MAX_VALUE;
        Move best = null;
        Logger.debug(String.format("algo\tnumber of available moves = [%d]", moves.size()));
        for (Move m : moves) {
            score = scoreMove(m);
            Logger.debug(String.format("algo\tmove = [%d, %d] score = [%f]", m.getI(), m.getJ(), score));
            if (bestScore < score) {
                bestScore = score;
                best = m;
            }
        }
        Logger.debug(String.format("algo\tchoosing [%d, %d] score = [%f]", best.getI(), best.getJ(), bestScore));
        return best;
    }

    private double scoreMove(Move m) {
        if (isCorner(m))
            return 1d;
        // TODO implement
        return 0d;
    }

    private boolean isCorner(Move m) {
        Move e = new Move(m.getI(), m.getJ(), null);
        return GameRules.getCorners().contains(e);
    }

    @Override
    public void afterGame() {
        return;
    }
}
