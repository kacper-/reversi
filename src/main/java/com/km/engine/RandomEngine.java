package com.km.engine;

import com.km.game.GameController;
import com.km.game.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomEngine implements MoveEngine {
    private GameController controller;

    @Override
    public void setGameController(GameController controller) {
        this.controller = controller;
    }

    @Override
    public Move chooseMove(Set<Move> moves) {
        List<Move> list = new ArrayList<>(moves);
        return list.get(new Random().nextInt(moves.size()));
    }

    @Override
    public void afterGame() {
        return;
    }
}
