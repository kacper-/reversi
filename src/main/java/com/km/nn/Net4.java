package com.km.nn;

import com.km.Config;

import java.io.Serializable;
import java.util.Arrays;

class Net4 implements Serializable, Net {
    private final static int SIZE = 64;
    private Layer front;
    private Layer back;
    private Layer middle;
    private Layer middle2;
    private Layer middle3;

    Net4() {
        front = new Layer(SIZE, SIZE, Config.getNet4LearningFactor());
        middle = new Layer(SIZE, SIZE, Config.getNet4LearningFactor());
        middle2 = new Layer(SIZE, SIZE, Config.getNet4LearningFactor());
        middle3 = new Layer(SIZE, SIZE, Config.getNet4LearningFactor());
        back = new Layer(1, SIZE, Config.getNet4LearningFactor());
    }

    @Override
    public double process(double[] signal) {
        front.process(signal);
        middle.process(front.getOutputs());
        middle2.process(middle.getOutputs());
        middle3.process(middle2.getOutputs());
        back.process(middle3.getOutputs());
        return back.getOutputs()[0];
    }

    @Override
    public void teach(double[] signal, double expected) {
        double result = process(signal);
        double[] backError = new double[]{result - expected};
        double[] middle3Error = calculateError(back.getWeights(), backError);
        double[] middle2Error = calculateError(middle3.getWeights(), middle3Error);
        double[] middleError = calculateError(middle2.getWeights(), middle2Error);
        double[] frontError = calculateError(middle.getWeights(), middleError);
        back.calculateWeightDeltas(backError);
        middle3.calculateWeightDeltas(middle3Error);
        middle2.calculateWeightDeltas(middle2Error);
        middle.calculateWeightDeltas(middleError);
        front.calculateWeightDeltas(frontError);
        apply();
    }

    private void apply() {
        front.applyWeightDeltas();
        middle.applyWeightDeltas();
        middle2.applyWeightDeltas();
        middle3.applyWeightDeltas();
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
    public int getSize() {
        return SIZE;
    }
}
