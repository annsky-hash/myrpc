package com.divx.lingshi.remoting.transport.codec;

import com.divx.lingshi.remoting.constant.RpcConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.EventExecutorGroup;

public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder(){
        this(RpcConstants.MAX_FRAME_LENGTH,5,4,-9,0);
    }

    /**
     * @param maxFrameLength      Maximum frame length. It decide the maximum length of data that can be received.
     *                            If it exceeds, the data will be discarded.
     * @param var1                Length field offset. The length field is the one that skips the specified length of byte.
     * @param var2                The number of bytes in the length field.
     * @param var3                The compensation value to add to the value of the length field
     * @param var4                Number of bytes skipped.
     *                            If you need to receive all of the header+body data, this value is 0
     *                            if you only want to receive the body data, then you need to skip the number of bytes consumed by the header.
     */
    public RpcMessageDecoder(int maxFrameLength, int var1, int var2, int var3, int var4) {
        super(maxFrameLength, var1, var2, var3, var3);
    }

    public Object decode(ChannelHandlerContext ctx, ByteBuf in){
        return null;
    }
}
