package com.km.nn;

public interface Net {
    double process(double[] signal);
    void teach(double[] signal, double[] expected);
    int getSize();
    // TODO implement multiple neurons in the last layer
    double expected(double[] n);
}
