package com.divx.lingshi.remoting.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RpcMessage {
    /**
     * rpc message type
     */
    private byte messageType;

    private byte codec;

    private byte compress;

    private Object data;
}
