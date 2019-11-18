package com.km.nn.ac.impl;

import com.km.nn.ac.AC;

public class Isru implements AC {
    private final static double ALPHA = 0.3;

    public double f(double x) {
        return x / Math.sqrt(1d + (ALPHA * x * x));
    }

    public double f1(double x) {
        return Math.pow(1d / Math.sqrt(1d + (ALPHA * x * x)), 3);
    }
}
