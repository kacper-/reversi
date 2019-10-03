package com.km.repos;

import com.km.Logger;
import com.km.entities.Moves;

import java.util.*;

class MovesRepo {
    private static Map<Integer, List<Moves>> map = new HashMap<>();

    static List<Moves> findByParent(int snodeId) {
        return map.get(snodeId);
    }

    static void save(Moves m) {
        List<Moves> list;
        if (map.containsKey(m.getSnode())) {
            list = map.get(m.getSnode());
            if (list.contains(m))
                return;
        } else {
            list = new ArrayList<>();
        }
        list.add(m);
        map.put(m.getSnode(), list);
    }

    static void clear() {
        map = new HashMap<>();
    }

    static void printStats() {
        Logger.trace(String.format("db\tmoves map count = [%d]", map.size()));
    }

}
