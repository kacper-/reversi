package com.km.repos;

import com.km.entities.Moves;
import com.km.entities.Nodes;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Repo implements Serializable {
    private Map<Integer, List<Moves>> movesMap;
    private Map<String, Nodes> nodesMap;
    private List<Nodes> nodesList;

    public Repo(Map<Integer, List<Moves>> movesMap, Map<String, Nodes> nodesMap, List<Nodes> nodesList) {
        this.movesMap = movesMap;
        this.nodesMap = nodesMap;
        this.nodesList = nodesList;
    }

    public Map<Integer, List<Moves>> getMovesMap() {
        return movesMap;
    }

    public Map<String, Nodes> getNodesMap() {
        return nodesMap;
    }

    public List<Nodes> getNodesList() {
        return nodesList;
    }
}
