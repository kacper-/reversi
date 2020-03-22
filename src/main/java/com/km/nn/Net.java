package com.km.nn;

public interface Net {
    double process(double[] signal);

    void teach(double[] signal, double expected);

    int getSize();

    default int[] report() {
        return new int[8];
    }

    default double expected(double[] n) {
        double wins = n[0];
        double loses = n[1];
        if (loses > wins) {
            return -(1d - Math.sqrt(wins / loses));
        } else {
            return 1d - Math.sqrt(loses / wins);
        }
    }

    default double expected2(double[] n) {
        double wins = n[0];
        double loses = n[1];
        if (loses > wins) {
            return -(1d - Math.pow(wins / loses, 2d));
        } else {
            return 1d - Math.pow(loses / wins, 2d);
        }
    }

    default int getSegments() {
        return 1;
    }
}
