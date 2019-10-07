package com.km.nn;

public interface Net {
    double process(double[] signal);
    void teach(double[] signal, double expected, double decay);
}
