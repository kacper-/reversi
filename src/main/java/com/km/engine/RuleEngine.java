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
        Logger.trace(String.format("algo\tnumber of available moves = [%d]", moves.size()));
        for (Move m : moves) {
            score = scoreMove(m);
            Logger.debug(String.format("algo\tmove = [%d, %d] score = [%f]", m.getI(), m.getJ(), score));
            if (bestScore < score) {
                bestScore = score;
                best = m;
            }
        }
        Logger.trace(String.format("algo\tchoosing [%d, %d] score = [%f]", best.getI(), best.getJ(), bestScore));
        return best;
    }

    private double scoreMove(Move m) {
        if (isCorner(m))
            return 1d;
        double risk = calculateRisk(m);
        if (risk < 0)
            return risk;
        int count = countGain(m);
        return ((double) count) / 64d;
    }

    private int countGain(Move m) {
        // TODO implement
        return 0;
    }

    private double calculateRisk(Move m) {
        // TODO implement
        return 1d;
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
