package com.divx.lingshi.impl;

import com.divx.lingshi.annotation.RpcService;
import com.divx.lingshi.api.Hello;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RpcService(group = "test1",version = "version1")
public class HelloServiceImpl implements Hello {
}
