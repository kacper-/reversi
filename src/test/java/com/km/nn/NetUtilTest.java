package com.km.nn;

import com.km.Config;
import com.km.entities.Nodes;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class NetUtilTest {
    @BeforeClass
    public static void setUp() throws Exception {
        Properties properties = new Properties();
        properties.load(NetTest.class.getClassLoader().getResourceAsStream(Config.FILE_NAME));
        Config.setProperties(properties);
    }
}