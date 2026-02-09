/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 默认网关 Token 校验器（兜底实现）
 *
 * <p>始终注册；未配置公钥时单独使用（直接放行）。配置了公钥时由 JwtGatewayTokenValidator（@Primary）优先注入。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class DefaultGatewayTokenValidator implements GatewayTokenValidator {

  @Override
  public Mono<ServerWebExchange> validate(ServerWebExchange exchange) {
    log.debug(
        "Gateway Token 校验（兜底实现）：默认放行 path={}", exchange.getRequest().getURI().getPath());
    return Mono.just(exchange);
  }
}
