/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.gateway.config;

import com.atlas.gateway.filter.GatewayTokenValidator;
import com.atlas.gateway.filter.IntrospectGatewayTokenValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Gateway Introspection 方式鉴权配置
 *
 * <p>当 {@code atlas.gateway.auth.validation-mode} 为 introspection 且配置了 {@code
 * atlas.gateway.auth.introspect.url} 时，注册 IntrospectGatewayTokenValidator 为 @Primary。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnExpression(
    "'${atlas.gateway.auth.validation-mode:jwt}' == 'introspection' "
        + "and !'${atlas.gateway.auth.introspect.url:}'.trim().isEmpty()")
public class IntrospectGatewayConfiguration {

  @Bean
  @Primary
  public GatewayTokenValidator introspectGatewayTokenValidator(
      GatewayProperties gatewayProperties, WebClient.Builder webClientBuilder) {
    return new IntrospectGatewayTokenValidator(gatewayProperties, webClientBuilder);
  }
}
