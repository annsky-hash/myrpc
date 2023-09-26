package com.divx.lingshi.provider;

import com.divx.lingshi.config.RpcServiceConfig;

public interface ServiceProvider {

    /**
     * 服务发布
     */
    void publishService(RpcServiceConfig rpcServiceConfig);

    /**
     * 服务加入到缓存
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
