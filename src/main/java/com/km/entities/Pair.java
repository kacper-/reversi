package com.km.entities;

public class Pair<V, K> {
    private V first;
    private K second;

    private Pair(V first, K second) {
        this.first = first;
        this.second = second;
    }

    public static <V, K> Pair<V, K> of(V first, K second) {
        return new Pair<>(first, second);
    }

    public V getFirst() {
        return first;
    }

    public K getSecond() {
        return second;
    }
}
