package com.km.repos;

import com.km.entities.Nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Repo implements Serializable {
    private List<Nodes> list = new ArrayList<>();

    public void addNodesList(Nodes node) {
        list.add(node);
    }

    public List<Nodes> getNodes() {
        return list;
    }
}
