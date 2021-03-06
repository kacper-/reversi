package com.km.nn;

import com.km.Config;
import com.km.ConfigTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class NetTest {
    private static final int TRAIN_CYCLE = 2000;
    private static final int TRAIN_ACCURACY = 10;
    private static final double[] tA = new double[]{
            -1, -1, -1, 1, 1, -1, -1, -1,
            0, 0, 1, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 1, 0, 0,
            0, 1, 0, 0, 0, 0, 1, 0,
            0, 1, 1, 1, 1, 1, 1, 0,
            0, 1, 0, 0, 0, 0, 1, 0,
            1, 0, 0, 0, 0, 0, 0, 1,
            1, -1, -1, -1, -1, -1, -1, 1
    };
    private static final double[] tB = new double[]{
            1, 1, 1, 1, 1, 0, 0, 0,
            1, -1, -1, -1, -1, 1, 0, 0,
            1, -1, -1, -1, -1, 1, 0, 0,
            1, 1, 1, 1, 1, 1, 0, 0,
            1, -1, -1, -1, -1, -1, 1, 0,
            1, -1, -1, -1, -1, -1, 1, 0,
            1, -1, -1, -1, -1, -1, 1, 0,
            1, 1, 1, 1, 1, 1, 0, 0
    };
    private static final double[] tC = new double[]{
            -1, 1, 1, 1, 1, 1, -1, -1,
            1, 0, 0, 0, 0, 0, 1, -1,
            1, 0, 0, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 1, -1,
            -1, 1, 1, 1, 1, 1, -1, -1
    };
    private static final double[] tD = new double[]{
            1, 1, 1, 1, 1, -1, -1, -1,
            1, 0, 0, 0, 0, 1, -1, -1,
            1, 0, 0, 0, 0, 0, 1, -1,
            1, 0, 0, 0, 0, 0, 1, -1,
            1, 0, 0, 0, 0, 0, 1, -1,
            1, 0, 0, 0, 0, 0, 1, -1,
            1, 0, 0, 0, 0, 1, -1, -1,
            1, 1, 1, 1, 1, -1, -1, -1
    };
    private static final double[] tE = new double[]{
            1, 1, 1, 1, 1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, 1, 1, 1, 1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, -1, -1, -1, -1, 0, 0, 0,
            1, 1, 1, 1, 1, 0, 0, 0
    };
    private static final double[] tF = new double[]{
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

    @BeforeClass
    public static void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(NetTest.class.getClassLoader().getResourceAsStream(Config.FILE_NAME));
        Config.setProperties(properties);
    }

    @Test
    public void teach() {
        Net net = new Net2();
        int count = TRAIN_CYCLE * test.length;
        int t;
        for (int i = 0; i < count; i++) {
            t = ThreadLocalRandom.current().nextInt(test.length);
            net.teach(test[t], result(t));
        }
        for (t = 0; t < test.length; t++) {
            double expected = result(t);
            double actual = net.process(test[t]);
            System.out.println(t + ">" + expected + ">" + actual);
            Assert.assertEquals(expected, actual, Math.abs(expected / TRAIN_ACCURACY));
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