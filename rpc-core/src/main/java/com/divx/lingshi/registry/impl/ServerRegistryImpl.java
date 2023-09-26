package com.divx.lingshi.registry.impl;

import com.divx.lingshi.registry.ServiceRegistry;
import com.divx.lingshi.utils.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.ApplicationContext;

import java.net.InetSocketAddress;

public class ServerRegistryImpl implements ServiceRegistry {

    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
    @Override
    public void registryService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.connect();
        CuratorUtils.registerPersistentNode(zkClient,servicePath);
    }
}
