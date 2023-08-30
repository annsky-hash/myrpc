package com.divx.lingshi.utils;

import com.divx.lingshi.enums.RpcConfigEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


@Slf4j
public class PropertiesUtils {
    public static Properties readProperties(String fileName) {
        Properties properties = null;
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String path = "";
        if(url != null) {
            path = url.getPath() + fileName;
        }

        try(InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8)){
            properties = new Properties();
            properties.load(inputStreamReader);
        }catch (Exception e){
            log.error("occur exception when read properties file [{}]", fileName);
        }
        return properties;
    }


    public static Properties readPropertiesV2(String fileName) {
        Properties properties = new Properties();
        try(InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
