package com.divx.lingshi.compress.gzip;

import com.divx.lingshi.compress.Compress;
import com.divx.lingshi.remoting.constant.RpcConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipCompress implements Compress {
    @Override
    public byte[] zip(byte[] bytes) {
        if (bytes == null){
            throw new NullPointerException("bytes is null");
        }
        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out)){
            gzip.write(bytes);
            gzip.flush();
            gzip.finish();
            return out.toByteArray();

        }catch (IOException e){
            throw new RuntimeException("gzip compress errored",e);
        }

    }

    @Override
    public byte[] unzip(byte[] bytes) {
        if (bytes == null){
            throw new NullPointerException("bytes is null");
        }
        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes))){
            byte[] buffer = new byte[RpcConstants.BUFFER_SIZE];
            int n;
            while ((n = gis.read(bytes)) > -1){
                out.write(buffer,0,n);
            }
            return out.toByteArray();
        }catch (IOException e){
            throw new RuntimeException("gzip unCompress errored",e);
        }

    }
}
