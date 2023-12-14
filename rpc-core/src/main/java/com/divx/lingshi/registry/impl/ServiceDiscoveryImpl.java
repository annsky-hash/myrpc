package com.divx.lingshi.registry.impl;

import com.divx.lingshi.enums.LoadBalanceEnum;
import com.divx.lingshi.extension.ExtensionLoader;
import com.divx.lingshi.loadbalance.LoadBalance;
import com.divx.lingshi.registry.ServiceDiscovery;
import com.divx.lingshi.remoting.dto.RpcRequest;
import com.divx.lingshi.utils.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ServiceDiscoveryImpl implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ServiceDiscoveryImpl() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(LoadBalanceEnum.LOADBALANCE.getName());
    }
    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        //String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.connect();
        List<String> childrenNodes = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        return null;
    }
}
