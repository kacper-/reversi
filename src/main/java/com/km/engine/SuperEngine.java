package com.km.engine;

import com.km.Logger;
import com.km.game.GameController;
import com.km.game.Move;
import com.km.nn.NetUtil;

import java.util.Set;

public class SuperEngine implements MoveEngine {
    private RuleEngine rule = new RuleEngine();
    private ANNEngine ann = new ANNEngine(NetUtil.NET_VERSION);

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
        Logger.trace(String.format("algo\tnumber of available moves = [%d]", moves.size()));
        for (Move m : moves) {
            score = rule.scoreMove(m) + ann.scoreMove(m);
            Logger.debug(String.format("algo\tmove = [%d, %d] score = [%f]", m.getI(), m.getJ(), score));
            if (bestScore < score) {
                bestScore = score;
                best = m;
            }
        }
        Logger.trace(String.format("algo\tchoosing [%d, %d] score = [%f]", best.getI(), best.getJ(), bestScore));
        return best;
    }
}
