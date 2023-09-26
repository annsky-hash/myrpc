package com.divx.lingshi.utils;

import com.divx.lingshi.enums.RpcConfigEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * zookeeper连接工具
 */
@Slf4j
public class CuratorUtils {

    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
    private static final Map<String,CuratorFramework> zkClientMap = new ConcurrentHashMap<>();
    private static final Set<String> SERVICE_PATH = ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;


    private CuratorUtils(){

    }

    public static CuratorFramework connect() {
        Properties properties = PropertiesUtils.readProperties(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        String ZKAddress = properties!= null && properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) != null ? properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) : DEFAULT_ZOOKEEPER_ADDRESS;
        //现在缓存中找
        if(zkClientMap.containsKey(ZKAddress)){
            return zkClientMap.get(ZKAddress);
        }
        //retry strategy. retry 3 times ,and will increase the sleep time between retries
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(ZKAddress)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();

        //连接zk的最大超时时间是30s
        try {
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)){
                throw new RuntimeException("Time out waiting to connect to ZK!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }

    public static void registerPersistentNode(CuratorFramework zkClient, String servicePath) {
        //首先判断缓存中是否存在
        try {
            if (SERVICE_PATH.contains(servicePath) || zkClient.checkExists().forPath(servicePath) != null) {
                log.info("The node already exists. The node is:[{}]", servicePath);
            }else{
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath);
                log.info("The node was created successfully. The node is:[{}]", servicePath);
            }
            SERVICE_PATH.add(servicePath);
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail", servicePath);
        }
    }

    public static void clearRegistry(CuratorFramework connect, InetSocketAddress inetSocketAddress) {
        //首先判断zk中是否有服务
        if (SERVICE_PATH.isEmpty()) {
            return;
        }
        SERVICE_PATH.stream().parallel().forEach(p -> {
            try {
                if (p.endsWith(inetSocketAddress.toString())) {
                    zkClient.delete().forPath(p);
                }
            }catch (Exception e){
                log.error("clear registry for path [{}] fail", p);
            }
        });
        log.info("All registered services on the server are cleared:[{}]", SERVICE_PATH.toString());
    }
}
