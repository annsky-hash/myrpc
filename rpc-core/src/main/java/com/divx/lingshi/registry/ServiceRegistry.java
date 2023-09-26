package com.divx.lingshi.registry;

import com.divx.lingshi.config.RpcServiceConfig;
import com.divx.lingshi.extension.SPI;

import java.net.InetSocketAddress;


@SPI
public interface ServiceRegistry {

    /**
     * 注册服务到注册中心
     * @param rpcServiceName 服务名
     * @param inetSocketAddress 注册中心地址
     */
    void registryService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
