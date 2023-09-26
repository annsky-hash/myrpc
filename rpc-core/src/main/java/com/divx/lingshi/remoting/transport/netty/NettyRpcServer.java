package com.divx.lingshi.remoting.transport.netty;

import com.divx.lingshi.config.CustomShutdownHook;
import com.divx.lingshi.config.RpcServiceConfig;
import com.divx.lingshi.factory.SingletonFactory;
import com.divx.lingshi.provider.ServiceProvider;
import com.divx.lingshi.provider.zk.ZkServiceProvider;
import com.divx.lingshi.remoting.handler.NettyRpcServerHandler;
import com.divx.lingshi.remoting.transport.codec.RpcMessageDecoder;
import com.divx.lingshi.remoting.transport.codec.RpcMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcServer {
    public static int port = 9998;
    public final ServiceProvider serviceProvider = SingletonFactory.getInstance(ZkServiceProvider.class);

    public NettyRpcServer(){

    }

    public void registerService(RpcServiceConfig rpcServiceConfig){
        serviceProvider.publishService(rpcServiceConfig);
    }

    @SneakyThrows
    public void start(){
        //1、启动时先卸载所有注册上的服务
        CustomShutdownHook.clearAll();
        String host = InetAddress.getLocalHost().getHostAddress();
        //用来处理建立连接的请求的线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //用来处理建链后的读写请求的线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    //默认建立NioServerSocketChannel，所以用NioServerSocketChannel.class作为参数
                    .channel(NioServerSocketChannel.class)
                    //childOption()是对NioServerSocketChannel建立之后设置属性
                    //TCP开启Nagle算法，该算法作用尽可能发送大数据块，减少网络IO
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    //开启底层心跳检查
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    //在建立channel时对channel设置属性
                    //表示系统用于存放完成了三次握手的TCP连接的队列的最大长度，如果连接建立频繁，可适当调大数值
                    .option(ChannelOption.SO_BACKLOG,128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline p = channel.pipeline();
                            p.addLast(new IdleStateHandler(30,0,0, TimeUnit.SECONDS));
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(new NettyRpcServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(host, port).sync();
            f.channel().closeFuture().sync();
        }catch (InterruptedException e){
            log.error("occur exception when start server:", e);
        }finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            //serviceHandlerGroup.shutdownGracefully();
        }
    }
}
