/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.filter;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关 Token 校验器接口
 *
 * <p>与 common 的 {@code TokenValidator}（返回 LoginUser、供 Servlet SecurityContext）区分：
 * 本接口仅负责网关层「通过/拒绝」，且校验通过时负责将用户信息写入转发请求头，供下游从 Header 填充 SecurityContext。
 *
 * <p>使用场景：
 *
 * <ul>
 *   <li>JWT 本地验签（公钥）
 *   <li>远程 Introspect
 *   <li>自定义校验逻辑
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public interface GatewayTokenValidator {

  /**
   * 校验 Token 并可选地写入用户信息到转发请求头
   *
   * <p>从 exchange 的请求中提取 Token，校验通过时向请求头写入 X-User-Id、X-Username、X-User-Roles 等，
   * 并返回携带新请求的 {@link ServerWebExchange}；校验失败返回空 Mono。
   *
   * @param exchange 当前请求的 ServerWebExchange
   * @return 校验通过时返回包含已添加用户信息请求头的 exchange（Mono 非空），失败返回 Mono.empty()
   */
  Mono<ServerWebExchange> validate(ServerWebExchange exchange);
}
