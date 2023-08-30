package com.divx.lingshi.provider;

import com.divx.lingshi.config.RpcServiceConfig;

public interface ServiceProvider {

    /**
     * 服务发布
     */
    void publishService(RpcServiceConfig rpcServiceConfig);

    /**
     * 注册服务到zk注册中心
     * @param rpcServiceConfig
     */
    void addService(RpcServiceConfig rpcServiceConfig);

    /**
     * 获取服务
     * @param rpcServiceName
     * @return
     */
    Object getService(String rpcServiceName);
}
