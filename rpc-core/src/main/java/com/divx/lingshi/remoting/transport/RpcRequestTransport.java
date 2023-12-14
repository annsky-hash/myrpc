package com.divx.lingshi.remoting.transport;

import com.divx.lingshi.remoting.dto.RpcRequest;

public interface RpcRequestTransport {

    /**
     * send request to netty server and get result
     * @param rpcRequest
     * @return
     */
    Object sendRpcRequest(RpcRequest rpcRequest);

}
