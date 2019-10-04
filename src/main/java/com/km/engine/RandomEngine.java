package com.km.engine;

import com.km.Logger;
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
        Move m = list.get(new Random().nextInt(moves.size()));
        Logger.trace(String.format("algo\tchoosing [%d, %d]", m.getI(), m.getJ()));
        return m;
    }

    @Override
    public void afterGame() {
        return;
    }
}