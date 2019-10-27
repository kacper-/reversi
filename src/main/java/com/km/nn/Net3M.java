package com.km.nn;

import java.io.Serializable;

public class Net3M implements Serializable, Net {
    private final static int NET_SIZE = 64;
    private static final int SIZE = 8;
    private Net3[] net = new Net3[SIZE + 1];

    Net3M() {
        for (int i = 0; i < SIZE; i++)
            net[i] = new Net3();
    }

    int index(double[] signal) {
        int i = 0;
        for (double v : signal) {
            if (isZero(v))
                i++;
        }
        return (NET_SIZE - i) / SIZE;
    }

    private boolean isZero(double d) {
        return Math.abs(d) < 0.01d;
    }

    @Override
    public double process(double[] signal) {
        return net[index(signal)].process(signal);
    }

    @Override
    public void teach(double[] signal, double expected) {
        net[index(signal)].teach(signal, expected);
    }

    @Override
    public int getSize() {
        return NET_SIZE;
    }
}
