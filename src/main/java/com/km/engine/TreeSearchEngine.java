package com.km.engine;

import com.km.entities.Nodes;
import com.km.game.*;
import com.km.repos.GameService;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TreeSearchEngine implements MoveEngine {
    private static final double MC_FACTOR = 1.4d;
    private static final double MC_DEFAULT_SCORE = -1d;
    private static final double EXPAND_RATIO = 0.75d;
    private static final int MIN_GOOD = 2;
    private final int[] tCount = new int[]{3500, 2500, 3500, 3500, 3500, 3500, 2500, 500};
    private GameController controller;
    private GameController[] sims;

    @Override
    public void setGameController(GameController controller) {
        this.controller = controller;
    }

    public Move chooseMove(Set<Move> moves) {
        if (!controller.isSimulation()) {
            runSimulations();
        }
        return moveLogic(moves);
    }

    private Move moveLogic(Set<Move> moves) {
        HistoryItem parent = HistoryItem.fromGB(controller.getGameBoard());
        Set<Nodes> simulations = GameService.findSimulations(parent.getBoard());
        if (simulations.isEmpty()) {
            return chooseRandomMove(moves);
        } else {
            Map<Move, Nodes> simulationMoves = new HashMap<>();
            for (Nodes p : simulations) {
                simulationMoves.put(boardDiff(p.getBoard(), parent.getBoard()), p);
            }
            return evaluateSimulations(simulationMoves, moves);
        }
    }

    private Move chooseRandomMove(Set<Move> moves) {
        return new ArrayList<>(moves).get(ThreadLocalRandom.current().nextInt(moves.size()));
    }

    private void runSimulations() {
        try {
            startSimulationPool();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (GameController c : sims)
            c.updateRepos();
    }

    private void startSimulationPool() throws InterruptedException {
        ExecutorService executor = Executors.newWorkStealingPool();
        int taskCount = getTaskCount();
        sims = new GameController[taskCount];
        for (int i = 0; i < taskCount; i++) {
            GameController simController = new GameController();
            sims[i] = simController;
            executor.execute(() -> startSingleSimulation(simController, new TreeSearchEngine()));
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
    }

    private int getTaskCount() {
        Score s = controller.getScore();
        return tCount[(s.getBlack() + s.getWhite()) / tCount.length];
    }

    private void startSingleSimulation(GameController simController, TreeSearchEngine engine) {
        simController.prepareSimulationGame(controller, engine);
        while (!simController.isFinished()) {
            simController.makeMove();
            simController.makeRandomMove();
        }
    }

    private Move evaluateSimulations(Map<Move, Nodes> simulations, Set<Move> moves) {
        Move best = simulations.keySet().iterator().next();
        double score = MC_DEFAULT_SCORE;
        int bigN = getBigN(simulations);
        int good = 0;
        for (Move m : simulations.keySet()) {
            Nodes p = simulations.get(m);
            double mcValue = getMCvalue(bigN, p.getWins(), p.getLoses());
            if (mcValue > score) {
                best = m;
                score = mcValue;
            }
            if (getMoveWLScore(p.getWins(), p.getLoses()) > EXPAND_RATIO)
                good++;
        }
        if (controller.isSimulation() && (good < MIN_GOOD) && (moves.size() > simulations.size())) {
            return expand(simulations, moves);
        } else {
            return best;
        }
    }

    private double getMoveWLScore(int wins, int loses) {
        double result = 0d;
        if (wins + loses > 1) {
            result = ((double) wins) / ((double) (wins + loses));
        }
        return result;
    }

    private Move expand(Map<Move, Nodes> simulations, Set<Move> moves) {
        Set<Move> nonSimulated = new HashSet<>(moves);
        for (Move m : simulations.keySet()) {
            nonSimulated.remove(m);
        }
        return chooseRandomMove(nonSimulated);
    }

    private double getMCvalue(int bigN, int wins, int loses) {
        double n = wins + loses;
        double ar = ((double) wins) / n;
        double br = MC_FACTOR * Math.sqrt(Math.log(bigN) / n);
        return ar + br;
    }

    private int getBigN(Map<Move, Nodes> simulations) {
        int n = 0;
        for (Nodes p : simulations.values())
            n += p.getWins() + p.getLoses();
        return n;
    }

    private Move boardDiff(String newBoard, String board) {
        Character e = DBSlot.EMPTY.getSymbol();
        for (int i = 0; i < GameBoard.S2; i++) {
            if (newBoard.charAt(i) != e && board.charAt(i) == e) {
                return new Move(GameBoard.getI(i), GameBoard.getJ(i));
            }
        }
        return null;
    }
}
