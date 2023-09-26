package com.divx.lingshi.serialize.kyro;

import com.divx.lingshi.remoting.dto.RpcRequest;
import com.divx.lingshi.remoting.dto.RpcResponse;
import com.divx.lingshi.serialize.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@Slf4j
public class KyroSerializer implements Serializer {
    private static ThreadLocal<Kryo> KyroThreadLocal= ThreadLocal.withInitial(() ->{
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });


    @Override
    public byte[] serialize(Object obj) {
        //将对象序列化为byte数组
        try(ByteArrayOutputStream ops = new ByteArrayOutputStream();
            Output output = new Output(ops)) {
            Kryo kryo = KyroThreadLocal.get();
            kryo.writeObject(output,obj);
            KyroThreadLocal.remove();
            return output.toBytes();
        }catch (IOException e){
            log.error("serializable occur some error",e.getMessage());
            throw new RuntimeException("serializable occur some error");
        }
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> clz) {
        try(ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            Input ois = new com.esotericsoftware.kryo.io.Input(bis)){
            Kryo kryo = KyroThreadLocal.get();
            Object o = kryo.readObject(ois,clz);
            KyroThreadLocal.remove();
            return clz.cast(o);
        }catch (IOException e){
            log.error("deserializable occur some error",e.getMessage());
            throw new RuntimeException("deserializable occur some error");
        }
    }
}
