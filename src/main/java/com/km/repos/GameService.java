package com.km.repos;

import com.km.Logger;
import com.km.entities.Moves;
import com.km.entities.Nodes;
import com.km.entities.Pair;
import com.km.game.HistoryItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameService {

    private static void addGameNode(HistoryItem item, int w, int l) {
        Nodes node = NodesRepo.findByBoard(item.getBoard());
        if (node == null) {
            node = NodesRepo.save(new Nodes(item.getBoard(), w, l));
        } else {
            node.setWins(node.getWins() + w);
            node.setLoses(node.getLoses() + l);
            NodesRepo.update(node);
        }
        if (item.getParent() != null) {
            Nodes snode = NodesRepo.findByBoard(item.getParent().getBoard());
            if (snode == null) {
                snode = NodesRepo.save(new Nodes(item.getParent().getBoard(), w, l));
            }
            MovesRepo.save(new Moves(snode.getId(), node.getId()));
        }
    }

    public static void updateScores(List<HistoryItem> historyWhite, List<HistoryItem> historyBlack, int winsWhite, int losesWhite, int winsBlack, int losesBlack) {
        updateHistory(historyWhite, winsWhite, losesWhite);
        //Logger.trace("db\thistoryWhite saved");
        updateHistory(historyBlack, winsBlack, losesBlack);
        //Logger.trace("db\thistoryBlack saved");
    }

    private static void updateHistory(List<HistoryItem> history, int wins, int loses) {
        for (HistoryItem historyItem : history) {
            addGameNode(historyItem, wins, loses);
        }
    }

    public static Map<Pair<String, Integer>, Pair<Integer, Integer>> findSimulations(String board) {
        Map<Pair<String, Integer>, Pair<Integer, Integer>> simulations = new HashMap<>();
        Nodes node = NodesRepo.findByBoard(board);
        if (node != null) {
            List<Moves> moves = MovesRepo.findByParent(node.getId());
            if (moves != null) {
                //Logger.debug(String.format("db\tfound simulation at node id = [%d]", node.getId()));
                for (Moves move : moves) {
                    Nodes n = NodesRepo.findById(move.getEnode());
                    //Logger.debug(String.format("db\tfound end node id = [%d] wins = [%d] loses = [%d]", n.getId(), n.getWins(), n.getLoses()));
                    simulations.put(Pair.of(n.getBoard(), n.getId()), Pair.of(n.getWins(), n.getLoses()));
                }
                return simulations;
            } else {
                //Logger.debug(String.format("db\tno move for snode = [%d]", node.getId()));
            }
        } else {
            //Logger.debug(String.format("db\tno node for board = [%s]", board));
        }
        return simulations;
    }

    public static List<Nodes> getNodes() {
        return NodesRepo.getNodes();
    }

    public static void clear() {
        MovesRepo.clear();
        NodesRepo.clear();
    }

    public static void printStats() {
        //Logger.trace("db\tstatistics:");
        //NodesRepo.printStats();
        //MovesRepo.printStats();
    }
}
