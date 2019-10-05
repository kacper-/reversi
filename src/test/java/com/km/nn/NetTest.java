package com.km.nn;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class NetTest {
    private double[] tA = new double[]{
            -1, -1, -1, 1, 1, -1, -1, -1,
            0, 0, 1, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 1, 0, 0,
            0, 1, 0, 0, 0, 0, 1, 0,
            0, 1, 1, 1, 1, 1, 1, 0,
            0, 1, 0, 0, 0, 0, 1, 0,
            1, 0, 0, 0, 0, 0, 0, 1,
            1, -1, -1, -1, -1, -1, -1, 1
    };
    private double[] tB = new double[]{
            1, 1, 1, 1, 1, 0, 0, 0,
            1, -1, -1, -1, -1, 1, 0, 0,
            1, -1, -1, -1, -1, 1, 0, 0,
            1, 1, 1, 1, 1, 1, 0, 0,
            1, -1, -1, -1, -1, -1, 1, 0,
            1, -1, -1, -1, -1, -1, 1, 0,
            1, -1, -1, -1, -1, -1, 1, 0,
            1, 1, 1, 1, 1, 1, 0, 0
    };
    private double[] tC = new double[]{
            -1, 1, 1, 1, 1, 1, -1, -1,
            1, 0, 0, 0, 0, 0, 1, -1,
            1, 0, 0, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 1, -1,
            -1, 1, 1, 1, 1, 1, -1, -1
    };
    private double[] tD = new double[]{
            1, 1, 1, 1, 1, -1, -1, -1,
            1, 0, 0, 0, 0, 1, -1, -1,
            1, 0, 0, 0, 0, 0, 1, -1,
            1, 0, 0, 0, 0, 0, 1, -1,
            1, 0, 0, 0, 0, 0, 1, -1,
            1, 0, 0, 0, 0, 0, 1, -1,
            1, 0, 0, 0, 0, 1, -1, -1,
            1, 1, 1, 1, 1, -1, -1, -1
    };
    private double[] tE = new double[]{
            1, 1, 1, 1, 1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, 1, 1, 1, 1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, 1, 1, 1, 1, 0, 0, 0
    };
    private double[] tF = new double[]{
            1, 1, 1, 1, 1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, 1, 1, 1, 1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 0, 0
    };

    private double[][] test = new double[][]{tA, tB, tC, tD, tE, tF};

    @Test
    public void teach() {
        Net net = new Net(NetUtil.USE_DECAY, NetUtil.USE_DROPOUT);
        int count = 10000 * test.length;
        int t;
        for (int i = 0; i < count; i++) {
            t = new Random().nextInt(test.length);
            net.teach(test[t], result(t), i, count);
        }
        for (t = 0; t < test.length; t++) {
            double expected = result(t);
            double actual = net.process(test[t]);
            System.out.println(t + ">" + expected + ">" + actual);
            Assert.assertEquals(expected, actual, Math.abs(expected / 10));
        }
    }

    private double result(int i) {
        switch (i) {
            case 0:
                return -0.6;
            case 1:
                return -0.3;
            case 2:
                return -0.1;
            case 3:
                return 0.1;
            case 4:
                return 0.3;
            case 5:
                return 0.6;
        }
        return 0;
    }
}