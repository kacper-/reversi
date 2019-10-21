package com.km.nn;

import com.km.Config;

import java.io.Serializable;
import java.util.Arrays;

class Net3 implements Serializable, Net {
    private final static int SIZE = 64;
    private Layer front;
    private Layer back;
    private Layer middle;
    private Layer middle2;

    Net3() {
        front = new Layer(SIZE, SIZE, Config.getNet3LearningFactor());
        middle = new Layer(SIZE, SIZE, Config.getNet3LearningFactor());
        middle2 = new Layer(SIZE, SIZE, Config.getNet3LearningFactor());
        back = new Layer(1, SIZE, Config.getNet3LearningFactor());
    }

    @Override
    public double[] process(double[] signal) {
        front.process(signal);
        middle.process(front.getOutputs());
        middle2.process(middle.getOutputs());
        back.process(middle2.getOutputs());
        return back.getOutputs();
    }

    @Override
    public void teach(double[] signal, double[] expected) {
        double[] result = process(signal);
        double[] backError = new double[]{result[0] - expected[0]};
        double[] middle2Error = calculateError(back.getWeights(), backError);
        double[] middleError = calculateError(middle2.getWeights(), middle2Error);
        double[] frontError = calculateError(middle.getWeights(), middleError);
        back.calculateWeightDeltas(backError);
        middle2.calculateWeightDeltas(middle2Error);
        middle.calculateWeightDeltas(middleError);
        front.calculateWeightDeltas(frontError);
        apply();
    }

    @Override
    public int getSize() {
        return SIZE;
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

    @Override
    public double[] expected(double[] n) {
        double wins = n[0];
        double loses = n[1];
        if (loses > wins) {
            return new double[]{-(1d - (wins / loses))};
        } else {
            return new double[]{1d - (loses / wins)};
        }
    }
}
