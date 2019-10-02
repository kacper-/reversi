package com.km.engine;

import com.km.game.GameController;
import com.km.game.Move;

import java.util.Set;

public interface MoveEngine {
    void setGameController(GameController controller);

    Move chooseMove(Set<Move> moves);

    void afterGame();
}
