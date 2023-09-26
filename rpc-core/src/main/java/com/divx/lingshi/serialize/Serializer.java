package com.divx.lingshi.serialize;


import com.divx.lingshi.extension.SPI;

@SPI
public interface Serializer {

    /**
     * 序列化
     * @param obj
     * @return
     */
     byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes
     * @param clz
     * @param <T>
     * @return
     */
     <T> T deSerialize(byte[] bytes,Class<T> clz);

}
