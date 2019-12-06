package com.km.repos;

import com.km.entities.Nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Repo implements Serializable {
    // TODO switch to Map<String, Nodes> instead of List
    private List<Nodes> nodesList = new ArrayList<>();

    public void addNodesList(Nodes node) {
        nodesList.add(node);
    }

    public List<Nodes> getNodesList() {
        return nodesList;
    }
}
