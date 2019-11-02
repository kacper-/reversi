package com.km.nn.ac.impl;

import com.km.nn.ac.AC;

public class SoftSign implements AC {
    public double f(double x) {
        return x / (1 + Math.abs(x));
    }
    public double f1(double x) {
        return 1 / Math.pow(1 + Math.abs(x), 2);
    }
}
