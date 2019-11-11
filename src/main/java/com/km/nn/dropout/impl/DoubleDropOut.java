package com.km.nn.dropout.impl;

import com.km.nn.dropout.DropOut;

import java.util.concurrent.ThreadLocalRandom;

public class DoubleDropOut implements DropOut {
    @Override
    public double apply(double x) {
        return ThreadLocalRandom.current().nextDouble() * x;
    }
}
