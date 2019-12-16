package com.km.repos;

import com.km.entities.Nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Repo implements Serializable {
    private List<List<Nodes>> list = new ArrayList<>();

    public void addNodesList(List<Nodes> nodes) {
        list.add(nodes);
    }

    public List<Nodes> getNodes(int i) {
        return list.get(i);
    }

    public int getNodesListCount() {
        return list.size();
    }

    public int getLastCount() {
        return list.get(list.size() - 1).size();
    }
}
