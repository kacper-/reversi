package com.km.nn.dropout.impl;

import com.km.nn.dropout.DropOut;

public class NoDropOut implements DropOut {
    @Override
    public double apply(double x) {
        return x;
    }
}
