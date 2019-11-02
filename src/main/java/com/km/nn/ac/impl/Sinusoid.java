package com.km.nn.ac.impl;

import com.km.nn.ac.AC;

public class Sinusoid implements AC {
    public double f(double x) {
        return Math.sin(x);
    }

    public double f1(double x) {
        return Math.cos(x);
    }
}
