package com.study.zk.distributeConfig.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.RetryOneTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class SystemConfigUtil {

    // 数据来自哪里？
    Properties properties = new Properties();

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Value("${config.zookeeper.url}")
    String zkUrl; // 配置文件读取localhost:2181
    @Value("${config.zookeeper.nodename}")
    String nodename; // 配置文件读取pay-service-config

    // TODO 从数据库加载，定时读取
    // TODO 从zookeeper中读取
    @PostConstruct
    public void init() {
        // 更加具体的zookeeper实现
        CuratorFramework zkClient = CuratorFrameworkFactory.newClient(zkUrl, new RetryOneTime(1000));
        zkClient.start(); // 启动和zookeeper的连接
        try {
            // TODO 1. 读取zookeeper中，系统对应的配置。 节点：  /pay-service-config节点
            Map<String, Object> configMap = new HashMap<>();
            List<String> configNames = zkClient.getChildren().forPath("/pay-service-config");
            for (String configName : configNames) {
                // 读取具体的配置值--内容
                byte[] value = zkClient.getData().forPath("/pay-service-config/" + configName);
                configMap.put(configName, new String(value));
            }
            // TODO 2. 将zookeeper的配置放到properties
            properties.putAll(configMap);

            // TODO 3. 监听内容变化
            // 不同的zk java客户端，写法不同，没必要死记硬背，理解思路
            // Curator客户端监听的写法 // 监听节点，及子节点的CRUD
            TreeCache treeCache = new TreeCache(zkClient, "/pay-service-config");
            treeCache.start();
            treeCache.getListenable().addListener(new TreeCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                    // TODO 收到数据变化通知，修改对象： 示例代码
                    switch (event.getType()) {
                        case NODE_UPDATED:
                            // 数据修改事件
                            System.out.println("发生了数据变化：" + event.getData());
                            // TODO 根据节点的变化，去修改对应的配置信息 简单示例，修改url
                            // path='/pay-service-config/pay.alipay.url'
                            String key = event.getData().getPath().replace("/" + nodename + "/", "");
                            String value = new String(event.getData().getData());
                            properties.setProperty(key, value);
                            break;
                        default:
                            break;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
