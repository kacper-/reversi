package com.km.repos;

import com.km.Logger;
import com.km.entities.Nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class NodesRepo {
    private static int id = -1;
    private static Map<String, Nodes> map = new HashMap<>();
    private static List<Nodes> list = new ArrayList<>();

    static List<Nodes> getNodes() {
        return list;
    }

    static Nodes findByBoard(String board) {
        return map.get(board);
    }

    static Nodes save(Nodes node) {
        id++;
        Nodes n = new Nodes(id, node.getBoard(), node.getWins(), node.getLoses());
        list.add(id, n);
        map.put(n.getBoard(), n);
        return n;
    }

    static void update(Nodes node) {
        list.set(node.getId(), node);
        map.put(node.getBoard(), node);
    }

    static Nodes findById(int nodeId) {
        return list.get(nodeId);
    }

    static void clear() {
        id = -1;
        map = new HashMap<>();
        list = new ArrayList<>();
    }

    static void printStats() {
        Logger.trace(String.format("db\tnodes map count = [%d]", map.size()));
        Logger.trace(String.format("db\tnodes list count = [%d]", list.size()));
    }
}