package com.study.zk.distributeConfig;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoServiceApplicationTests {

    // 项目使用zookeeper的java客户端
    @Value("${config.zookeeper.url}")
    String zkUrl; // 配置文件读取localhost:2181
    @Value("${config.zookeeper.nodename}")
    String nodename; // 配置文件读取pay-service-config

    @Test
    public void zkTest() throws Exception {
        CuratorFramework zkClient = CuratorFrameworkFactory.newClient(zkUrl, new RetryOneTime(1000));
        zkClient.start(); // 启动和zookeeper的连接
        // 1. 获取节点对应的值
        byte[] bytes = zkClient.getData().forPath("/" + nodename);
        System.out.println("/pay-service-config对应的值是：" + new String(bytes));

        // 2. 获取节点下的子节点/pay-service-config根目录， 下面的每一个子节点都代表一项配置
        List<String> strings = zkClient.getChildren().forPath("/" + nodename);
        System.out.println("pay-service-config节点下有这些子节点");
        strings.forEach(s -> {
            System.out.println(s);
        });
    }

}
