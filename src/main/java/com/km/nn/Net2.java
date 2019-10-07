package com.km.nn;

import java.io.Serializable;
import java.util.Arrays;

class Net2 implements Serializable, Net {
    private static final int SIZE = 64;
    private Layer front;
    private Layer back;
    private Layer middle;

    Net2(boolean useDropout) {
        front = new Layer(SIZE, SIZE, useDropout);
        middle = new Layer(SIZE, SIZE, useDropout);
        back = new Layer(1, SIZE, useDropout);
    }

    public double process(double[] signal) {
        front.process(signal);
        middle.process(front.getOutputs());
        back.process(middle.getOutputs());
        return back.getOutputs()[0];
    }

    public void teach(double[] signal, double expected) {
        double result = process(signal);
        double[] backError = new double[]{result - expected};
        double[] middleError = calculateError(back.getWeights(), backError);
        double[] frontError = calculateError(middle.getWeights(), middleError);
        back.calculateWeightDeltas(backError);
        middle.calculateWeightDeltas(middleError);
        front.calculateWeightDeltas(frontError);
        apply();
    }

    private void apply() {
        front.applyWeightDeltas();
        middle.applyWeightDeltas();
        back.applyWeightDeltas();
    }

    private double[] calculateError(double[][] weights, double[] error) {
        double[] result = new double[weights[0].length];
        Arrays.fill(result, 0);
        for (int w = 0; w < result.length; w++) {
            for (int n = 0; n < weights.length; n++)
                result[w] += weights[n][w] * error[n];
        }
        return result;
    }
}
