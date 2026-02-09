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
 * <p>始终注册；未配置公钥时由本实现处理非白名单请求。为避免「未配公钥即放行」的安全风险，
 * 本实现不校验 Token 且不放行，直接返回空 Mono，由 AuthGatewayFilter 返回 401。
 * 配置了公钥时由 JwtGatewayTokenValidator（@Primary）优先注入并正常验签放行。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class DefaultGatewayTokenValidator implements GatewayTokenValidator {

  @Override
  public Mono<ServerWebExchange> validate(ServerWebExchange exchange) {
    log.warn(
        "Gateway 未配置 JWT 公钥，拒绝非白名单请求: path={}. 请配置 atlas.gateway.auth.jwt.public-key",
        exchange.getRequest().getURI().getPath());
    return Mono.empty();
  }
}
