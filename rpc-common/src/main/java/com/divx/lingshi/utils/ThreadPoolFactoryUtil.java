package com.divx.lingshi.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadPoolFactoryUtil {
    /**
     * 创建ThreadFactory， if threadNamePrefix is not null, create a custom ThreadFactory ,else create a defaultThreadFactory
     * @param threadNamePrefix 线程工厂类创建线程的线程前缀名
     * @param daemon 是否指定Thread为守护线程
     * @return
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null){
            if (daemon != null){
               return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
            }else{
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }
}
