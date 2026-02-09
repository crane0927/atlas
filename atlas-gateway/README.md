# atlas-gateway

## 模块简介

`atlas-gateway` 是 Atlas 项目的 API 网关模块，作为微服务架构的统一入口，提供路由转发、CORS 跨域支持、TraceId 链路追踪、统一错误返回、鉴权控制等功能。Gateway 负责请求路由、跨域处理、链路追踪、错误处理和鉴权控制。

## 主要功能

### 1. 路由转发

Gateway 能够根据配置的路由规则将请求转发到对应的后端服务：
- **路径匹配**: 支持基于路径的路由匹配（Path Route）
- **路径重写**: 支持路径重写（StripPrefix、RewritePath）
- **负载均衡**: 支持负载均衡（如果后端有多个实例）
- **动态配置**: 路由规则支持通过 Nacos Config 配置和动态更新

### 2. CORS 跨域支持

Gateway 支持 CORS 跨域请求，允许前端应用跨域调用后端 API：
- **灵活配置**: 可以配置允许的源（origins）、方法（methods）、请求头（headers）
- **预检请求**: 预检请求（OPTIONS）能够正确处理
- **动态更新**: CORS 配置支持通过 Nacos Config 动态更新

### 3. TraceId 链路追踪

Gateway 自动处理 TraceId，确保所有请求都有 TraceId 用于链路追踪：
- **自动生成**: 如果请求头中没有 TraceId，则自动生成
- **自动传递**: TraceId 在请求头中传递到后端服务
- **响应返回**: TraceId 在响应头中返回给客户端
- **日志集成**: TraceId 与 `atlas-common-infra-logging` 模块的 TraceId 工具类集成，在日志中自动输出

### 4. 统一错误返回

Gateway 的所有错误响应使用统一的 `Result` 格式：
- **统一格式**: 所有错误响应使用 `atlas-common-feature-core` 模块的 `Result` 类
- **完整信息**: 错误响应包含错误码、错误消息、TraceId
- **错误码规范**: 错误码符合项目的错误码规范（6位数字，01 开头）

### 5. 鉴权控制（白名单 + GatewayTokenValidator）

Gateway 提供鉴权控制功能，支持白名单配置与 Token 校验（通过/拒绝，通过时传递用户信息头）：
- **白名单**: 白名单路径无需鉴权；建议只放行具体路径（如 `/api/v1/auth/login`、`/api/v1/auth/public-key`），勿使用 `/api/v1/auth/**` 以免误放行登出等敏感接口
- **GatewayTokenValidator**: 与 common 的 `TokenValidator`（供 Servlet 设置 SecurityContext）区分；网关侧接口为 `GatewayTokenValidator`，职责为「通过/拒绝」且「校验通过时写入 X-User-Id、X-Username、X-User-Roles、X-User-Permissions 等请求头转发下游」
- **JwtGatewayTokenValidator**: 当 `validation-mode=jwt` 且配置 `atlas.gateway.auth.jwt.public-key`（PEM）后启用，使用与 atlas-auth 一致的 RS256 公钥本地验签
- **IntrospectGatewayTokenValidator**: 当 `validation-mode=introspection` 且配置 `atlas.gateway.auth.introspect.url` 后启用，调用 Auth 的 Introspection 接口校验 Token
- **DefaultGatewayTokenValidator**: 未配置有效校验方式（公钥或 Introspection URL）时使用，**拒绝**非白名单请求并返回 401，避免误放行
- **鉴权失败**: 返回 **HTTP 401** 及统一错误体（错误码 013001、message、traceId）
- **动态配置**: 白名单支持通过 Nacos Config 动态更新

### 6. Nacos Config 配置管理

Gateway 的配置（路由规则、白名单、限流规则、CORS 配置）通过 Nacos Config 管理：
- **配置中心**: Gateway 对接 Nacos Config
- **动态更新**: 配置变更后 Gateway 能够动态生效（无需重启）
- **命名规范**: 配置项遵循项目的配置命名规范（`atlas.gateway.*`）

## 快速开始

### 前置条件

- JDK 21
- Spring Boot 3.5.9
- Spring Cloud 2025.0.1
- Spring Cloud Alibaba 2025.0.0.0
- Nacos Server（用于配置管理）
- 已安装 `atlas-common-feature-core` 模块
- 已安装 `atlas-common-infra-logging` 模块

### 配置 Nacos Config

在 Nacos 控制台创建配置：

**DataId**: `atlas-gateway-dev.yaml`  
**Group**: `DEFAULT_GROUP`  
**配置格式**: YAML

```yaml
atlas:
  gateway:
    routes:
      - id: health-route
        uri: http://localhost:8080
        predicates:
          - Path=/health/**
        filters:
          - StripPrefix=1
      - id: mock-route
        uri: http://localhost:8080
        predicates:
          - Path=/mock/**
        filters:
          - StripPrefix=1
    
    whitelist:
      enabled: true
      paths:
        - /health/**
        - /mock/**
        - /api/v1/auth/login
        - /api/v1/auth/public-key
    auth:
      validation-mode: jwt   # jwt | introspection
      jwt:
        public-key: ""   # 配置 PEM 公钥后启用 JWT 校验；未配置且非 introspection 时非白名单请求返回 401
        algorithm: RS256
      introspect:
        url: ""          # validation-mode=introspection 时必填，如 http://localhost:8084/api/v1/auth/introspect
        api-key: ""      # 与 atlas.auth.introspect.api-key 一致
    cors:
      allowed-origins: "*"
      allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
      allowed-headers: "*"
      allow-credentials: false   # 与 allowedOrigins=* 同用时浏览器会忽略凭证，需带凭证时请配置具体 origins
      max-age: 3600
```

### 启动应用

```bash
mvn spring-boot:run -pl atlas-gateway
```

## 使用示例

### 路由转发

Gateway 会自动根据路由规则转发请求到对应的后端服务。

**示例请求**:
```bash
# 转发到 /health 接口
curl http://localhost:8080/gateway/health

# 转发到 /mock 接口
curl http://localhost:8080/gateway/mock/test
```

### TraceId 传递

Gateway 会自动处理 TraceId，确保所有请求都有 TraceId。

**示例请求**:
```bash
# 不带 TraceId 的请求（Gateway 会自动生成）
curl http://localhost:8080/gateway/health

# 带 TraceId 的请求（Gateway 会使用请求头中的 TraceId）
curl -H "X-Trace-Id: abc123def456" http://localhost:8080/gateway/health
```

**响应头**:
```
X-Trace-Id: abc123def456
```

### CORS 跨域支持

Gateway 会自动处理 CORS 跨域请求。

**示例请求**:
```bash
# 预检请求（OPTIONS）
curl -X OPTIONS http://localhost:8080/gateway/health \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET"
```

### 统一错误返回

Gateway 的所有错误响应使用统一的 `Result` 格式。

**示例错误响应**:
```json
{
  "code": "010404",
  "message": "路由不存在",
  "data": null,
  "timestamp": 1706342400000,
  "traceId": "abc123def456"
}
```

### 白名单功能

白名单路径的请求可以直接通过 Gateway，无需 Token 校验。

**示例请求**:
```bash
# 白名单路径（无需 Token）
curl http://localhost:8080/gateway/health

# 非白名单路径（会触发 Token 校验；未配置公钥时默认放行，配置公钥后需携带有效 Bearer Token）
curl http://localhost:8080/gateway/api/user/info
curl -H "Authorization: Bearer <token>" http://localhost:8080/gateway/api/user/info
```

## 配置说明

### 路由配置

路由配置支持通过 `application.yml` 和 Nacos Config 两种方式配置。推荐使用 Nacos Config 配置，支持动态更新。

**配置项说明**:
- `id`: 路由 ID，唯一标识
- `uri`: 后端服务 URI
- `predicates`: 路由断言（匹配条件），如 `Path=/health/**`
- `filters`: 路由过滤器（路径重写等），如 `StripPrefix=1`

### 白名单配置

白名单配置支持通过 Nacos Config 动态更新。建议只放行具体路径，勿使用 `/api/v1/auth/**` 以免误放行登出等接口。

**配置项说明**:
- `enabled`: 是否启用白名单
- `paths`: 白名单路径列表（支持通配符），如 `/health/**`、`/api/v1/auth/login`、`/api/v1/auth/public-key`

### 鉴权配置（atlas.gateway.auth）

- `auth.validation-mode`: 校验方式，`jwt`（默认）或 `introspection`。
- `auth.jwt.public-key`: JWT 公钥（PEM 字符串）。当 validation-mode=jwt 且配置后启用 `JwtGatewayTokenValidator`；未配置时非白名单请求返回 401。
- `auth.introspect.url`: Introspection 接口地址。当 validation-mode=introspection 时必填。
- `auth.introspect.api-key`: 服务间认证 API Key，与 atlas.auth.introspect.api-key 一致。
- `auth.jwt.algorithm`: 算法，默认 RS256。

**错误码**: 鉴权失败固定返回业务错误码 `013001`（与 Auth 错误码体系统一约定），HTTP 状态码为 401。

### CORS 配置

CORS 配置支持通过 Nacos Config 动态更新。

**配置项说明**:
- `allowed-origins`: 允许的源（多个用逗号分隔，* 表示所有）
- `allowed-methods`: 允许的 HTTP 方法（多个用逗号分隔）
- `allowed-headers`: 允许的请求头（多个用逗号分隔，* 表示所有）
- `allow-credentials`: 是否允许携带凭证（与 allowedOrigins=* 同用时浏览器会忽略，需带凭证时请配置具体 origins）
- `max-age`: 预检请求缓存时间（秒）

## GatewayTokenValidator 与 common TokenValidator 区分

- **atlas-gateway**：`GatewayTokenValidator`，方法 `Mono<ServerWebExchange> validate(ServerWebExchange exchange)`。用于网关层「通过/拒绝」；校验通过时在转发请求上添加 X-User-Id、X-Username、X-User-Roles 等请求头。
- **atlas-common-feature-security**：`TokenValidator`，方法 `LoginUser validateToken(String token)`。供后端 Servlet 服务从 Token 解析用户并设置 SecurityContextHolder。

下游服务可优先从请求头（X-User-*）构建 LoginUser 并设置 SecurityContext（`atlas-common-infra-web` 的 `SecurityContextFilter` 已支持），若无则回退为 Token 解析。

## 验收测试

### 1. 健康检查接口测试

```bash
# 测试 Gateway 能否转发请求到临时的 health/mock 接口
curl http://localhost:8080/gateway/health
curl http://localhost:8080/gateway/mock/test
```

### 2. TraceId 传递测试

```bash
# 测试 TraceId 是否在请求头和响应头中传递
curl -v http://localhost:8080/gateway/health
# 检查响应头中的 X-Trace-Id
```

### 3. 统一错误码测试

```bash
# 测试不存在的路由，应该返回统一错误格式
curl http://localhost:8080/gateway/not-exist
# 应该返回 Result 格式的错误响应，错误码为 010404
```

### 4. 白名单测试

```bash
# 测试白名单路径（应该能正常通过）
curl http://localhost:8080/gateway/health

# 测试非白名单路径（会触发 Token 校验，占位实现默认放行）
curl http://localhost:8080/gateway/api/user/info
```

### 5. 配置更新测试

1. 在 Nacos 控制台修改路由规则或白名单配置
2. 等待配置生效（< 5 秒）
3. 测试配置是否生效

## 常见问题

### Q1: Gateway 无法启动？

**A**: 检查以下几点：
- 确认 Nacos Server 是否启动
- 确认 Nacos Config 配置是否正确
- 确认依赖是否正确添加

### Q2: 路由转发失败？

**A**: 检查以下几点：
- 确认路由规则配置是否正确
- 确认后端服务是否启动
- 确认后端服务 URI 是否正确

### Q3: TraceId 没有传递？

**A**: 检查以下几点：
- 确认 `TraceIdGatewayFilter` 是否正确配置
- 确认 `TraceIdUtil` 是否正确集成
- 检查日志中是否有 TraceId

### Q4: CORS 跨域请求失败？

**A**: 检查以下几点：
- 确认 CORS 配置是否正确
- 确认预检请求（OPTIONS）是否正确处理
- 检查浏览器控制台的错误信息

### Q5: 配置更新不生效？

**A**: 检查以下几点：
- 确认 Nacos Config 配置变更监听是否正确实现
- 确认配置刷新机制是否正确配置
- 检查日志中是否有配置更新记录

## 最佳实践

### 1. 路由配置最佳实践

- **使用 Nacos Config 配置路由**: 推荐使用 Nacos Config 配置路由规则，支持动态更新
- **路由 ID 命名规范**: 使用有意义的路由 ID，如 `user-service-route`、`order-service-route`
- **路径匹配策略**: 使用 Ant 风格路径匹配，如 `/api/user/**`、`/api/order/**`
- **负载均衡**: 如果后端有多个实例，使用服务发现（如 Nacos Discovery）进行负载均衡

### 2. CORS 配置最佳实践

- **生产环境限制源**: 生产环境不要使用 `*` 作为允许的源，应该指定具体的域名
- **最小权限原则**: 只允许必要的 HTTP 方法和请求头
- **预检请求缓存**: 合理设置 `max-age`，减少预检请求次数

### 3. 白名单配置最佳实践

- **最小化白名单**: 只将必要的公开接口加入白名单
- **路径规范**: 使用统一的路径前缀，如 `/api/public/**`、`/api/open/**`
- **定期审查**: 定期审查白名单配置，移除不再需要的路径

### 4. TraceId 使用最佳实践

- **客户端传递 TraceId**: 客户端应该从响应头中获取 TraceId，并在后续请求中传递
- **日志记录**: 确保所有关键日志都包含 TraceId，便于问题排查
- **监控集成**: 将 TraceId 集成到监控系统，实现分布式追踪

### 5. 错误处理最佳实践

- **统一错误格式**: 所有错误响应都使用统一的 `Result` 格式
- **错误码规范**: 错误码符合项目规范（6位数字，01 开头）
- **错误日志**: 记录详细的错误日志，包含 TraceId 和错误堆栈

### 6. 性能优化最佳实践

- **连接池配置**: 合理配置 HTTP 连接池大小
- **超时配置**: 设置合理的请求超时时间
- **限流配置**: 在生产环境启用限流功能，防止服务过载

### 7. 安全最佳实践

- **Token 校验**: 实现具体的 Token 校验逻辑，不要使用占位实现
- **HTTPS**: 生产环境使用 HTTPS 协议
- **敏感信息**: 不要在日志中记录敏感信息（如 Token、密码）

## Docker 部署

### 目录结构

```
atlas-gateway/
├── docker/
│   ├── Dockerfile.build    # 编译阶段 Dockerfile
│   └── Dockerfile.run      # 运行阶段 Dockerfile
├── src/
└── pom.xml
```

### 构建镜像

#### 方式一：本地构建后打包（推荐用于开发）

```bash
# 1. 本地 Maven 构建
mvn clean package -DskipTests

# 2. 构建运行镜像
docker build -f docker/Dockerfile.run -t atlas-gateway:latest .
```

#### 方式二：使用编译镜像构建

```bash
# 使用编译阶段 Dockerfile 构建（需要在项目根目录执行）
docker build -f atlas-gateway/docker/Dockerfile.build -t atlas-gateway-build .
```

### 运行容器

```bash
# 基本运行
docker run -d \
  --name atlas-gateway \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e NACOS_SERVER_ADDR=host.docker.internal:8848 \
  atlas-gateway:latest

# 自定义 JVM 参数
docker run -d \
  --name atlas-gateway \
  -p 8080:8080 \
  -e JAVA_OPTS="-Xms512m -Xmx1024m" \
  atlas-gateway:latest
```

### 健康检查

```bash
# 检查服务健康状态
curl http://localhost:8080/actuator/health

# 查看容器健康状态
docker inspect --format='{{.State.Health.Status}}' atlas-gateway
```

### 查看日志

```bash
docker logs -f atlas-gateway
```

## 参考资源

- [Spring Cloud Gateway 官方文档](https://spring.io/projects/spring-cloud-gateway)
- [Nacos Config 官方文档](https://nacos.io/docs/latest/guide/user/quick-start/)
- [项目配置命名规范](../../docs/engineering-standards/config-naming.md)
- [项目错误码规范](../../docs/engineering-standards/error-code.md)
- [快速开始指南](../../specs/008-gateway/quickstart.md)
- [验收测试文档](../../specs/008-gateway/acceptance-tests.md)

