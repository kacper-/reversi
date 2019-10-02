package com.km.engine;

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
    private static final long SIM_TIME = 1000;
    private static final int SIM_COUNT_L1 = 10000;
    private static final int SIM_COUNT_L2 = 2500;
    private static final int SIM_COUNT_L3 = 200;
    private static final int SIM_COUNT_L4 = 10;
    private static final int SIM_L2 = 53;
    private static final int SIM_L3 = 57;
    private static final int SIM_L4 = 60;
    private static final int SIM_HB = 100;
    private static final int CORES = Runtime.getRuntime().availableProcessors() < 3 ? 1 : Runtime.getRuntime().availableProcessors() - 2;
    private int simCount = 0;
    private int wins = 0;
    private int loses = 0;
    private GameController controller;

    @Override
    public void setGameController(GameController controller) {
        this.controller = controller;
        Logger.debug(String.format("algo\tpreparing for [%d] cores", CORES));
    }

    public Move chooseMove(Set<Move> moves) {
        if (!controller.isSimulation())
            runSimulations();
        HistoryItem parent = HistoryItem.fromGB(controller.getGameBoard());
        Map<Pair<String, Integer>, Pair<Integer, Integer>> simulations = GameService.findSimulations(parent);
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
        Logger.debug("algo\tstarting simulations");
        simCount = 1;
        wins = 0;
        loses = 0;
        long start = new Date().getTime();
        while (continueSim(start, simCount)) {
            Logger.setDebugOff();
            startSimulationPool();
            Logger.setDebugOn();
        }
        Logger.debug(String.format("algo\t[%d] simulations with result = [%d, %d] success ratio = [%.2f]", simCount - 1, wins, loses, ((float) wins) / (float) (loses + wins)));
    }

    private void startSimulationPool() {
        ExecutorService executor = Executors.newFixedThreadPool(CORES);
        int taskCount = CORES * SIM_HB;
        for (int i = 0; i < taskCount; i++) {
            executor.execute(this::startSingleSimulation);
        }
        executor.shutdown();
        while (!executor.isTerminated());
    }

    private void startSingleSimulation() {
        GameController simController = new GameController();
        simController.prepareSimulationGame(controller, EngineType.TREE);
        while (!simController.isFinished()) {
            simController.makeMove();
        }
        updateSimResults(simController.getScore());
    }

    private void updateSimResults(Score s) {
        if (s.getWinner() == controller.getGameBoard().getTurn())
            wins++;
        else
            loses++;
        simCount++;
    }

    private boolean continueSim(long start, int count) {
        long stop = new Date().getTime();
        if (count > SIM_COUNT_L1 || (stop - start) > SIM_TIME)
            return false;
        Score s = controller.getScore();
        int gameProgress = s.getBlack() + s.getWhite();
        if (gameProgress > SIM_L2 && count > SIM_COUNT_L2)
            return false;
        if (gameProgress > SIM_L3 && count > SIM_COUNT_L3)
            return false;
        return !(gameProgress > SIM_L4 && count > SIM_COUNT_L4);
    }


    private Move evaluateSimulations(Map<Pair<Move, Integer>, Pair<Integer, Integer>> simulations, Set<Move> moves) {
        Pair<Move, Integer> best = simulations.keySet().iterator().next();
        double score = MC_DEFAULT_SCORE;
        int bigN = getBigN(simulations);
        Logger.debug(String.format("algo\tmoves [%d] simulations [%d]", moves.size(), simulations.size()));
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
            Logger.debug(String.format("algo\tchoosing from simulation node id = [%d]", best.getSecond()));
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
        Logger.debug("algo\trandom options");
        for (Move m : moves) {
            Logger.debug(String.format("algo\toption move = [%d, %d]", m.getI(), m.getJ()));
        }
        Move move = new ArrayList<>(moves).get(new Random().nextInt(moves.size()));
        Logger.debug(String.format("algo\tchoosing random move = [%d, %d] from [%d] options", move.getI(), move.getJ(), moves.size()));
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

    @Override
    public void afterGame() {
        return;
    }

}
