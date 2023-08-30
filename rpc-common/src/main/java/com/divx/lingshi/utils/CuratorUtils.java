package com.divx.lingshi.utils;

import com.divx.lingshi.enums.RpcConfigEnum;
import org.apache.curator.framework.CuratorFramework;

import java.util.Properties;

public class CuratorUtils {

    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
    public static CuratorFramework connect() {
        Properties properties = PropertiesUtils.readProperties(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        String ZKAddress = properties!= null && properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) != null ? properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) : DEFAULT_ZOOKEEPER_ADDRESS;
    }
}
