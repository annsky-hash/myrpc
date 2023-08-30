package com.divx.lingshi.config;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceConfig {

    private String version = "";
    private String group = "";
    private Object service;

    public String getRpcServiceName(){
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    private String getServiceName() {
        return this.getService().getClass().getInterfaces()[0].getCanonicalName();
    }
}
