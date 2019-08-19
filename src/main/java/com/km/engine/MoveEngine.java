package com.km.engine;

import com.km.game.*;

import java.util.*;

public interface MoveEngine {
    void setGameController(GameController controller);
    Move chooseMove(Set<Move> moves);
    EngineType getType();
}
