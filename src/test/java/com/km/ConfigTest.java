package com.km;

import com.km.engine.EngineType;
import com.km.nn.NetVersion;
import com.km.nn.ac.ACType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertSame;

public class ConfigTest {

    @BeforeClass
    public static void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(ConfigTest.class.getClassLoader().getResourceAsStream(Config.FILE_NAME));
        Config.setProperties(properties);
    }

    @Test
    public void getNetActivationFunction() {
        Assert.assertEquals(ACType.SOFT_SIGN, Config.getNetActivationFunction());
    }

    @Test
    public void getEngineANN3MRCfile() {
        Assert.assertEquals("ANN3MRC", Config.getEngineANN3MRCfile());
    }

    @Test
    public void getEngineANN3RCfile() {
        Assert.assertEquals("ANN3RC", Config.getEngineANN3RCfile());
    }

    @Test
    public void getEngineANN4RCfile() {
        Assert.assertEquals("ANN4RC", Config.getEngineANN4RCfile());
    }

    @Test
    public void isHeadless() {
        Assert.assertTrue(Config.isHeadless());
    }

    @Test
    public void getLevel() {
        assertSame(Config.getLevel(), LogLevel.INFO);
    }

    @Test
    public void getTestLen() {
        Assert.assertEquals(Config.getTestLen(), 100);
    }

    @Test
    public void getCycleCount() {
        Assert.assertEquals(Config.getCycleCount(), 1000);
    }

    @Test
    public void getSimCount() {
        Assert.assertEquals(Config.getSimCount(), 11);
    }

    @Test
    public void getSimDiff() {
        Assert.assertEquals(Config.getSimDiff(), 0);
    }

    @Test
    public void getNet2LearningFactor() {
        Assert.assertEquals(Config.getNet2LearningFactor(), 0.01, 0.001);
    }

    @Test
    public void getNet3LearningFactor() {
        Assert.assertEquals(Config.getNet3LearningFactor(), 0.001, 0.0001);
    }

    @Test
    public void getNet4LearningFactor() {
        Assert.assertEquals(Config.getNet4LearningFactor(), 0.0007, 0.0001);
    }

    @Test
    public void getBatchNetVersion() {
        assertSame(Config.getBatchNetVersion(), NetVersion.NET3);
    }

    @Test
    public void getBatchTrainEngines() {
        EngineType[] engineTypes = Config.getBatchTrainEngines();
        Assert.assertEquals(2, engineTypes.length);
        Assert.assertEquals(EngineType.RANDOM, engineTypes[0]);
        Assert.assertEquals(EngineType.BATCH, engineTypes[1]);
    }

    @Test
    public void getBatchNetFile() {
        Assert.assertEquals(Config.getBatchNetFile(), "BATCH");
    }

    @Test
    public void isBatchClear() {
        Assert.assertFalse(Config.isBatchClear());
    }

    @Test
    public void getSimCountL0() {
        Assert.assertEquals(Config.getSimCountL0(), 200);
    }

    @Test
    public void getSimCountL1() {
        Assert.assertEquals(Config.getSimCountL1(), 300);
    }

    @Test
    public void getSimCountL2() {
        Assert.assertEquals(Config.getSimCountL2(), 400);
    }

    @Test
    public void getSimCountL3() {
        Assert.assertEquals(Config.getSimCountL3(), 600);
    }

    @Test
    public void getSimCountL4() {
        Assert.assertEquals(Config.getSimCountL4(), 600);
    }

    @Test
    public void getSimCountL5() {
        Assert.assertEquals(Config.getSimCountL5(), 400);
    }

    @Test
    public void getSimCountL6() {
        Assert.assertEquals(Config.getSimCountL6(), 15);
    }

    @Test
    public void getWidth() {
        Assert.assertEquals(Config.getWidth(), 950);
    }

    @Test
    public void getHeight() {
        Assert.assertEquals(Config.getHeight(), 1000);
    }
}