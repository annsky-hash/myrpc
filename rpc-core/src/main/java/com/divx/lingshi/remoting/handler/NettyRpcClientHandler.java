package com.divx.lingshi.remoting.handler;

import com.divx.lingshi.factory.SingletonFactory;
import com.divx.lingshi.remoting.constant.RpcConstants;
import com.divx.lingshi.remoting.dto.RpcMessage;
import com.divx.lingshi.remoting.dto.RpcResponse;
import com.divx.lingshi.remoting.transport.netty.client.UnprocessRequests;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {

    private final UnprocessRequests unprocessRequests;

    public NettyRpcClientHandler() {
        this.unprocessRequests = SingletonFactory.getInstance(UnprocessRequests.class);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("client recieve msg: {}",msg);
        try {
            if(msg instanceof RpcMessage){
                //if msg is heart beat message
                RpcMessage tmp = (RpcMessage) msg;
                byte messageType = tmp.getMessageType();
                if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE){
                    log.info("heart [{}]",tmp.getData());
                }else if (messageType == RpcConstants.RESPONSE_TYPE){
                    RpcResponse<Object> rpcResponse = (RpcResponse<Object>) tmp.getData();
                    unprocessRequests.complete(rpcResponse);
                }
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }

    }
}
