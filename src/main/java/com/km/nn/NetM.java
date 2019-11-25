package com.km.nn;

import java.io.Serializable;

public class NetM implements Serializable, Net {
    private final static int NET_SIZE = 64;
    private static final int SIZE = 8;
    private Net[] net = new Net[SIZE];
    private int[] trainCounter = new int[SIZE];

    @Override
    public int getSegments() {
        return SIZE;
    }

    NetM(NetVersion v) {
        for (int i = 0; i < SIZE; i++) {
            switch (v) {
                case NET3:
                    net[i] = new Net3();
                    break;
                case NET4:
                    net[i] = new Net4();
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public int[] report() {
        return trainCounter;
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
        int idx = index(signal);
        if (idx < SIZE)
            return net[index(signal)].process(signal);
        else
            return 0d;
    }

    @Override
    public void teach(double[] signal, double expected) {
        int idx = index(signal);
        net[idx].teach(signal, expected);
        trainCounter[idx]++;
    }

    @Override
    public int getSize() {
        return NET_SIZE;
    }
}
