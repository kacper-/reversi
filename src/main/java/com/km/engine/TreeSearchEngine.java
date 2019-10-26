package com.km.engine;

import com.km.Config;
import com.km.Logger;
import com.km.entities.Pair;
import com.km.game.*;
import com.km.repos.GameService;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TreeSearchEngine implements MoveEngine {
    private static final double MC_FACTOR_SIM = 1.7d;
    private static final double MC_FACTOR_GAME = 0.8d;
    private static final double MC_DEFAULT_SCORE = -1d;
    private static final double EXPAND_RATIO = 0.75d;
    private static final int MIN_GOOD = 2;
    private int simCount = 0;
    private int wins = 0;
    private int loses = 0;
    private GameController controller;
    private GameController[] sims;

    @Override
    public void setGameController(GameController controller) {
        this.controller = controller;
    }

    public Move chooseMove(Set<Move> moves) {
        if (!controller.isSimulation()) {
            long start = new Date().getTime();
            runSimulations();
            Move m = moveLogic(moves);
            long stop = new Date().getTime();
            Score s = controller.getScore();
            Logger.info(String.format("algo\tprogress = [%d], task count = [%d], sim time = [%d] ms", s.getBlack() + s.getWhite(), getTaskCount(), stop - start));
            return m;
        }
        return moveLogic(moves);
    }

    private Move moveLogic(Set<Move> moves) {
        HistoryItem parent = HistoryItem.fromGB(controller.getGameBoard());
        Map<Pair<String, Integer>, Pair<Integer, Integer>> simulations = GameService.findSimulations(parent.getBoard());
        if (simulations.isEmpty()) {
            return chooseRandomMove(moves);
        } else {
            Map<Pair<Move, Integer>, Pair<Integer, Integer>> simulationMoves = new HashMap<>();
            for (Pair<String, Integer> p : simulations.keySet()) {
                simulationMoves.put(Pair.of(boardDiff(p.getFirst(), parent.getBoard()), p.getSecond()), simulations.get(p));
            }
            return evaluateSimulations(simulationMoves, moves);
        }
    }

    private void runSimulations() {
        Logger.trace("algo\tstarting simulations");
        simCount = 1;
        wins = 0;
        loses = 0;
        Logger.setOff();
        try {
            startSimulationPool();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (GameController c : sims)
            c.updateRepos();
        Logger.setOn();
        Logger.trace(String.format("algo\t[%d] simulations with result = [%d, %d] success ratio = [%.2f]", simCount - 1, wins, loses, ((float) wins) / (float) (loses + wins)));
    }

    private void startSimulationPool() throws InterruptedException {
        ExecutorService executor = Executors.newWorkStealingPool();
        int taskCount = getTaskCount();
        sims = new GameController[taskCount];
        for (int i = 0; i < taskCount; i++) {
            GameController simController = new GameController();
            sims[i] = simController;
            executor.execute(() -> startSingleSimulation(simController));
        }
        executor.shutdown();
        while (!executor.isTerminated())
            Thread.sleep(50);
    }

    private int getTaskCount() {
        Score s = controller.getScore();
        int gameProgress = (s.getBlack() + s.getWhite()) / 10;
        switch (gameProgress) {
            case 0:
                return Config.getSimCountL0();
            case 1:
                return Config.getSimCountL1();
            case 2:
                return Config.getSimCountL2();
            case 3:
                return Config.getSimCountL3();
            case 4:
                return Config.getSimCountL4();
            case 5:
                return Config.getSimCountL5();
            case 6:
                return Config.getSimCountL6();
        }
        return 0;
    }

    private void startSingleSimulation(GameController simController) {
        simController.prepareSimulationGame(controller, EngineType.MC);
        while (!simController.isFinished()) {
            simController.makeMove();
        }
        updateSimResults(simController);
    }

    private void updateSimResults(GameController simController) {
        Score s = simController.getScore();
        if (s.getWinner() == controller.getGameBoard().getTurn())
            wins++;
        else
            loses++;
        simCount++;
    }

    private Move evaluateSimulations(Map<Pair<Move, Integer>, Pair<Integer, Integer>> simulations, Set<Move> moves) {
        Pair<Move, Integer> best = simulations.keySet().iterator().next();
        double score = MC_DEFAULT_SCORE;
        int bigN = getBigN(simulations);
        Logger.trace(String.format("algo\tmoves [%d] simulations [%d]", moves.size(), simulations.size()));
        int good = 0;
        for (Pair<Move, Integer> m : simulations.keySet()) {
            Pair<Integer, Integer> p = simulations.get(m);
            double mcValue = getMCvalue(bigN, p.getFirst(), p.getSecond());
            Logger.debug(String.format("algo\toption from simulation node id = [%d] mcval = [%f]", m.getSecond(), mcValue));
            if (mcValue > score) {
                best = m;
                score = mcValue;
            }
            if (mcValue > EXPAND_RATIO)
                good++;
        }
        if (controller.isSimulation() && (good < MIN_GOOD) && (moves.size() > simulations.size())) {
            return expand(simulations, moves);
        } else {
            Logger.trace(String.format("algo\tchoosing from simulation node id = [%d]", best.getSecond()));
            return best.getFirst();
        }
    }

    private Move expand(Map<Pair<Move, Integer>, Pair<Integer, Integer>> simulations, Set<Move> moves) {
        Set<Move> nonSimulated = new HashSet<>(moves);
        for (Pair<Move, Integer> p : simulations.keySet()) {
            Move m = p.getFirst();
            nonSimulated.remove(m);
        }
        return chooseRandomMove(nonSimulated);
    }

    private Move chooseRandomMove(Set<Move> moves) {
        Logger.debug(String.format("algo\t[%d] random options", moves.size()));
        for (Move m : moves) {
            Logger.debug(String.format("algo\toption move = [%d, %d]", m.getI(), m.getJ()));
        }
        Move move = new ArrayList<>(moves).get(new Random().nextInt(moves.size()));
        Logger.trace(String.format("algo\tchoosing random move = [%d, %d] from [%d] options", move.getI(), move.getJ(), moves.size()));
        return move;
    }

    private double getMCvalue(int bigN, int wins, int loses) {
        int n = wins + loses;
        double ar = ((double) wins) / ((double) n);
        double factor = controller.isSimulation() ? MC_FACTOR_SIM : MC_FACTOR_GAME;
        double br = factor * Math.sqrt(Math.log(bigN) / ((double) n));
        return ar + br;
    }

    private int getBigN(Map<Pair<Move, Integer>, Pair<Integer, Integer>> simulations) {
        int n = 0;
        for (Pair<Integer, Integer> p : simulations.values()) {
            n += p.getFirst() + p.getSecond();
        }
        return n;
    }

    private Move boardDiff(String newBoard, String board) {
        for (int i = 0; i < newBoard.length(); i++) {
            if (newBoard.charAt(i) != DBSlot.EMPTY.getSymbol() && board.charAt(i) == DBSlot.EMPTY.getSymbol()) {
                return new Move(i % GameBoard.SIZE, i / GameBoard.SIZE, controller.getGameBoard().getTurn());
            }
        }
        return null;
    }
}
