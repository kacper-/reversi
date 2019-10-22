package com.km.nn;

public interface Net {
    double process(double[] signal);

    void teach(double[] signal, double expected);

    int getSize();

    default double expected(double[] n) {
        double wins = n[0];
        double loses = n[1];
        if (loses > wins) {
            return -(1d - (wins / loses));
        } else {
            return 1d - (loses / wins);
        }
    }
}
