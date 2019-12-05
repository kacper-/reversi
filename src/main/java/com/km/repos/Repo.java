package com.km.repos;

import com.km.entities.Nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Repo implements Serializable {
    private List<Nodes> nodesList = new ArrayList<>();

    public void addNodesList(List<Nodes> nodes) {
        nodesList.addAll(nodes);
    }

    public List<Nodes> getNodesList() {
        return nodesList;
    }
}
