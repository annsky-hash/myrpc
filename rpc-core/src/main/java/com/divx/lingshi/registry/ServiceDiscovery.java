package com.divx.lingshi.registry;


import com.divx.lingshi.extension.SPI;
import com.divx.lingshi.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

@SPI
public interface ServiceDiscovery {


    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
