package com.km.nn;

import com.km.Config;

import java.io.Serializable;
import java.util.Arrays;

public class NetB implements Net, Serializable {
    private final static int SIZE = 64;
    private final static int BSIZE = 128;
    private Layer front;
    private Layer back;
    private Layer middle;
    private Layer middle2;

    NetB() {
        front = new Layer(BSIZE, SIZE, Config.getNet3LearningFactor());
        middle = new Layer(BSIZE, BSIZE, Config.getNet3LearningFactor());
        middle2 = new Layer(BSIZE, BSIZE, Config.getNet3LearningFactor());
        back = new Layer(1, BSIZE, Config.getNet3LearningFactor());
    }

    @Override
    public double process(double[] signal) {
        front.process(signal);
        middle.process(front.getOutputs());
        middle2.process(middle.getOutputs());
        back.process(middle2.getOutputs());
        return back.getOutputs()[0];
    }

    @Override
    public void teach(double[] signal, double expected) {
        double result = process(signal);
        double[] backError = new double[]{result - expected};
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
}
