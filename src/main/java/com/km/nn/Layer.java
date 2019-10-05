package com.km.nn;

import java.io.Serializable;
import java.util.Random;

class Layer implements Serializable {
    private final static double WEIGHT_INIT_LIMIT = 0.05d;
    private final static double LEARNING_FACTOR = 0.005d;
    private double[][] weights;
    private double[][] weightDeltas;
    private double[] outputs;
    private double[] inputs;
    private int neuronCount;
    private int weightCount;
    private boolean useDecay;

    Layer(int neuronCount, int weightCount, boolean useDecay) {
        this.useDecay = useDecay;
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
            outputs[n] = f(combinedSignal(n));
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

    private double f(double x) {
        return x / (1 + Math.abs(x));
    }

    private double f1(double x) {
        return 1 / Math.pow(1 + Math.abs(x), 2);
    }

    void calculateWeightDeltas(double[] outputDiff, double decay) {
        double f1Val;
        for (int n = 0; n < neuronCount; n++) {
            f1Val = f1(combinedSignal(n));
            for (int w = 0; w < weightCount; w++) {
                if (useDecay)
                    weightDeltas[n][w] = decay * LEARNING_FACTOR * outputDiff[n] * f1Val * inputs[w];
                else
                    weightDeltas[n][w] = LEARNING_FACTOR * outputDiff[n] * f1Val * inputs[w];
            }
        }
    }

    void applyWeightDeltas() {
        for (int n = 0; n < neuronCount; n++) {
            for (int w = 0; w < weightCount; w++) {
                switch (new Random().nextInt(3)) {
                    case 0:
                        weights[n][w] -= weightDeltas[n][w];
                        break;
                    case 1:
                        weights[n][w] -= weightDeltas[n][w] / 2;
                        break;
                }
                weightDeltas[n][w] = 0d;
            }
        }
    }
}
