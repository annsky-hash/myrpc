package com.divx.lingshi.remoting.transport.netty.client;

import com.divx.lingshi.extension.ExtensionLoader;
import com.divx.lingshi.factory.ChannelFactory;
import com.divx.lingshi.factory.SingletonFactory;
import com.divx.lingshi.registry.ServiceDiscovery;
import com.divx.lingshi.remoting.dto.RpcRequest;
import com.divx.lingshi.remoting.dto.RpcResponse;
import com.divx.lingshi.remoting.handler.NettyRpcClientHandler;
import com.divx.lingshi.remoting.transport.RpcRequestTransport;
import com.divx.lingshi.remoting.transport.codec.RpcMessageDecoder;
import com.divx.lingshi.remoting.transport.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery;
    private final ChannelFactory channelFactory;
    private final EventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;

    public NettyRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,6000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(0,5,0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new NettyRpcClientHandler());
                    }
                });
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        this.channelFactory = SingletonFactory.getInstance(ChannelFactory.class);
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        //build return value
        CompletableFuture<RpcResponse<?>> resultFuture = new CompletableFuture<>();
        //get server address
        InetSocketAddress address = serviceDiscovery.lookupService(rpcRequest);
        //get server address related
        Channel channel = getChannel(address);
        return null;
    }

    private Channel getChannel(InetSocketAddress address) {
         Channel channel = channelFactory.getChannel(address);
         if (channel == null){
             channel = doConnect(address);
             channelFactory.setChannel(address,channel);
         }
         return channel;
    }

    @SneakyThrows
    private Channel doConnect(InetSocketAddress address) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(address).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()){
                log.info("The client has connected {} successful",address.toString());
                completableFuture.complete(future.channel());
            }else{
                throw new IllegalArgumentException();
            }
        });
        return completableFuture.get();
    }
}
