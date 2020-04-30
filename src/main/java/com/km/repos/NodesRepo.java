package com.km.repos;

import com.km.entities.Nodes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class NodesRepo {
    static Map<String, Nodes> map = new HashMap<>();

    static Collection<Nodes> getNodes() {
        return map.values();
    }

    static Nodes findByBoard(String board) {
        return map.get(board);
    }

    static Nodes save(Nodes node) {
        Nodes n = new Nodes(node.getBoard(), node.getWins(), node.getLoses());
        map.put(n.getBoard(), n);
        return n;
    }

    static void update(Nodes node) {
        map.put(node.getBoard(), node);
    }

    static void clear() {
        map = new HashMap<>();
    }
}