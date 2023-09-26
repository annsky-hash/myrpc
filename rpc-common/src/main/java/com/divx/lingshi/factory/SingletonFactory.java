package com.divx.lingshi.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonFactory {

    private static final Map<String,Object> cache = new ConcurrentHashMap<>();

    private SingletonFactory(){}
    public static <T> T getInstance(Class<T> clazz) {

        if (clazz == null) {
            throw new RuntimeException("clazz must not be null");
        }else{
            String key = clazz.toString();
            if (cache.containsKey(key)){
                return (T) cache.get(key);
            }
            T value = null;
            synchronized (value){
                if (value == null){
                    try {
                        value = clazz.newInstance();
                        cache.putIfAbsent(key,value);

                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            return value;
        }


    }
}
