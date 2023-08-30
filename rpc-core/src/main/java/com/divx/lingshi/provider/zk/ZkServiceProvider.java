package com.divx.lingshi.provider.zk;

import com.divx.lingshi.config.RpcServiceConfig;
import com.divx.lingshi.enums.RpcErrorMessageEnum;
import com.divx.lingshi.exception.RpcException;
import com.divx.lingshi.extension.ExtensionLoader;
import com.divx.lingshi.provider.ServiceProvider;
import com.divx.lingshi.registry.ServiceRegistry;
import com.divx.lingshi.registry.impl.ServerRegistryImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class ZkServiceProvider implements ServiceProvider {

    private final Map<String,Object> serviceMap;
    private final Set<String> registeredService;
    private final ServiceRegistry serviceRegistry;

    public ZkServiceProvider() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        //serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");
        serviceRegistry = new ServerRegistryImpl();
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {

    }

    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if(registeredService.contains(rpcServiceName)){
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName,rpcServiceConfig.getService());
        log.info("Add service: {} and interfaces:{}", rpcServiceName, rpcServiceConfig.getService().getClass().getInterfaces());
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (service == null){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }
}
