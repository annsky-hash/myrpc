package com.divx.lingshi.registry;

import com.divx.lingshi.config.RpcServiceConfig;

import java.net.InetSocketAddress;

public interface ServiceRegistry {

    /**
     * 注册服务到注册中心
     * @param rpcServiceName 服务名
     * @param inetSocketAddress 注册中心地址
     */
    void registryService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
