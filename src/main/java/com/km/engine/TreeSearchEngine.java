package com.km.engine;

import com.km.Config;
import com.km.Logger;
import com.km.entities.Pair;
import com.km.game.*;
import com.km.repos.GameService;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class TreeSearchEngine implements MoveEngine {
    private static final boolean RANDOM = false;
    private static final double MC_FACTOR = 1.4d;
    private static final double MC_DEFAULT_SCORE = -1d;
    private static final double EXPAND_RATIO = 0.75d;
    private static final int MIN_GOOD = 2;
    private int simCount = 0;
    private int wins = 0;
    private int loses = 0;
    private GameController controller;
    private GameController[] sims;
    private int[] tCount = new int[8];

    TreeSearchEngine() {
        loadConfig();
    }

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
            return chooseNewMove(moves);
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
        return tCount[(s.getBlack() + s.getWhite()) / 16];
    }

    private void loadConfig() {
        tCount[0] = Config.getSimCountL0();
        tCount[1] = Config.getSimCountL1();
        tCount[2] = Config.getSimCountL2();
        tCount[3] = Config.getSimCountL3();
        tCount[4] = Config.getSimCountL4();
        tCount[5] = Config.getSimCountL5();
        tCount[6] = Config.getSimCountL6();
        tCount[7] = Config.getSimCountL7();
    }

    private void startSingleSimulation(GameController simController) {
        simController.prepareSimulationGame(controller, EngineType.MC);
        while (!simController.isFinished()) {
            simController.makeMove();
            simController.makeRandomMove();
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
        int good = 0;
        for (Pair<Move, Integer> m : simulations.keySet()) {
            Pair<Integer, Integer> p = simulations.get(m);
            double mcValue = getMCvalue(bigN, p.getFirst(), p.getSecond());
            if (mcValue > score) {
                best = m;
                score = mcValue;
            }
            if (getMoveWLScore(p.getFirst(), p.getSecond()) > EXPAND_RATIO)
                good++;
        }
        if (controller.isSimulation() && (good < MIN_GOOD) && (moves.size() > simulations.size())) {
            return expand(simulations, moves);
        } else {
            return best.getFirst();
        }
    }

    private double getMoveWLScore(int wins, int loses) {
        double result = 0d;
        if (wins + loses > 1) {
            result = ((double) wins) / ((double) (wins + loses));
        }
        return result;
    }

    private Move expand(Map<Pair<Move, Integer>, Pair<Integer, Integer>> simulations, Set<Move> moves) {
        Set<Move> nonSimulated = new HashSet<>(moves);
        for (Pair<Move, Integer> p : simulations.keySet()) {
            Move m = p.getFirst();
            nonSimulated.remove(m);
        }
        return chooseNewMove(nonSimulated);
    }

    private Move chooseNewMove(Set<Move> moves) {
        if (RANDOM) {
            return new ArrayList<>(moves).get(ThreadLocalRandom.current().nextInt(moves.size()));
        }
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

    private double scoreMove(Move m) {
        if (isCorner(m))
            return 1d;
        double risk = calculateRisk(m);
        if (risk < 0)
            return risk;
        int count = countGain(m);
        return ((double) count) / 64d;
    }

    private int countGain(Move m) {
        Slot turn = controller.getGameBoard().getTurn();
        int start = turn == Slot.BLACK ? controller.getScore().getBlack() : controller.getScore().getWhite();
        GameController copy = controller.copy();
        copy.updateBoard(m);
        int end = turn == Slot.BLACK ? copy.getScore().getBlack() : copy.getScore().getWhite();
        return end - start;
    }

    private double calculateRisk(Move m) {
        double exposeCorner = calculateExposedCorner(m);
        if (exposeCorner < 0)
            return exposeCorner;
        double exposeSemiCorner = calculateExposedSemiCorner(m);
        if (exposeSemiCorner < 0)
            return exposeSemiCorner;
        return 1d;
    }

    private double calculateExposedSemiCorner(Move m) {
        int i = GameRules.getSemiCorners().indexOf(GameRules.toSimpleMove(m));
        if (i > -1) {
            Move cc = GameRules.getCorners().get(i);
            if (controller.getGameBoard().getValue(cc.getI(), cc.getJ()) == Slot.EMPTY)
                return -0.5d;
        }
        return 1d;
    }

    private double calculateExposedCorner(Move m) {
        GameController copy = controller.copy();
        copy.updateBoard(m);
        Set<Move> moves = GameRules.getAvailableMoves(copy.getGameBoard());
        for (Move move : moves) {
            if (isCorner(move))
                return -1d;
        }
        return 1d;
    }

    private boolean isCorner(Move m) {
        return GameRules.getCorners().contains(GameRules.toSimpleMove(m));
    }

    private double getMCvalue(int bigN, int wins, int loses) {
        double n = wins + loses;
        double ar = ((double) wins) / n;
        double br = MC_FACTOR * Math.sqrt(Math.log(bigN) / n);
        return ar + br;
    }

    private int getBigN(Map<Pair<Move, Integer>, Pair<Integer, Integer>> simulations) {
        int n = 0;
        for (Pair<Integer, Integer> p : simulations.values())
            n += p.getFirst() + p.getSecond();
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
