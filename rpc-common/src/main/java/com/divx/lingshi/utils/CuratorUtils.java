package com.divx.lingshi.utils;

import com.divx.lingshi.enums.RpcConfigEnum;
import org.apache.curator.framework.CuratorFramework;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class CuratorUtils {

    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
    private final Map<String,CuratorFramework> ZKClientMap = new ConcurrentHashMap<>();
    public static CuratorFramework connect() {
        Properties properties = PropertiesUtils.readProperties(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        String ZKAddress = properties!= null && properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) != null ? properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) : DEFAULT_ZOOKEEPER_ADDRESS;
        CuratorUtils curatorUtils = new CuratorUtils();
        //现在缓存中找
        if(curatorUtils.ZKClientMap.containsKey(ZKAddress)){
            return curatorUtils.ZKClientMap.get(ZKAddress);
        }
    }
}
