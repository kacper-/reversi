package com.km.engine;

import com.km.Logger;
import com.km.game.GameController;
import com.km.game.GameRules;
import com.km.game.Move;
import com.km.game.Slot;

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
        Logger.setOff();
        if (isCorner(m))
            return 1d;
        double risk = calculateRisk(m);
        if (risk < 0)
            return risk;
        int count = countGain(m);
        Logger.setOn();
        return ((double) count) / 64d;
    }

    private int countGain(Move m) {
        Slot turn = controller.getGameBoard().getTurn();
        int start = turn == Slot.BLACK ? controller.getScore().getBlack() : controller.getScore().getWhite();
        GameController copy = controller.copy();
        copy.updateBoard(m);
        int end = turn == Slot.BLACK ? copy.getScore().getBlack() : copy.getScore().getWhite();
        return end - start;
    }

    private double calculateRisk(Move m) {
        double exposeCorner = calculateExposedCorner(m);
        if (exposeCorner < 0)
            return exposeCorner;
        double exposeSemiCorner = calculateExposedSemiCorner(m);
        if (exposeSemiCorner < 0)
            return exposeSemiCorner;
        return 1d;
    }

    private double calculateExposedSemiCorner(Move m) {
        int i = GameRules.getSemiCorners().indexOf(GameRules.toSimpleMove(m));
        if (i > -1) {
            Move cc = GameRules.getCorners().get(i);
            if (controller.getGameBoard().getValue(cc.getI(), cc.getJ()) == Slot.EMPTY)
                return -0.5d;
        }
        return 1d;
    }

    private double calculateExposedCorner(Move m) {
        GameController copy = controller.copy();
        copy.updateBoard(m);
        Set<Move> moves = GameRules.getAvailableMoves(copy.getGameBoard());
        for (Move move : moves) {
            if (isCorner(move))
                return -1d;
        }
        return 1d;
    }

    private boolean isCorner(Move m) {
        return GameRules.getCorners().contains(GameRules.toSimpleMove(m));
    }

    @Override
    public void afterGame() {
        return;
    }
}
