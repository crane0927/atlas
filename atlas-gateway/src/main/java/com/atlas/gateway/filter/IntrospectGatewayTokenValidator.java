/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.gateway.filter;

import com.atlas.common.feature.core.result.Result;
import com.atlas.gateway.client.IntrospectDataDto;
import com.atlas.gateway.config.GatewayProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 基于 Auth Introspection 接口的网关 Token 校验器
 *
 * <p>从 Authorization: Bearer 提取 Token，调用 Auth 的 POST /api/v1/auth/introspect 校验； 通过后写入
 * X-User-Id、X-Username、X-User-Roles、X-User-Permissions 到转发请求头。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
public class IntrospectGatewayTokenValidator implements GatewayTokenValidator {

  private final String introspectUrl;
  private final String apiKey;
  private final WebClient webClient;

  public IntrospectGatewayTokenValidator(
      GatewayProperties gatewayProperties, WebClient.Builder webClientBuilder) {
    GatewayProperties.AuthConfig auth = gatewayProperties.getAuth();
    GatewayProperties.IntrospectConfig introspect = auth.getIntrospect();
    this.introspectUrl =
        introspect != null && introspect.getUrl() != null ? introspect.getUrl().trim() : "";
    this.apiKey =
        introspect != null && introspect.getApiKey() != null ? introspect.getApiKey().trim() : "";
    this.webClient = webClientBuilder.build();
  }

  @Override
  public Mono<ServerWebExchange> validate(ServerWebExchange exchange) {
    String token = extractBearerToken(exchange.getRequest());
    if (token == null || token.isEmpty()) {
      log.debug("请求中无 Bearer Token");
      return Mono.empty();
    }
    Map<String, String> body = new HashMap<>();
    body.put("token", token);

    return webClient
        .post()
        .uri(introspectUrl)
        .contentType(MediaType.APPLICATION_JSON)
        .headers(
            h -> {
              if (!apiKey.isEmpty()) {
                h.set("X-Introspect-Api-Key", apiKey);
              }
            })
        .bodyValue(body)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<Result<IntrospectDataDto>>() {})
        .flatMap(
            result -> {
              if (result == null || !result.isSuccess() || result.getData() == null) {
                log.debug("Introspection 响应无效");
                return Mono.empty();
              }
              IntrospectDataDto data = result.getData();
              if (!Boolean.TRUE.equals(data.getActive())) {
                log.debug("Introspection 返回 active=false");
                return Mono.empty();
              }
              ServerHttpRequest requestWithHeaders =
                  exchange
                      .getRequest()
                      .mutate()
                      .header(
                          JwtGatewayTokenValidator.HEADER_X_USER_ID,
                          data.getUserId() != null ? String.valueOf(data.getUserId()) : "")
                      .header(
                          JwtGatewayTokenValidator.HEADER_X_USERNAME,
                          data.getUsername() != null ? data.getUsername() : "")
                      .header(JwtGatewayTokenValidator.HEADER_X_USER_ROLES, join(data.getRoles()))
                      .header(
                          JwtGatewayTokenValidator.HEADER_X_USER_PERMISSIONS,
                          join(data.getPermissions()))
                      .build();
              return Mono.just(exchange.mutate().request(requestWithHeaders).build());
            })
        .onErrorResume(
            WebClientResponseException.class,
            e -> {
              log.debug(
                  "Introspection 调用失败: status={}, body={}",
                  e.getStatusCode(),
                  e.getResponseBodyAsString());
              return Mono.empty();
            })
        .onErrorResume(
            Exception.class,
            e -> {
              log.debug("Introspection 调用异常: {}", e.getMessage());
              return Mono.empty();
            });
  }

  private static String extractBearerToken(ServerHttpRequest request) {
    String authorization = request.getHeaders().getFirst("Authorization");
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      return null;
    }
    return authorization.substring(7).trim();
  }

  private static String join(List<String> list) {
    if (list == null || list.isEmpty()) {
      return "";
    }
    return String.join(",", list);
  }
}
