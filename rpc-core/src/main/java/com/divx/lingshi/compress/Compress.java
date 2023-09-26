package com.divx.lingshi.compress;


import com.divx.lingshi.extension.SPI;

@SPI
public interface Compress {

    /**
     * 压缩
     * @param bytes
     * @return
     */
    byte[] zip(byte[] bytes);

    byte[] unzip(byte[] bytes);
}
