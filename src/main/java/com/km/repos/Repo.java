package com.km.repos;

import com.km.entities.Nodes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Repo implements Serializable {
    private Map<String, Nodes> map = new HashMap<>();

    public void addNodesList(Nodes node) {
        if (map.containsKey(node.getBoard())) {
            Nodes n = map.get(node.getBoard());
            n.setWins(n.getWins() + node.getWins());
            n.setLoses(n.getLoses() + node.getLoses());
            map.put(n.getBoard(), n);
        } else
            map.put(node.getBoard(), node);
    }

    public Map<String, Nodes> getNodesMap() {
        return map;
    }
}
