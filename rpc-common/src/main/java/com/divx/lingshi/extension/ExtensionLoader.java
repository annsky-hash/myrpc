package com.divx.lingshi.extension;


import com.divx.lingshi.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class ExtensionLoader<T> {

    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";
    //扩展类加载器缓存
    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>,Object> extensionInstance = new ConcurrentHashMap<>(64);
    private final Class<?> type;
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    public ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    /**
     * 泛型方法 <S> 返回类型 方法名 （泛型参数列表）
     * @param type
     * @param <S>
     * @return
     */
    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type){
        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null");
        }
        if(!type.isInterface()){
            throw new IllegalArgumentException("Extension type must be interface");
        }
        if(type.getAnnotation(SPI.class) == null){
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        // first get from cache,if not hint,then create one
        if (EXTENSION_LOADERS.containsKey(type)){
            return (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        EXTENSION_LOADERS.putIfAbsent(type,new ExtensionLoader<S>(type));
        return (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
    }

    public T getExtension(String name) {
        if (StringUtils.isBlank(name)){
            throw new IllegalArgumentException("Extension name must not be null or empty");
        }
        Holder<Object> holder = cachedInstances.get(name);
        if(holder == null) {
            cachedInstances.putIfAbsent(name,new Holder<>());
            holder = cachedInstances.get(name);
        }
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder){
                instance = holder.get();
                if (instance == null){
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        
        
        return (T) instance;
    }

    private T createExtension(String name) {
        //get extension classes of T and get special one by name
        Map<String, Class<?>> classMap = cachedClasses.get();
        if(classMap == null) {
            synchronized (cachedClasses){
                if( cachedClasses.get() == null) {
                    classMap= new HashMap<>();
                    loadDirectory(classMap);
                    cachedClasses.set(classMap);
                }
            }
        }
        Class<?> clazz = classMap.get(name);
        if (clazz == null){
            throw new RuntimeException("No such extension of name "+ name);
        }
        T instance = (T)extensionInstance.get(clazz);
        if (instance == null){
            try {
                extensionInstance.putIfAbsent(clazz,clazz.newInstance());
                instance = (T)extensionInstance.get(clazz);
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
        return instance;
    }

    private void loadDirectory(Map<String, Class<?>> classMap) {
        String filename = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        try{
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(filename);
            if (urls != null) {
                while (urls.hasMoreElements()){
                    URL url = urls.nextElement();
                    loadResource(url,classLoader,classMap);
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    private void loadResource(URL url, ClassLoader classLoader, Map<String, Class<?>> classMap) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line;
            //read every line
            while((line = reader.readLine()) != null){
                //获取注释符#的位置
                final int ci = line.indexOf("#");
                if(ci >= 0) {
                    //#之后的代码都是注释代码
                    line = line.substring(0,ci);
                }
                line = line.trim();
                if(line.length() >0) {
                    try{
                        //获取=符号的位置
                        final int ei = line.indexOf("=");
                        String name = line.substring(0,ei).trim();
                        String clazzName = line.substring(ei+1).trim();
                        if (name.length()>0 && clazzName.length()>0){
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            classMap.putIfAbsent(name,clazz);
                        }
                    }catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }
}
