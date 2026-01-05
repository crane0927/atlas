# 技术调研文档

## 调研目标

为"创建 atlas-gateway 模块"功能提供技术决策依据，解决规划中的技术选型和设计问题。

## 决策记录

### 决策 1: Spring Cloud Gateway 版本和配置方式

**问题**: 如何选择 Spring Cloud Gateway 版本和配置方式？

**决策**: 使用 Spring Cloud Gateway 2025.1.0 版本，支持通过 Nacos Config 动态配置路由规则

**理由**:
1. **版本兼容性**: Spring Cloud Gateway 2025.1.0 与 Spring Boot 4.0.1 和 Spring Cloud 2025.1.0 兼容
2. **官方组件**: Spring Cloud Gateway 是 Spring Cloud 官方组件，符合项目宪法原则 5
3. **功能完整性**: 支持路由转发、过滤器、负载均衡等功能
4. **动态配置**: 支持通过配置中心动态更新路由规则

**实现方式**:
```java
@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("health-route", r -> r.path("/health/**")
                .uri("http://localhost:8080"))
            .build();
    }
}
```

**替代方案考虑**:
- **Zuul**: 已停止维护，不推荐使用
- **Kong**: 第三方网关，不符合项目宪法原则 5（优先使用 Spring Cloud 生态）

### 决策 2: Nacos Config 配置管理方式

**问题**: 如何实现 Gateway 路由规则通过 Nacos Config 动态配置？

**决策**: 使用 `RouteDefinitionLocator` 和 `RouteDefinitionWriter` 实现动态路由管理

**理由**:
1. **官方支持**: Spring Cloud Gateway 提供 `RouteDefinitionLocator` 接口支持动态路由
2. **配置中心集成**: Nacos Config 支持配置变更监听，可以实现动态更新
3. **灵活性**: 支持通过代码和配置文件两种方式配置路由
4. **可维护性**: 路由规则集中管理，便于维护

**实现方式**:
```java
@Component
public class NacosRouteDefinitionLocator implements RouteDefinitionLocator {
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        // 从 Nacos Config 读取路由配置
        // 解析配置并转换为 RouteDefinition
    }
}
```

**替代方案考虑**:
- **静态配置**: 使用 `application.yml` 配置路由，但不支持动态更新
- **数据库存储**: 将路由规则存储在数据库中，但增加数据库依赖

### 决策 3: TraceId 在 Gateway 中的处理方式

**问题**: 如何在 Gateway 中处理 TraceId，确保 TraceId 能够传递到后端服务？

**决策**: 使用 `GlobalFilter` 实现 TraceId 处理，复用 `atlas-common-infra-logging` 模块的 `TraceIdUtil`

**理由**:
1. **统一性**: 复用现有 TraceId 工具类，保持 TraceId 格式一致
2. **执行顺序**: `GlobalFilter` 在路由匹配之前执行，可以确保所有请求都有 TraceId
3. **响应式支持**: Gateway 使用 WebFlux，`GlobalFilter` 支持响应式编程
4. **易于维护**: 集中处理 TraceId，代码简洁

**实现方式**:
```java
@Component
public class TraceIdGatewayFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String traceId = request.getHeaders().getFirst("X-Trace-Id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = TraceIdUtil.generate();
        }
        TraceIdUtil.setTraceId(traceId);
        
        // 添加到响应头
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add("X-Trace-Id", traceId);
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            TraceIdUtil.clear();
        }));
    }
}
```

**替代方案考虑**:
- **自定义 Filter**: 可以实现，但不如复用现有工具类简洁
- **不处理 TraceId**: 不符合需求，不推荐

### 决策 4: Gateway 错误处理方式

**问题**: 如何在 Gateway 中实现统一错误处理，返回 `Result` 格式响应？

**决策**: 使用 `ErrorWebExceptionHandler` 接口实现全局异常处理

**理由**:
1. **官方支持**: Spring Cloud Gateway 提供 `ErrorWebExceptionHandler` 接口处理异常
2. **统一格式**: 所有错误响应使用统一的 `Result` 格式
3. **响应式支持**: `ErrorWebExceptionHandler` 支持 WebFlux 响应式编程
4. **易于扩展**: 可以轻松扩展支持更多异常类型

**实现方式**:
```java
@Component
@Order(-1)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        Result<Void> result = Result.error("010000", "Gateway 错误");
        // 设置 TraceId
        result.setTraceId(TraceIdUtil.getTraceId());
        
        DataBuffer buffer = response.bufferFactory()
            .wrap(JSON.toJSONString(result).getBytes());
        return response.writeWith(Mono.just(buffer));
    }
}
```

**替代方案考虑**:
- **@ControllerAdvice**: Gateway 使用 WebFlux，不支持 `@ControllerAdvice`
- **Fallback**: 可以使用 Fallback，但不如全局异常处理器统一

### 决策 5: CORS 跨域配置方式

**问题**: 如何在 Gateway 中配置 CORS 跨域支持？

**决策**: 使用 Spring Cloud Gateway 的 CORS 配置，支持通过 Nacos Config 动态配置

**理由**:
1. **官方支持**: Spring Cloud Gateway 内置 CORS 支持，配置简单
2. **动态配置**: 支持通过 Nacos Config 动态更新 CORS 配置
3. **灵活性**: 支持配置允许的源、方法、请求头等
4. **性能**: Gateway 内置 CORS 处理，性能优于自定义 Filter

**实现方式**:
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
```

**替代方案考虑**:
- **自定义 Filter**: 可以实现，但不如使用官方 CORS 配置简洁
- **Nginx 配置**: 在 Nginx 层配置 CORS，但不符合 Gateway 统一管理的要求

### 决策 6: 白名单和 Token 校验实现方式

**问题**: 如何实现 Gateway 白名单和 Token 校验功能？

**决策**: 使用 `GlobalFilter` 实现白名单检查，提供 Token 校验扩展点（占位实现，默认放行）

**理由**:
1. **执行顺序**: `GlobalFilter` 在路由匹配之前执行，可以控制请求是否继续
2. **灵活性**: 白名单支持通配符匹配，易于配置
3. **可扩展性**: Token 校验提供扩展点，便于后续实现具体校验逻辑
4. **占位实现**: Token 校验占位实现默认放行，不影响当前功能

**实现方式**:
```java
@Component
public class AuthGatewayFilter implements GlobalFilter, Ordered {
    @Autowired
    private GatewayProperties gatewayProperties;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // 检查白名单
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }
        
        // Token 校验（占位实现，默认放行）
        if (validateToken(request)) {
            return chain.filter(exchange);
        }
        
        // Token 校验失败，返回错误
        return handleAuthError(exchange);
    }
    
    private boolean isWhitelisted(String path) {
        List<String> whitelist = gatewayProperties.getWhitelist().getPaths();
        return whitelist.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
    
    private boolean validateToken(ServerHttpRequest request) {
        // 占位实现，默认放行
        // 后续可以扩展实现具体的 Token 校验逻辑
        return true;
    }
}
```

**替代方案考虑**:
- **Spring Security**: 可以使用 Spring Security，但 Gateway 使用 WebFlux，需要 Spring Security WebFlux，复杂度较高
- **不实现白名单**: 不符合需求，不推荐

### 决策 7: 配置属性绑定方式

**问题**: 如何绑定 Gateway 配置属性（路由规则、白名单、CORS 等）？

**决策**: 使用 `@ConfigurationProperties` 注解绑定配置属性，支持从 Nacos Config 读取

**理由**:
1. **官方支持**: Spring Boot 提供 `@ConfigurationProperties` 注解，支持类型安全的配置绑定
2. **动态更新**: Nacos Config 支持配置变更监听，可以实现动态更新
3. **命名规范**: 配置项遵循 `atlas.gateway.*` 命名规范
4. **易于维护**: 配置集中管理，便于维护

**实现方式**:
```java
@Data
@ConfigurationProperties(prefix = "atlas.gateway")
public class GatewayProperties {
    private List<RouteConfig> routes = new ArrayList<>();
    private WhitelistConfig whitelist = new WhitelistConfig();
    private CorsConfig cors = new CorsConfig();
    
    @Data
    public static class RouteConfig {
        private String id;
        private String uri;
        private List<String> predicates;
        private List<String> filters;
    }
    
    @Data
    public static class WhitelistConfig {
        private Boolean enabled = true;
        private List<String> paths = new ArrayList<>();
    }
    
    @Data
    public static class CorsConfig {
        private String allowedOrigins = "*";
        private String allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";
        private String allowedHeaders = "*";
        private Boolean allowCredentials = true;
        private Integer maxAge = 3600;
    }
}
```

**替代方案考虑**:
- **@Value 注解**: 可以使用 `@Value` 注解，但不如 `@ConfigurationProperties` 类型安全
- **硬编码**: 不符合配置管理要求，不推荐

## 技术选型总结

| 技术点 | 选型 | 理由 |
|--------|------|------|
| API 网关 | Spring Cloud Gateway 2025.1.0 | Spring Cloud 官方组件，符合项目宪法 |
| 配置管理 | Nacos Config | Spring Cloud Alibaba 官方组件，支持动态配置 |
| 路由配置 | RouteDefinitionLocator + RouteDefinitionWriter | 官方支持动态路由管理 |
| TraceId 处理 | GlobalFilter + TraceIdUtil | 复用现有工具类，保持一致性 |
| 错误处理 | ErrorWebExceptionHandler | Gateway 官方异常处理接口 |
| CORS 配置 | CorsWebFilter | Gateway 内置 CORS 支持 |
| 白名单和 Token 校验 | GlobalFilter | 执行顺序早，易于控制 |
| 配置属性绑定 | @ConfigurationProperties | 类型安全，易于维护 |

## 参考资源

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Spring Cloud Gateway Route Definition](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#route-definition-locator)
- [Nacos Config Documentation](https://nacos.io/docs/latest/guide/user/quick-start/)
- [WebFlux Error Handling](https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/exception-handler.html)

