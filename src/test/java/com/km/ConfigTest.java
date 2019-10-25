package com.km;

import com.km.nn.NetVersion;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertSame;

public class ConfigTest {

    @BeforeClass
    public static void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(ConfigTest.class.getClassLoader().getResourceAsStream(Config.getFileName()));
        Config.setProperties(properties);
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
        assertSame(Config.getLevel(), LogLevel.ERROR);
    }

    @Test
    public void getTestLen() {
        Assert.assertEquals(Config.getTestLen(), 111);
    }

    @Test
    public void getCycleCount() {
        Assert.assertEquals(Config.getCycleCount(), 222);
    }

    @Test
    public void getSimCount() {
        Assert.assertEquals(Config.getSimCount(), 11);
    }

    @Test
    public void getSimDiff() {
        Assert.assertEquals(Config.getSimDiff(), 1);
    }

    @Test
    public void getNet2LearningFactor() {
        Assert.assertEquals(Config.getNet2LearningFactor(), 0.02, 0.001);
    }

    @Test
    public void getNet3LearningFactor() {
        Assert.assertEquals(Config.getNet3LearningFactor(), 0.03, 0.001);
    }

    @Test
    public void getNet4LearningFactor() {
        Assert.assertEquals(Config.getNet4LearningFactor(), 0.04, 0.001);
    }

    @Test
    public void getBatchNetVersion() {
        assertSame(Config.getBatchNetVersion(), NetVersion.NET3);
    }

    @Test
    public void getBatchNetFile() {
        Assert.assertEquals(Config.getBatchNetFile(), "BF");
    }

    @Test
    public void getSimCountL1() {
        Assert.assertEquals(Config.getSimCountL1(), 21);
    }

    @Test
    public void getSimCountL2() {
        Assert.assertEquals(Config.getSimCountL2(), 22);
    }

    @Test
    public void getSimCountL3() {
        Assert.assertEquals(Config.getSimCountL3(), 23);
    }

    @Test
    public void getSimCountL4() {
        Assert.assertEquals(Config.getSimCountL4(), 24);
    }

    @Test
    public void getSimL2() {
        Assert.assertEquals(Config.getSimL2(), 12);
    }

    @Test
    public void getSimL3() {
        Assert.assertEquals(Config.getSimL3(), 13);
    }

    @Test
    public void getSimL4() {
        Assert.assertEquals(Config.getSimL4(), 14);
    }

    @Test
    public void getWidth() {
        Assert.assertEquals(Config.getWidth(), 10);
    }

    @Test
    public void getHeight() {
        Assert.assertEquals(Config.getHeight(), 20);
    }
}