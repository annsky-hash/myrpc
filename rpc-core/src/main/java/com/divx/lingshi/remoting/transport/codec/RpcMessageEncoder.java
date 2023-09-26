package com.divx.lingshi.remoting.transport.codec;

import com.divx.lingshi.compress.Compress;
import com.divx.lingshi.enums.CompressTypeEnum;
import com.divx.lingshi.enums.SerializationTypeEnum;
import com.divx.lingshi.extension.ExtensionLoader;
import com.divx.lingshi.remoting.constant.RpcConstants;
import com.divx.lingshi.remoting.dto.RpcMessage;
import com.divx.lingshi.serialize.Serializer;
import com.divx.lingshi.serialize.kyro.KyroSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {


    public static final AtomicInteger REQUEST_ID = new AtomicInteger(0);

    /**
     * 加密
     * @param channelHandlerContext 传输业务数据
     * @param message message
     * @param out message编码后转换成字节，ByteBuf用来存储字节的容器
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage message, ByteBuf out) throws Exception {

        try{
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writeByte(RpcConstants.VERSION);
            //给消息长度预留空间
            out.writerIndex(out.writerIndex() + 4);
            byte messageType = message.getMessageType();
            out.writeByte(messageType);
            out.writeByte(message.getCodec());
            out.writeByte(CompressTypeEnum.GZIP.getCode());
            out.writeInt(REQUEST_ID.getAndIncrement());
            //构建full length(消息长度)
            byte[] bodyByte = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            //如果消息不是心跳检测 消息，消息做特异化处理，消息长度 = head length + body length
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE){
                //serialize the object
                String codecName = SerializationTypeEnum.getName(message.getCodec());
                log.info("codec name is {}",codecName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(KyroSerializer.class).getExtension(codecName);
                bodyByte = serializer.serialize(message.getData());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(CompressTypeEnum.getName(message.getCompress()));
                byte[] zip = compress.zip(bodyByte);
                fullLength += bodyByte.length;
            }
            if (bodyByte != null){
                out.writeBytes(bodyByte);
            }
            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        }catch (Exception e){
            log.error("Encode request error!", e);
        }
    }
}
