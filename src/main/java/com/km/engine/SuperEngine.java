package com.km.engine;

import com.km.game.GameController;
import com.km.game.Move;

import java.util.Set;

// TODO implement
public class SuperEngine implements MoveEngine {
    private GameController controller;

    @Override
    public void setGameController(GameController controller) {
        this.controller = controller;
    }

    @Override
    public Move chooseMove(Set<Move> moves) {
        return null;
    }

    @Override
    public boolean isSimRequired() {
        return true;
    }
}
