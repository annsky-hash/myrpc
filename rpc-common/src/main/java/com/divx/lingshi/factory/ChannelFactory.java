package com.divx.lingshi.factory;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelFactory {

    private final Map<String, Channel> channelMap;

    public ChannelFactory() {
        this.channelMap = new ConcurrentHashMap<>();
    }

    public  Channel getChannel(InetSocketAddress inetSocketAddress){
     String key = inetSocketAddress.toString();
     if(channelMap.containsKey(key)){
         Channel channel = channelMap.get(key);
         if (channel != null && channel.isActive()){
             return channel;
         }else {
             channelMap.remove(key);
         }
     }
     return null;
    }

    public void setChannel(InetSocketAddress inetSocketAddress,Channel channel) {
        channelMap.put(inetSocketAddress.toString(),channel);
    }
}
