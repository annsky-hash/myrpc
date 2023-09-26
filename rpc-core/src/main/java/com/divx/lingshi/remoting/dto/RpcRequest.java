package com.divx.lingshi.remoting.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    private Object[] params;
    private String methodName;
    private String interfaceName;
    private Class<?> paramType;
    private String group;
    private String version;


    public String getRpcServiceName() {return this.interfaceName+this.group+this.version;}
}
