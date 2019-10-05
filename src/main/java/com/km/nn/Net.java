package com.km.nn;

import java.io.Serializable;
import java.util.Arrays;

class Net implements Serializable {
    static final int SIZE = 64;
    private Layer front;
    private Layer back;
    private Layer middle;
    private Layer middle2;

    Net(boolean useDecay) {
        front = new Layer(SIZE, SIZE, useDecay);
        middle = new Layer(SIZE, SIZE, useDecay);
        middle2 = new Layer(SIZE, SIZE, useDecay);
        back = new Layer(1, SIZE, useDecay);
    }

    double process(double[] signal) {
        front.process(signal);
        middle.process(front.getOutputs());
        middle2.process(middle.getOutputs());
        back.process(middle2.getOutputs());
        return back.getOutputs()[0];
    }

    void teach(double[] signal, double expected, int i, int count) {
        double decay = Math.sqrt(1d - ((double) i / (double) count));
        double result = process(signal);
        double[] backError = new double[]{result - expected};
        double[] middle2Error = calculateError(back.getWeights(), backError);
        double[] middleError = calculateError(middle2.getWeights(), middle2Error);
        double[] frontError = calculateError(middle.getWeights(), middleError);
        back.calculateWeightDeltas(backError, decay);
        middle2.calculateWeightDeltas(middle2Error, decay);
        middle.calculateWeightDeltas(middleError, decay);
        front.calculateWeightDeltas(frontError, decay);
        apply();
    }

    private void apply() {
        front.applyWeightDeltas();
        middle.applyWeightDeltas();
        middle2.applyWeightDeltas();
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
