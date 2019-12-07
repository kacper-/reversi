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

    @Test
    public void updateRepo() {
        NetUtil netUtil = new NetUtil(NetVersion.NET4M, "", "");
        String key = "ECO";
        Nodes node1 = new Nodes(key, 20, 30);
        List<Nodes> nodes1 = new ArrayList<>();
        nodes1.add(node1);
        netUtil.clearRepo();
        netUtil.updateRepo(nodes1);
        Nodes node2 = new Nodes(key, 50, 130);
        List<Nodes> nodes2 = new ArrayList<>();
        nodes2.add(node2);
        netUtil.updateRepo(nodes2);
        Map<String, Nodes> nodesMap = netUtil.getNodesMap();
        Nodes node3 = nodesMap.get(key);
        Assert.assertNotNull(node3);
        Assert.assertEquals(70, node3.getWins());
        Assert.assertEquals(160, node3.getLoses());
    }
}