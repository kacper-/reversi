package com.km.nn;

import com.km.Config;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

public class Net3MTest {

    @BeforeClass
    public static void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(NetTest.class.getClassLoader().getResourceAsStream(Config.FILE_NAME));
        Config.setProperties(properties);
    }

    @Test
    public void index() {
        NetM net3M = new NetM(NetVersion.NET3);
        double[] signal = new double[net3M.getSize()];
        for (int i = 0; i < net3M.getSize(); i++)
            signal[i] = 0d;
        Assert.assertEquals(0, net3M.index(signal));
        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 8; i++)
                signal[i + (8 * j)] = 1d;
            Assert.assertEquals(j + 1, net3M.index(signal));
        }
    }
}