package com.km.repos;

import com.km.entities.Moves;
import com.km.entities.Nodes;
import com.km.game.HistoryItem;
import com.km.nn.NetUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            MovesRepo.save(new Moves(snode, node));
            addGameNode(item.getParent(), w, l);
        }
    }

    public static void updateScores(List<HistoryItem> historyWhite, List<HistoryItem> historyBlack, int winsWhite, int losesWhite, int winsBlack, int losesBlack) {
        updateHistory(historyWhite, winsWhite, losesWhite);
        updateHistory(historyBlack, winsBlack, losesBlack);
    }

    private static void updateHistory(List<HistoryItem> history, int wins, int loses) {
        for (HistoryItem historyItem : history) {
            addGameNode(historyItem, wins, loses);
        }
    }

    public static Set<Nodes> findSimulations(String board) {
        Set<Nodes> simulations = new HashSet<>();
        Nodes node = NodesRepo.findByBoard(board);
        if (node != null) {
            List<Moves> moves = MovesRepo.findByParent(node);
            if (moves != null) {
                for (Moves move : moves) {
                    simulations.add(move.getEnode());
                }
                return simulations;
            }
        }
        return simulations;
    }

    public static Collection<Nodes> getNodes() {
        return NodesRepo.getNodes();
    }

    public static void filter() {
        Set<Nodes> toDelete = new HashSet<>();
        for (Nodes n : NodesRepo.map.values()) {
            if (!NetUtil.validate(n))
                toDelete.add(n);
        }
        System.out.println("Before = " + NodesRepo.map.size());
        System.out.println("toDelete = " + toDelete.size());
        for (Nodes n : toDelete) {
            MovesRepo.map.remove(n);
            NodesRepo.map.remove(n.getBoard());
        }
        System.out.println("After  = " + NodesRepo.map.size());
    }

    public static void clear() {
        MovesRepo.clear();
        NodesRepo.clear();
    }
}
