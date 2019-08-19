package com.km.engine;

import com.km.Logger;
import com.km.entities.Pair;
import com.km.game.*;
import com.km.nn.NetUtil;
import com.km.repos.GameService;

import java.util.*;

public class TreeSearchEngine implements MoveEngine {
    private static final double MC_FACTOR_SIM = 1.7d;
    private static final double MC_FACTOR_GAME = 0.8d;
    private static final double MC_DEFAULT_SCORE = -1d;
    private static final double EXPAND_RATIO = 0.75d;
    private static final int MIN_GOOD = 2;
    private GameController controller;

    @Override
    public void setGameController(GameController controler) {
        this.controller = controler;
    }

    public Move chooseMove(Set<Move> moves) {
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

    @Override
    public EngineType getType() {
        return EngineType.TREE_SEARCH;
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
            Logger.debug(String.format("algo\toption from simulation node id = [%d] mcval = [%f] ann = [%f]", m.getSecond(), mcValue, NetUtil.process(GameService.getNode(m.getSecond()).getBoard())));
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
}
