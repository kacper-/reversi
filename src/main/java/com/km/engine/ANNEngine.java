package com.km.engine;

import com.km.game.GameController;
import com.km.game.Move;
import com.km.nn.NetUtil;
import com.km.nn.NetVersion;

import java.util.Set;

public class ANNEngine implements MoveEngine {
    private GameController controller;
    private NetUtil netUtil;

    ANNEngine(NetVersion version, String name, String dataFile) {
        netUtil = new NetUtil(version, name, dataFile);
    }

    @Override
    public void setGameController(GameController controller) {
        this.controller = controller;
    }

    @Override
    public Move chooseMove(Set<Move> moves) {
        double score;
        double bestScore = -Double.MAX_VALUE;
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

    double scoreMove(Move m) {
        GameController copy = controller.copy();
        copy.updateBoard(m);
        copy.nextTurn();
        String board = copy.getGameBoard().toDBString();
        return netUtil.process(board);
    }
}
