package com.km.nn.ac.impl;

import com.km.nn.ac.AC;

public class ArcTan implements AC {
    public double f(double x) {
        return Math.atan(x);
    }

    public double f1(double x) {
        return 1d / ((x * x) + 1d);
    }

}
