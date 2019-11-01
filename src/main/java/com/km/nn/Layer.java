package com.km.nn;

import com.km.nn.ac.AC;
import com.km.nn.ac.ACFactory;
import com.km.nn.ac.ACType;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Layer implements Serializable {
    private final static double WEIGHT_INIT_LIMIT = 0.05d;
    private double[][] weights;
    private double[][] weightDeltas;
    private double[] outputs;
    private double[] inputs;
    private int neuronCount;
    private int weightCount;
    private double learningFactor;
    private AC ac;

    Layer(int neuronCount, int weightCount, double learningFactor) {
        ac = ACFactory.createInstance(ACType.SOFT_SIGN);
        this.learningFactor = learningFactor;
        this.neuronCount = neuronCount;
        this.weightCount = weightCount;
        initWeights();
        initInputs();
        initOutputs();
    }

    double[][] getWeights() {
        return weights;
    }

    private void initInputs() {
        inputs = new double[weightCount];
    }

    double[] getOutputs() {
        return outputs;
    }

    private void initOutputs() {
        outputs = new double[neuronCount];
    }

    private void initWeights() {
        weights = new double[neuronCount][weightCount];
        weightDeltas = new double[neuronCount][weightCount];
        for (int n = 0; n < neuronCount; n++) {
            for (int w = 0; w < weightCount; w++) {
                weights[n][w] = (new Random().nextDouble() * 2 * WEIGHT_INIT_LIMIT) - WEIGHT_INIT_LIMIT;
                weightDeltas[n][w] = 0d;
            }
        }
    }

    void process(double[] signal) {
        if (signal.length != weightCount)
            throw new IllegalArgumentException(String.format("Wrong signal size expected = %d actual = %d", weightCount, signal.length));
        saveInputs(signal);
        for (int n = 0; n < neuronCount; n++) {
            outputs[n] = 0;
            outputs[n] = ac.f(combinedSignal(n));
        }
    }

    private double combinedSignal(int n) {
        double o = 0d;
        for (int w = 0; w < weightCount; w++) {
            o += weights[n][w] * inputs[w];
        }
        return o;
    }

    private void saveInputs(double[] signal) {
        System.arraycopy(signal, 0, inputs, 0, weightCount);
    }

    void calculateWeightDeltas(double[] outputDiff) {
        double f1Val;
        for (int n = 0; n < neuronCount; n++) {
            f1Val = ac.f1(combinedSignal(n));
            for (int w = 0; w < weightCount; w++) {
                if (ThreadLocalRandom.current().nextBoolean())
                    weightDeltas[n][w] = learningFactor * outputDiff[n] * f1Val * inputs[w];
                else
                    weightDeltas[n][w] = 0d;
            }
        }
    }

    void applyWeightDeltas() {
        for (int n = 0; n < neuronCount; n++) {
            for (int w = 0; w < weightCount; w++) {
                weights[n][w] -= weightDeltas[n][w];
                weightDeltas[n][w] = 0d;
            }
        }
    }
}
