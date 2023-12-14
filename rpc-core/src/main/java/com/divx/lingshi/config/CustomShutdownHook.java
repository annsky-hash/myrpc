package com.divx.lingshi.config;

import com.divx.lingshi.remoting.transport.netty.server.NettyRpcServer;
import com.divx.lingshi.utils.CuratorUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@Slf4j
public class CustomShutdownHook {
    public static void clearAll() {
        log.info("addShutdownHook for clear");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.port);
                CuratorUtils.clearRegistry(CuratorUtils.connect(),inetSocketAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        }));
    }
}
