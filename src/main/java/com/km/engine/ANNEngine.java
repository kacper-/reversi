package com.km.engine;

import com.km.Logger;
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
        double bestScore = Integer.MIN_VALUE;
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
        Logger.debug(String.format("algo\tbest = [%d, %d] score = [%f]", best.getI(), best.getJ(), bestScore));
        return best;
    }

    private double scoreMove(Move m) {
        GameController copy = controller.copy();
        Logger.setDebugOff();
        copy.updateBoard(m);
        copy.nextTurn();
        Logger.setDebugOn();
        String board = copy.getGameBoard().toDBString();
        return NetUtil.process(board);
    }

    @Override
    public EngineType getType() {
        return EngineType.ANN;
    }
}
