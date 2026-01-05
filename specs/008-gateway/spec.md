# 功能规格说明

## 宪法检查

本规格说明必须符合项目宪法要求：

- ✅ **模块化**: 功能归属 `atlas-gateway` 模块
- ✅ **代码复用**: 复用 `atlas-common-infra-logging` 的 TraceId 工具类和 `atlas-common-feature-core` 的统一响应格式
- ✅ **中文注释**: 所有代码使用中文注释
- ✅ **RESTful API**: Gateway 转发遵循 RESTful 设计规范
- ✅ **统一响应格式**: 使用 `atlas-common-feature-core` 模块的 `Result` 类

## 功能描述

创建 `atlas-gateway` 模块，实现 API 网关功能，包括路由转发、CORS 跨域支持、TraceId 链路追踪、统一错误返回、鉴权（白名单 + token 校验占位）等功能。Gateway 作为微服务架构的统一入口，负责请求路由、跨域处理、链路追踪、错误处理和鉴权控制。路由规则、白名单和限流规则通过 Nacos Config 进行配置管理，支持动态更新。

## 用户故事

### US1: 路由转发

**作为** 前端开发者或客户端  
**我希望** 通过统一的网关入口访问后端服务  
**以便** 简化服务调用和统一管理

**验收标准**:
- Gateway 能够根据路由规则转发请求到对应的后端服务
- 支持路径匹配和重写
- 支持负载均衡（如果后端有多个实例）
- 路由规则可以通过 Nacos Config 配置
- Gateway 能够转发请求到临时的 health/mock 接口用于验收测试

### US2: CORS 跨域支持

**作为** 前端开发者  
**我希望** Gateway 能够处理跨域请求  
**以便** 前端应用可以正常调用后端 API

**验收标准**:
- Gateway 支持 CORS 跨域配置
- 可以配置允许的源（origins）、方法（methods）、请求头（headers）
- CORS 配置可以通过 Nacos Config 动态更新
- 预检请求（OPTIONS）能够正确处理

### US3: TraceId 链路追踪

**作为** 开发人员或运维人员  
**我希望** 所有经过 Gateway 的请求都有 TraceId  
**以便** 追踪请求在整个微服务系统中的调用链

**验收标准**:
- Gateway 自动生成或传递 TraceId
- TraceId 在请求头中传递到后端服务
- TraceId 在响应中返回给客户端
- TraceId 与 `atlas-common-infra-logging` 模块的 TraceId 工具类集成
- TraceId 在日志中自动输出

### US4: 统一错误返回

**作为** 前端开发者  
**我希望** Gateway 返回统一的错误格式  
**以便** 前端能够统一处理错误

**验收标准**:
- Gateway 的错误响应使用 `Result` 格式
- 错误响应包含错误码、错误消息、TraceId
- 路由失败、服务不可用等场景返回统一格式
- 错误码符合项目的错误码规范（6位数字）

### US5: 鉴权控制（白名单 + Token 校验占位）

**作为** 系统管理员  
**我希望** Gateway 能够控制哪些请求需要鉴权  
**以便** 保护后端服务

**验收标准**:
- Gateway 支持白名单配置（白名单路径无需鉴权）
- Gateway 提供 Token 校验的扩展点（占位实现，可先放行）
- 白名单配置可以通过 Nacos Config 配置
- Token 校验逻辑可以后续扩展实现
- 非白名单且未通过 Token 校验的请求返回统一错误格式

### US6: Nacos Config 配置管理

**作为** 系统管理员  
**我希望** Gateway 的配置可以通过 Nacos Config 管理  
**以便** 动态更新配置而无需重启服务

**验收标准**:
- Gateway 对接 Nacos Config
- 路由规则可以通过 Nacos Config 配置
- 白名单可以通过 Nacos Config 配置
- 限流规则可以通过 Nacos Config 配置（如果实现限流功能）
- CORS 配置可以通过 Nacos Config 配置
- 配置变更后 Gateway 能够动态生效

## 功能需求

### FR1: 路由转发

**需求描述**: Gateway 能够根据配置的路由规则将请求转发到对应的后端服务。

**功能要求**:
- 支持基于路径的路由匹配（Path Route）
- 支持路径重写（StripPrefix、RewritePath）
- 支持负载均衡（如果后端有多个实例）
- 路由规则支持通过 Nacos Config 配置
- 路由规则支持动态更新
- 提供默认的健康检查路由（/health 或 /mock/health）用于验收测试
- 路由配置包含完整的中文注释

**验收标准**:
- Gateway 能够根据路由规则正确转发请求
- 路径匹配和重写功能正常工作
- 路由规则可以通过 Nacos Config 配置和更新
- Gateway 能够转发请求到临时的 health/mock 接口
- 路由配置包含完整的中文注释

### FR2: CORS 跨域支持

**需求描述**: Gateway 支持 CORS 跨域请求，允许前端应用跨域调用后端 API。

**功能要求**:
- 支持配置允许的源（allowedOrigins）
- 支持配置允许的 HTTP 方法（allowedMethods）
- 支持配置允许的请求头（allowedHeaders）
- 支持配置是否允许携带凭证（allowCredentials）
- 支持配置预检请求的缓存时间（maxAge）
- CORS 配置支持通过 Nacos Config 配置
- CORS 配置支持动态更新
- CORS 配置包含完整的中文注释

**验收标准**:
- 跨域请求能够正常处理
- 预检请求（OPTIONS）能够正确处理
- CORS 配置可以通过 Nacos Config 配置和更新
- CORS 配置包含完整的中文注释

### FR3: TraceId 链路追踪

**需求描述**: Gateway 自动处理 TraceId，确保所有请求都有 TraceId 用于链路追踪。

**功能要求**:
- Gateway 从请求头 `X-Trace-Id` 获取 TraceId
- 如果请求头中没有 TraceId，则自动生成
- TraceId 通过请求头传递到后端服务
- TraceId 在响应头中返回给客户端
- TraceId 与 `atlas-common-infra-logging` 模块的 `TraceIdUtil` 集成
- TraceId 在 Gateway 日志中自动输出
- TraceId 处理包含完整的中文注释

**验收标准**:
- 所有经过 Gateway 的请求都有 TraceId
- TraceId 能够正确传递到后端服务
- TraceId 能够在响应中返回
- TraceId 与日志系统集成，能够在日志中看到 TraceId
- TraceId 处理包含完整的中文注释

### FR4: 统一错误返回

**需求描述**: Gateway 的所有错误响应使用统一的 `Result` 格式，包含错误码、错误消息和 TraceId。

**功能要求**:
- Gateway 的错误响应使用 `atlas-common-feature-core` 模块的 `Result` 类
- 路由失败（404）返回统一错误格式
- 服务不可用（503）返回统一错误格式
- 请求超时返回统一错误格式
- 其他 Gateway 异常返回统一错误格式
- 错误响应包含错误码（符合项目错误码规范，6位数字）
- 错误响应包含错误消息
- 错误响应包含 TraceId
- 错误处理包含完整的中文注释

**验收标准**:
- 所有 Gateway 错误响应使用统一的 `Result` 格式
- 错误响应包含错误码、错误消息、TraceId
- 错误码符合项目错误码规范
- 错误处理包含完整的中文注释

### FR5: 鉴权控制（白名单 + Token 校验占位）

**需求描述**: Gateway 提供鉴权控制功能，支持白名单配置和 Token 校验扩展点。

**功能要求**:
- 支持白名单路径配置（白名单路径无需鉴权）
- 白名单支持路径匹配（支持通配符或正则表达式）
- 提供 Token 校验的扩展点（接口或抽象类）
- Token 校验占位实现默认放行（便于后续扩展）
- 白名单配置支持通过 Nacos Config 配置
- 白名单配置支持动态更新
- 非白名单且未通过 Token 校验的请求返回统一错误格式（错误码：013001 Token 无效）
- 鉴权控制包含完整的中文注释

**验收标准**:
- 白名单路径的请求能够正常通过 Gateway
- 非白名单路径的请求会触发 Token 校验（占位实现默认放行）
- 白名单配置可以通过 Nacos Config 配置和更新
- Token 校验扩展点可以后续扩展实现
- 鉴权控制包含完整的中文注释

### FR6: Nacos Config 配置管理

**需求描述**: Gateway 的配置（路由规则、白名单、限流规则、CORS 配置）通过 Nacos Config 管理，支持动态更新。

**功能要求**:
- Gateway 对接 Nacos Config
- 路由规则支持通过 Nacos Config 配置（DataId: `atlas-gateway-{profile}.yaml`）
- 白名单支持通过 Nacos Config 配置
- 限流规则支持通过 Nacos Config 配置（如果实现限流功能）
- CORS 配置支持通过 Nacos Config 配置
- 配置变更后 Gateway 能够动态生效（无需重启）
- 配置项遵循项目的配置命名规范（`atlas.gateway.*`）
- Nacos Config 配置包含完整的中文注释

**验收标准**:
- Gateway 能够从 Nacos Config 读取配置
- 路由规则可以通过 Nacos Config 配置和更新
- 白名单可以通过 Nacos Config 配置和更新
- CORS 配置可以通过 Nacos Config 配置和更新
- 配置变更后 Gateway 能够动态生效
- 配置项符合项目的配置命名规范
- Nacos Config 配置包含完整的中文注释

## 成功标准

### 功能完整性

- Gateway 能够成功转发请求到后端服务
- CORS 跨域请求能够正常处理
- TraceId 能够正确传递和追踪
- 错误响应使用统一的 `Result` 格式
- 白名单和 Token 校验功能正常工作
- Nacos Config 配置能够正常读取和更新

### 性能指标

- Gateway 转发延迟 < 50ms（P95）
- Gateway 支持至少 1000 QPS 的并发请求
- 配置更新后生效时间 < 5 秒

### 可维护性

- 所有代码包含完整的中文注释
- 配置项符合项目的配置命名规范
- 代码结构清晰，易于扩展和维护

### 验收测试

- Gateway 能够转发请求到临时的 health/mock 接口
- TraceId 能够在请求和响应中正确传递
- 统一错误码在错误响应中生效
- 白名单配置能够正常工作
- Nacos Config 配置能够正常读取和更新

## 数据模型

### Gateway 路由配置

**描述**: Gateway 路由规则配置，定义请求路径与后端服务的映射关系。

**配置结构**:
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
```

**配置项说明**:

| 配置项 | 类型 | 说明 | 必填 |
|--------|------|------|------|
| routes | List | 路由规则列表 | 是 |
| routes[].id | String | 路由 ID，唯一标识 | 是 |
| routes[].uri | String | 后端服务 URI | 是 |
| routes[].predicates | List | 路由断言（匹配条件） | 是 |
| routes[].filters | List | 路由过滤器（路径重写等） | 否 |

### Gateway 白名单配置

**描述**: Gateway 白名单配置，定义无需鉴权的路径列表。

**配置结构**:
```yaml
atlas:
  gateway:
    whitelist:
      enabled: true
      paths:
        - /health/**
        - /mock/**
        - /api/public/**
```

**配置项说明**:

| 配置项 | 类型 | 说明 | 必填 |
|--------|------|------|------|
| whitelist.enabled | Boolean | 是否启用白名单 | 是 |
| whitelist.paths | List<String> | 白名单路径列表（支持通配符） | 是 |

### Gateway CORS 配置

**描述**: Gateway CORS 跨域配置。

**配置结构**:
```yaml
atlas:
  gateway:
    cors:
      allowed-origins: "*"
      allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
      allowed-headers: "*"
      allow-credentials: true
      max-age: 3600
```

**配置项说明**:

| 配置项 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| cors.allowed-origins | String | 允许的源（多个用逗号分隔，* 表示所有） | 是 | "*" |
| cors.allowed-methods | String | 允许的 HTTP 方法（多个用逗号分隔） | 是 | "GET,POST,PUT,DELETE,OPTIONS" |
| cors.allowed-headers | String | 允许的请求头（多个用逗号分隔，* 表示所有） | 否 | "*" |
| cors.allow-credentials | Boolean | 是否允许携带凭证 | 否 | true |
| cors.max-age | Integer | 预检请求缓存时间（秒） | 否 | 3600 |

### Gateway 限流配置（可选）

**描述**: Gateway 限流规则配置（如果实现限流功能）。

**配置结构**:
```yaml
atlas:
  gateway:
    rate-limit:
      enabled: true
      default-qps: 1000
      rules:
        - path: /api/**
          qps: 500
        - path: /mock/**
          qps: 100
```

**配置项说明**:

| 配置项 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| rate-limit.enabled | Boolean | 是否启用限流 | 是 | false |
| rate-limit.default-qps | Integer | 默认 QPS 限制 | 否 | 1000 |
| rate-limit.rules | List | 限流规则列表 | 否 | [] |
| rate-limit.rules[].path | String | 路径匹配（支持通配符） | 是 | - |
| rate-limit.rules[].qps | Integer | 该路径的 QPS 限制 | 是 | - |

## 业务逻辑

### 请求处理流程

1. **请求到达**: 客户端请求到达 Gateway
2. **TraceId 处理**: Gateway 从请求头获取或生成 TraceId，设置到请求上下文
3. **CORS 处理**: 如果是预检请求（OPTIONS），返回 CORS 响应头
4. **白名单检查**: 检查请求路径是否在白名单中
5. **Token 校验**: 如果不在白名单，执行 Token 校验（占位实现默认放行）
6. **路由匹配**: 根据路由规则匹配对应的后端服务
7. **请求转发**: 将请求转发到后端服务，携带 TraceId
8. **响应处理**: 接收后端服务响应，添加 TraceId 到响应头
9. **错误处理**: 如果转发失败或服务不可用，返回统一错误格式

### TraceId 处理流程

1. **获取 TraceId**: 从请求头 `X-Trace-Id` 获取 TraceId
2. **生成 TraceId**: 如果请求头中没有 TraceId，调用 `TraceIdUtil.generate()` 生成
3. **设置 TraceId**: 调用 `TraceIdUtil.setTraceId()` 设置 TraceId（设置到 ThreadLocal 和 MDC）
4. **传递 TraceId**: 将 TraceId 添加到转发请求的请求头
5. **返回 TraceId**: 将 TraceId 添加到响应头
6. **清理 TraceId**: 请求结束后清理 TraceId（避免内存泄漏）

### 错误处理流程

1. **异常捕获**: Gateway 捕获路由失败、服务不可用等异常
2. **错误分类**: 根据异常类型确定错误码和错误消息
3. **构建响应**: 使用 `Result.error()` 方法构建错误响应
4. **TraceId 注入**: 从请求上下文获取 TraceId 并注入到响应中
5. **返回响应**: 返回统一的错误响应格式

### 白名单和 Token 校验流程

1. **路径匹配**: 检查请求路径是否匹配白名单规则
2. **白名单判断**: 如果匹配白名单，直接放行
3. **Token 校验**: 如果不匹配白名单，执行 Token 校验
4. **占位实现**: Token 校验占位实现默认放行（便于后续扩展）
5. **错误返回**: 如果 Token 校验失败（未来扩展），返回统一错误格式（错误码：013001）

### Nacos Config 配置更新流程

1. **配置监听**: Gateway 监听 Nacos Config 配置变更
2. **配置解析**: 解析配置变更内容（路由规则、白名单、CORS 等）
3. **配置应用**: 将新配置应用到 Gateway
4. **动态生效**: 配置变更后立即生效，无需重启服务

## 异常处理

### 异常类型

1. **路由失败**: 请求路径不匹配任何路由规则（错误码：010404）
2. **服务不可用**: 后端服务不可用或连接失败（错误码：010503）
3. **请求超时**: 转发请求超时（错误码：010002）
4. **Token 无效**: Token 校验失败（错误码：013001，未来扩展）
5. **配置错误**: Nacos Config 配置格式错误或读取失败（错误码：010000）

### 错误响应格式

所有错误响应使用统一的 `Result` 格式：

```json
{
  "code": "010404",
  "message": "路由不存在",
  "data": null,
  "timestamp": 1706342400000,
  "traceId": "abc123def456"
}
```

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

## 测试要求

### 单元测试

- Gateway 路由配置测试
- TraceId 处理测试
- CORS 配置测试
- 白名单匹配测试
- Token 校验扩展点测试
- 错误处理测试
- Nacos Config 配置读取测试

### 集成测试

- Gateway 路由转发集成测试
- TraceId 传递集成测试
- CORS 跨域集成测试
- 白名单功能集成测试
- 统一错误返回集成测试
- Nacos Config 配置更新集成测试

### 验收测试

- Gateway 能够转发请求到临时的 health/mock 接口
- TraceId 能够在请求和响应中正确传递
- 统一错误码在错误响应中生效
- 白名单配置能够正常工作
- Nacos Config 配置能够正常读取和更新

## 实现注意事项

- [ ] 复用 `atlas-common-infra-logging` 模块的 `TraceIdUtil` 工具类
- [ ] 复用 `atlas-common-feature-core` 模块的 `Result` 类和 `CommonErrorCode` 常量
- [ ] 确保所有类和方法添加中文注释
- [ ] 遵循项目的配置命名规范（`atlas.gateway.*`）
- [ ] Gateway 错误响应使用统一的 `Result` 格式
- [ ] Token 校验提供扩展点，便于后续实现
- [ ] Nacos Config 配置遵循项目的配置命名规范
- [ ] 提供临时的 health/mock 接口用于验收测试

## 假设

1. **Spring Cloud Gateway**: 使用 Spring Cloud Gateway 作为 API 网关框架
2. **Nacos Config**: 使用 Nacos Config 作为配置中心
3. **TraceId 格式**: TraceId 使用 UUID 或 Snowflake 算法生成（复用 `atlas-common-infra-logging` 模块的实现）
4. **错误码规范**: Gateway 模块使用错误码段 01（atlas-gateway），错误码格式：01TTSS（6位数字）
5. **配置格式**: Nacos Config 配置使用 YAML 格式
6. **Token 校验**: Token 校验占位实现默认放行，后续可以扩展实现具体的校验逻辑
7. **限流功能**: 限流功能为可选功能，如果实现，使用 Sentinel 或其他限流组件

## 验收标准

### 基本功能验收

1. **路由转发**: Gateway 能够根据路由规则转发请求到后端服务
2. **CORS 支持**: Gateway 能够正确处理跨域请求
3. **TraceId 追踪**: TraceId 能够在请求和响应中正确传递
4. **统一错误返回**: Gateway 的错误响应使用统一的 `Result` 格式
5. **白名单功能**: 白名单路径的请求能够正常通过 Gateway
6. **Nacos Config 配置**: Gateway 能够从 Nacos Config 读取配置并动态更新

### 验收测试场景

1. **健康检查接口**: Gateway 能够转发请求到临时的 `/health` 或 `/mock/health` 接口
2. **TraceId 传递**: 请求经过 Gateway 后，TraceId 能够在请求头和响应头中看到
3. **统一错误码**: Gateway 返回的错误响应包含统一的错误码格式（6位数字）
4. **白名单测试**: 白名单路径的请求能够正常通过，非白名单路径会触发 Token 校验（占位实现默认放行）
5. **配置更新测试**: 修改 Nacos Config 中的路由规则或白名单配置，Gateway 能够动态生效
