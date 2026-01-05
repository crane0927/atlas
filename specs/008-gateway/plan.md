# 功能规划文档

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0
- ✅ **API 设计**: Gateway 转发遵循 RESTful 风格
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 复用 `atlas-common-infra-logging` 的 TraceId 工具类和 `atlas-common-feature-core` 的统一响应格式
- ✅ **模块化**: Gateway 作为独立模块，符合模块化设计原则
- ✅ **包名规范**: 遵循 `com.atlas.gateway` 包名结构
- ✅ **配置文件格式**: 使用 YAML 格式
- ✅ **组件优先使用 Spring Cloud 生态**: 使用 Spring Cloud Gateway 作为 API 网关（符合原则 5）

**宪法合规性评估**:
- ✅ 所有技术栈版本符合宪法要求
- ✅ Gateway 模块使用 Spring Cloud Gateway（Spring Cloud 官方组件），符合原则 5
- ✅ 使用 Nacos Config 进行配置管理（Spring Cloud Alibaba 组件），符合原则 5
- ✅ 复用 `atlas-common-infra-logging` 模块的 `TraceIdUtil` 工具类，符合代码复用原则
- ✅ 复用 `atlas-common-feature-core` 模块的 `Result` 类和 `CommonErrorCode` 常量，符合代码复用原则
- ✅ 所有代码将使用中文注释
- ✅ 遵循包名规范
- ✅ Gateway 错误响应使用统一的 `Result` 格式

## 功能概述

创建 `atlas-gateway` 模块，实现 API 网关功能，作为微服务架构的统一入口。Gateway 负责请求路由、跨域处理、链路追踪、错误处理和鉴权控制。该模块确保：

1. **统一性**: 所有请求通过统一的网关入口，路由规则统一管理
2. **可追溯性**: 所有请求都有 TraceId，支持完整的链路追踪
3. **规范性**: 所有错误响应使用统一的 `Result` 格式
4. **安全性**: 支持白名单和 Token 校验（占位实现，便于后续扩展）
5. **可配置性**: 路由规则、白名单、CORS 配置通过 Nacos Config 管理，支持动态更新
6. **性能**: Gateway 转发延迟 < 50ms（P95），支持至少 1000 QPS

## 技术方案

### 架构设计

**模块结构**:
```
atlas/
└── atlas-gateway/              # Gateway 模块
    ├── pom.xml                    # 模块 POM
    ├── README.md                  # 模块文档
    ├── src/
    │   ├── main/java/
    │   │   └── com/atlas/gateway/
    │   │       ├── config/        # Gateway 配置
    │   │       │   ├── GatewayConfig.java
    │   │       │   ├── GatewayProperties.java
    │   │       │   └── CorsConfig.java
    │   │       ├── filter/        # Gateway Filter
    │   │       │   ├── TraceIdGatewayFilter.java
    │   │       │   └── AuthGatewayFilter.java
    │   │       ├── exception/     # 异常处理
    │   │       │   └── GatewayExceptionHandler.java
    │   │       └── GatewayApplication.java
    │   └── resources/
    │       └── application.yml    # 应用配置
    └── src/test/java/             # 测试代码
        └── com/atlas/gateway/
            ├── config/
            ├── filter/
            └── exception/
```

**核心组件**:

1. **Gateway 配置**: `GatewayConfig`
   - 配置 Spring Cloud Gateway 路由规则
   - 配置全局过滤器
   - 支持从 Nacos Config 读取路由配置

2. **路由配置属性**: `GatewayProperties`
   - 使用 `@ConfigurationProperties` 绑定配置
   - 支持路由规则、白名单、CORS 配置
   - 配置项遵循 `atlas.gateway.*` 命名规范

3. **CORS 配置**: `CorsConfig`
   - 配置 Gateway 的 CORS 跨域支持
   - 支持动态配置更新

4. **TraceId Filter**: `TraceIdGatewayFilter`
   - 实现 `GlobalFilter` 接口
   - 从请求头获取或生成 TraceId
   - 设置 TraceId 到 `TraceIdUtil`（复用 `atlas-common-infra-logging` 模块）
   - 将 TraceId 添加到响应头

5. **鉴权 Filter**: `AuthGatewayFilter`
   - 实现 `GlobalFilter` 接口
   - 检查请求路径是否在白名单中
   - 提供 Token 校验扩展点（占位实现，默认放行）

6. **异常处理器**: `GatewayExceptionHandler`
   - 处理 Gateway 异常（路由失败、服务不可用等）
   - 统一返回 `Result` 格式响应
   - 自动注入 TraceId

### 技术选型

**构建工具**: Maven 3.8+
- **理由**: 项目使用 Maven，继承父 POM 配置

**核心依赖**:
- **spring-cloud-starter-gateway**: Spring Cloud Gateway 核心依赖（Spring Cloud 2025.1.0）
- **spring-cloud-starter-alibaba-nacos-config**: Nacos Config 配置管理（Spring Cloud Alibaba 2025.1.0）
- **spring-cloud-starter-alibaba-nacos-discovery**: Nacos 服务发现（可选，用于服务发现）
- **atlas-common-feature-core**: 依赖 `Result` 类和 `CommonErrorCode` 常量
- **atlas-common-infra-logging**: 依赖 `TraceIdUtil` 工具类

**技术决策**:

1. **API 网关**: 使用 Spring Cloud Gateway
   - **理由**: Spring Cloud 官方组件，与 Spring Boot 深度集成，符合项目宪法原则 5
   - **版本**: Spring Cloud 2025.1.0（与 Spring Boot 4.0.1 兼容）

2. **配置管理**: 使用 Nacos Config
   - **理由**: Spring Cloud Alibaba 官方组件，支持动态配置更新，符合项目宪法原则 5
   - **配置格式**: YAML 格式
   - **DataId**: `atlas-gateway-{profile}.yaml`

3. **路由配置**: 支持通过 Nacos Config 和代码配置
   - **理由**: Nacos Config 支持动态更新，代码配置支持静态路由
   - **实现方式**: 使用 `RouteDefinitionLocator` 和 `RouteDefinitionWriter` 动态管理路由

4. **TraceId 处理**: 复用 `atlas-common-infra-logging` 模块的 `TraceIdUtil`
   - **理由**: 避免重复实现，保持 TraceId 格式一致
   - **实现方式**: 在 `GlobalFilter` 中调用 `TraceIdUtil` 方法

5. **错误处理**: 使用 `@ControllerAdvice` 或 `ErrorWebExceptionHandler`
   - **理由**: Gateway 使用 WebFlux，需要使用响应式异常处理
   - **实现方式**: 实现 `ErrorWebExceptionHandler` 接口

6. **CORS 配置**: 使用 Spring Cloud Gateway 的 CORS 配置
   - **理由**: Gateway 内置 CORS 支持，配置简单
   - **实现方式**: 通过 `CorsConfig` 配置类配置

7. **白名单和 Token 校验**: 使用 `GlobalFilter` 实现
   - **理由**: Filter 在路由匹配之前执行，可以控制请求是否继续
   - **Token 校验**: 提供接口扩展点，占位实现默认放行

## 实施计划

### 阶段 1: 项目初始化和基础配置

**目标**: 创建 Gateway 模块结构，配置依赖，搭建基础框架

**任务**:
1. 创建 `atlas-gateway` 模块目录结构
2. 创建 `pom.xml`，配置依赖（spring-cloud-starter-gateway、spring-cloud-starter-alibaba-nacos-config、atlas-common-feature-core、atlas-common-infra-logging）
3. 创建包结构（config、filter、exception）
4. 创建 `GatewayApplication` 主类
5. 创建 `application.yml` 配置文件
6. 创建 `README.md` 文档

**验收标准**:
- 模块结构创建完成
- 依赖配置正确
- 包结构符合规范
- Gateway 应用可以启动

### 阶段 2: Gateway 路由配置实现

**目标**: 实现 Gateway 路由转发功能，支持通过 Nacos Config 配置路由规则

**任务**:
1. 创建 `GatewayProperties` 配置属性类
2. 创建 `GatewayConfig` 配置类，配置路由规则
3. 实现从 Nacos Config 读取路由配置
4. 实现路由动态更新功能
5. 创建临时的 health/mock 接口用于验收测试
6. 编写单元测试

**验收标准**:
- Gateway 能够根据路由规则转发请求
- 路由规则可以通过 Nacos Config 配置和更新
- Gateway 能够转发请求到临时的 health/mock 接口

### 阶段 3: CORS 跨域支持实现

**目标**: 实现 Gateway CORS 跨域支持，支持动态配置

**任务**:
1. 创建 `CorsConfig` 配置类
2. 配置 CORS 允许的源、方法、请求头等
3. 支持通过 Nacos Config 配置 CORS
4. 实现 CORS 配置动态更新
5. 编写单元测试和集成测试

**验收标准**:
- 跨域请求能够正常处理
- 预检请求（OPTIONS）能够正确处理
- CORS 配置可以通过 Nacos Config 配置和更新

### 阶段 4: TraceId 链路追踪实现

**目标**: 实现 TraceId 自动处理，确保所有请求都有 TraceId

**任务**:
1. 创建 `TraceIdGatewayFilter` 类
2. 实现从请求头获取或生成 TraceId
3. 集成 `TraceIdUtil` 工具类
4. 将 TraceId 添加到响应头
5. 编写单元测试和集成测试

**验收标准**:
- 所有经过 Gateway 的请求都有 TraceId
- TraceId 能够正确传递到后端服务
- TraceId 能够在响应中返回
- TraceId 与日志系统集成

### 阶段 5: 统一错误返回实现

**目标**: 实现 Gateway 统一错误处理，所有错误响应使用 `Result` 格式

**任务**:
1. 创建 `GatewayExceptionHandler` 类
2. 实现路由失败异常处理（404）
3. 实现服务不可用异常处理（503）
4. 实现请求超时异常处理
5. 实现其他 Gateway 异常处理
6. 错误响应自动注入 TraceId
7. 编写单元测试和集成测试

**验收标准**:
- 所有 Gateway 错误响应使用统一的 `Result` 格式
- 错误响应包含错误码、错误消息、TraceId
- 错误码符合项目错误码规范（01 开头）

### 阶段 6: 鉴权控制实现（白名单 + Token 校验占位）

**目标**: 实现 Gateway 鉴权控制，支持白名单和 Token 校验扩展点

**任务**:
1. 创建 `AuthGatewayFilter` 类
2. 实现白名单路径匹配功能
3. 实现 Token 校验扩展点（接口或抽象类）
4. Token 校验占位实现（默认放行）
5. 支持通过 Nacos Config 配置白名单
6. 实现白名单配置动态更新
7. 编写单元测试和集成测试

**验收标准**:
- 白名单路径的请求能够正常通过 Gateway
- 非白名单路径的请求会触发 Token 校验（占位实现默认放行）
- 白名单配置可以通过 Nacos Config 配置和更新
- Token 校验扩展点可以后续扩展实现

### 阶段 7: Nacos Config 配置管理完善

**目标**: 完善 Nacos Config 配置管理，确保所有配置支持动态更新

**任务**:
1. 完善 `GatewayProperties` 配置属性类
2. 实现配置变更监听和动态更新
3. 验证路由规则动态更新功能
4. 验证白名单动态更新功能
5. 验证 CORS 配置动态更新功能
6. 编写集成测试

**验收标准**:
- Gateway 能够从 Nacos Config 读取所有配置
- 配置变更后 Gateway 能够动态生效（无需重启）
- 配置项符合项目的配置命名规范（`atlas.gateway.*`）

### 阶段 8: 文档和测试完善

**目标**: 完善文档和测试，确保代码质量和可维护性

**任务**:
1. 完善 `README.md` 文档
2. 编写使用示例和最佳实践
3. 完善单元测试覆盖率（≥ 70%）
4. 完善集成测试
5. 编写验收测试文档

**验收标准**:
- 文档完整，包含使用示例和最佳实践
- 单元测试覆盖率 ≥ 70%
- 集成测试覆盖主要业务流程
- 验收测试文档完整

## 风险评估

### 风险 1: Spring Cloud Gateway 与 Spring Boot 4.0.1 兼容性

**风险描述**: Spring Cloud Gateway 2025.1.0 可能与 Spring Boot 4.0.1 存在兼容性问题

**影响**: 高

**应对措施**:
- 在实施前验证 Spring Cloud Gateway 2025.1.0 与 Spring Boot 4.0.1 的兼容性
- 如果存在兼容性问题，考虑使用兼容的版本或等待官方更新
- 参考 Spring Cloud 官方文档和社区反馈

### 风险 2: Nacos Config 动态配置更新实现复杂度

**风险描述**: Gateway 路由规则动态更新需要实现 `RouteDefinitionLocator` 和 `RouteDefinitionWriter`，实现复杂度较高

**影响**: 中

**应对措施**:
- 参考 Spring Cloud Gateway 官方文档和示例代码
- 先实现静态路由配置，再逐步实现动态配置更新
- 如果实现复杂度过高，可以考虑使用 Nacos Config 的配置刷新机制

### 风险 3: Gateway 错误处理与 WebFlux 响应式编程

**风险描述**: Gateway 使用 WebFlux，错误处理需要使用响应式编程方式，与传统的 Spring MVC 异常处理不同

**影响**: 中

**应对措施**:
- 学习 WebFlux 响应式编程模型
- 参考 Spring Cloud Gateway 官方文档中的错误处理示例
- 实现 `ErrorWebExceptionHandler` 接口处理异常

### 风险 4: TraceId 在 Gateway 中的传递和清理

**风险描述**: Gateway 使用 WebFlux，TraceId 的传递和清理方式可能与传统的 Servlet 不同

**影响**: 低

**应对措施**:
- Gateway Filter 中使用 `ServerWebExchange` 传递 TraceId
- 在 Filter 的 `filter()` 方法中设置和清理 TraceId
- 参考 `atlas-common-infra-logging` 模块的实现方式

### 风险 5: 性能问题

**风险描述**: Gateway 作为统一入口，可能成为性能瓶颈

**影响**: 中

**应对措施**:
- 优化 Filter 执行顺序，减少不必要的 Filter 执行
- 使用异步处理提高性能
- 进行性能测试，确保满足性能指标（转发延迟 < 50ms，支持 ≥ 1000 QPS）

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
3. **统一错误码**: Gateway 返回的错误响应包含统一的错误码格式（6位数字，01 开头）
4. **白名单测试**: 白名单路径的请求能够正常通过，非白名单路径会触发 Token 校验（占位实现默认放行）
5. **配置更新测试**: 修改 Nacos Config 中的路由规则或白名单配置，Gateway 能够动态生效

### 性能验收

1. **转发延迟**: Gateway 转发延迟 < 50ms（P95）
2. **并发支持**: Gateway 支持至少 1000 QPS 的并发请求
3. **配置更新**: 配置更新后生效时间 < 5 秒

### 代码质量验收

1. **代码注释**: 所有代码包含完整的中文注释
2. **测试覆盖率**: 单元测试覆盖率 ≥ 70%
3. **代码规范**: 代码符合项目规范，通过 Spotless 检查
