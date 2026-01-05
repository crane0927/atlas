# 数据模型文档

## 概述

本文档定义 `atlas-gateway` 模块涉及的数据模型，包括配置属性类、路由定义、过滤器配置等。

## 配置属性类

### GatewayProperties（Gateway 配置属性类）

**描述**: Gateway 配置属性类，用于绑定配置文件中的 Gateway 相关配置。

**包名**: `com.atlas.gateway.config`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| routes | List<RouteConfig> | 路由规则列表 | 否 | [] |
| whitelist | WhitelistConfig | 白名单配置 | 否 | new WhitelistConfig() |
| cors | CorsConfig | CORS 配置 | 否 | new CorsConfig() |
| rateLimit | RateLimitConfig | 限流配置（可选） | 否 | new RateLimitConfig() |

**配置前缀**: `atlas.gateway`

**约束规则**:
- 配置项遵循项目的配置命名规范（`atlas.gateway.*`）
- 支持从 Nacos Config 读取配置
- 支持配置动态更新

### RouteConfig（路由配置类）

**描述**: Gateway 路由规则配置，定义请求路径与后端服务的映射关系。

**包名**: `com.atlas.gateway.config`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| id | String | 路由 ID，唯一标识 | 是 | - |
| uri | String | 后端服务 URI | 是 | - |
| predicates | List<String> | 路由断言（匹配条件），如 `Path=/health/**` | 是 | [] |
| filters | List<String> | 路由过滤器（路径重写等），如 `StripPrefix=1` | 否 | [] |

**约束规则**:
- `id` 必须唯一
- `uri` 必须是有效的 URI 格式
- `predicates` 至少包含一个断言
- `filters` 可选，支持多个过滤器

**示例**:
```java
RouteConfig route = new RouteConfig();
route.setId("health-route");
route.setUri("http://localhost:8080");
route.setPredicates(Arrays.asList("Path=/health/**"));
route.setFilters(Arrays.asList("StripPrefix=1"));
```

### WhitelistConfig（白名单配置类）

**描述**: Gateway 白名单配置，定义无需鉴权的路径列表。

**包名**: `com.atlas.gateway.config`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| enabled | Boolean | 是否启用白名单 | 否 | true |
| paths | List<String> | 白名单路径列表（支持通配符） | 否 | [] |

**约束规则**:
- `paths` 支持通配符匹配（如 `/health/**`、`/api/public/**`）
- 路径匹配使用 Ant 风格路径匹配器

**示例**:
```java
WhitelistConfig whitelist = new WhitelistConfig();
whitelist.setEnabled(true);
whitelist.setPaths(Arrays.asList("/health/**", "/mock/**", "/api/public/**"));
```

### CorsConfig（CORS 配置类）

**描述**: Gateway CORS 跨域配置。

**包名**: `com.atlas.gateway.config`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| allowedOrigins | String | 允许的源（多个用逗号分隔，* 表示所有） | 否 | "*" |
| allowedMethods | String | 允许的 HTTP 方法（多个用逗号分隔） | 否 | "GET,POST,PUT,DELETE,OPTIONS" |
| allowedHeaders | String | 允许的请求头（多个用逗号分隔，* 表示所有） | 否 | "*" |
| allowCredentials | Boolean | 是否允许携带凭证 | 否 | true |
| maxAge | Integer | 预检请求缓存时间（秒） | 否 | 3600 |

**约束规则**:
- `allowedOrigins` 支持 `*` 表示所有源，或指定具体域名
- `allowedMethods` 支持多个 HTTP 方法，用逗号分隔
- `allowedHeaders` 支持 `*` 表示所有请求头，或指定具体请求头
- `maxAge` 必须大于等于 0

**示例**:
```java
CorsConfig cors = new CorsConfig();
cors.setAllowedOrigins("*");
cors.setAllowedMethods("GET,POST,PUT,DELETE,OPTIONS");
cors.setAllowedHeaders("*");
cors.setAllowCredentials(true);
cors.setMaxAge(3600);
```

### RateLimitConfig（限流配置类，可选）

**描述**: Gateway 限流规则配置（如果实现限流功能）。

**包名**: `com.atlas.gateway.config`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| enabled | Boolean | 是否启用限流 | 否 | false |
| defaultQps | Integer | 默认 QPS 限制 | 否 | 1000 |
| rules | List<RateLimitRule> | 限流规则列表 | 否 | [] |

**约束规则**:
- `defaultQps` 必须大于 0
- `rules` 中的规则按顺序匹配，第一个匹配的规则生效

**示例**:
```java
RateLimitConfig rateLimit = new RateLimitConfig();
rateLimit.setEnabled(true);
rateLimit.setDefaultQps(1000);
RateLimitRule rule = new RateLimitRule();
rule.setPath("/api/**");
rule.setQps(500);
rateLimit.setRules(Arrays.asList(rule));
```

### RateLimitRule（限流规则类，可选）

**描述**: Gateway 限流规则，定义特定路径的 QPS 限制。

**包名**: `com.atlas.gateway.config`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| path | String | 路径匹配（支持通配符） | 是 | - |
| qps | Integer | 该路径的 QPS 限制 | 是 | - |

**约束规则**:
- `path` 支持通配符匹配（如 `/api/**`）
- `qps` 必须大于 0

## Filter 配置

### TraceIdGatewayFilter（TraceId Filter 类）

**描述**: Gateway TraceId Filter，自动处理 TraceId，确保所有请求都有 TraceId。

**包名**: `com.atlas.gateway.filter`

**方法定义**:

| 方法名 | 参数 | 返回类型 | 说明 | 必填 |
|--------|------|----------|------|------|
| filter | ServerWebExchange, GatewayFilterChain | Mono<Void> | 过滤请求，设置 TraceId | 是 |
| getOrder | - | int | 获取 Filter 执行顺序 | 是 |

**配置项**:

| 配置项 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| traceIdHeader | String | TraceId 请求头名称 | X-Trace-Id |
| addResponseHeader | Boolean | 是否在响应头中添加 TraceId | true |
| order | Integer | Filter 执行顺序 | Ordered.HIGHEST_PRECEDENCE |

**约束规则**:
- 从请求头 `X-Trace-Id` 获取 TraceId
- 如果请求头中没有 TraceId，则自动生成
- TraceId 设置到 `TraceIdUtil`（复用 `atlas-common-infra-logging` 模块的工具类）
- TraceId 添加到响应头
- 请求结束后清理 TraceId，避免内存泄漏

### AuthGatewayFilter（鉴权 Filter 类）

**描述**: Gateway 鉴权 Filter，实现白名单检查和 Token 校验。

**包名**: `com.atlas.gateway.filter`

**方法定义**:

| 方法名 | 参数 | 返回类型 | 说明 | 必填 |
|--------|------|----------|------|------|
| filter | ServerWebExchange, GatewayFilterChain | Mono<Void> | 过滤请求，检查白名单和 Token | 是 |
| getOrder | - | int | 获取 Filter 执行顺序 | 是 |
| isWhitelisted | String | boolean | 检查路径是否在白名单中 | 是 |
| validateToken | ServerHttpRequest | boolean | Token 校验（占位实现，默认放行） | 是 |
| handleAuthError | ServerWebExchange | Mono<Void> | 处理鉴权错误 | 是 |

**配置项**:

| 配置项 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| order | Integer | Filter 执行顺序 | Ordered.HIGHEST_PRECEDENCE + 1 |

**约束规则**:
- 白名单路径的请求直接放行
- 非白名单路径的请求会触发 Token 校验
- Token 校验占位实现默认放行（便于后续扩展）
- Token 校验失败返回统一错误格式（错误码：013001）

### GatewayExceptionHandler（Gateway 异常处理器类）

**描述**: Gateway 全局异常处理器，统一处理 Gateway 异常并返回 `Result` 格式响应。

**包名**: `com.atlas.gateway.exception`

**方法定义**:

| 方法名 | 参数 | 返回类型 | 说明 | 必填 |
|--------|------|----------|------|------|
| handle | ServerWebExchange, Throwable | Mono<Void> | 处理异常，返回统一错误响应 | 是 |

**处理的异常类型**:

| 异常类型 | 错误码 | HTTP 状态码 | 说明 |
|----------|--------|-------------|------|
| NotFoundException | 010404 | 404 | 路由不存在 |
| ServiceUnavailableException | 010503 | 503 | 服务不可用 |
| TimeoutException | 010002 | 504 | 请求超时 |
| GatewayException | 010000 | 500 | Gateway 其他异常 |

**约束规则**:
- 所有异常响应使用统一的 `Result` 格式
- 错误响应包含错误码、错误消息、TraceId
- 错误码符合项目错误码规范（01 开头）

## 依赖关系

### 内部依赖

- **atlas-common-feature-core**: 
  - `Result` 类：统一响应格式
  - `CommonErrorCode`：通用错误码常量
- **atlas-common-infra-logging**:
  - `TraceIdUtil`：TraceId 管理工具类

### 外部依赖

- **spring-cloud-starter-gateway**: Spring Cloud Gateway 核心功能
- **spring-cloud-starter-alibaba-nacos-config**: Nacos Config 配置管理
- **spring-cloud-starter-alibaba-nacos-discovery**: Nacos 服务发现（可选）

## 数据流

### 请求处理流程

```
客户端请求
    ↓
Gateway 接收请求
    ↓
TraceId Filter（获取或生成 TraceId）
    ↓
CORS Filter（处理预检请求）
    ↓
Auth Filter（检查白名单和 Token）
    ↓
路由匹配
    ↓
请求转发到后端服务（携带 TraceId）
    ↓
接收后端服务响应
    ↓
添加 TraceId 到响应头
    ↓
返回响应给客户端
```

### TraceId 处理流程

```
请求到达 Gateway
    ↓
从请求头获取 TraceId（X-Trace-Id）
    ↓
如果不存在，生成新的 TraceId
    ↓
设置 TraceId 到 TraceIdUtil（ThreadLocal + MDC）
    ↓
添加到转发请求的请求头
    ↓
转发请求到后端服务
    ↓
接收响应
    ↓
添加 TraceId 到响应头
    ↓
清理 TraceId（避免内存泄漏）
```

### 错误处理流程

```
Gateway 异常发生
    ↓
GatewayExceptionHandler 捕获异常
    ↓
根据异常类型确定错误码和错误消息
    ↓
从 TraceIdUtil 获取 TraceId
    ↓
构建 Result 错误响应
    ↓
返回统一错误格式
```

