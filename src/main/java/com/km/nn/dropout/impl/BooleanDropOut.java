package com.km.nn.dropout.impl;

import com.km.nn.dropout.DropOut;

import java.util.concurrent.ThreadLocalRandom;

public class BooleanDropOut implements DropOut {
    @Override
    public double apply(double x) {
        if (ThreadLocalRandom.current().nextBoolean())
            return x;
        return 0;
    }
}
