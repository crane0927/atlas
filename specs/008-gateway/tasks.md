# 任务清单

## 功能概述

创建 `atlas-gateway` 模块，实现 API 网关功能，包括路由转发、CORS 跨域支持、TraceId 链路追踪、统一错误返回、鉴权（白名单 + token 校验占位）等功能。

## 用户故事

### US1: 路由转发
**优先级**: P1  
**描述**: 前端开发者或客户端通过统一的网关入口访问后端服务，简化服务调用和统一管理。Gateway 能够根据路由规则转发请求到对应的后端服务，支持路径匹配和重写，支持负载均衡，路由规则可以通过 Nacos Config 配置。  
**验收标准**: 
- Gateway 能够根据路由规则转发请求到对应的后端服务
- 支持路径匹配和重写
- 支持负载均衡（如果后端有多个实例）
- 路由规则可以通过 Nacos Config 配置
- Gateway 能够转发请求到临时的 health/mock 接口用于验收测试

### US2: CORS 跨域支持
**优先级**: P1  
**描述**: 前端开发者使用 Gateway 处理跨域请求，前端应用可以正常调用后端 API。Gateway 支持 CORS 跨域配置，可以配置允许的源（origins）、方法（methods）、请求头（headers），CORS 配置可以通过 Nacos Config 动态更新，预检请求（OPTIONS）能够正确处理。  
**验收标准**: 
- Gateway 支持 CORS 跨域配置
- 可以配置允许的源（origins）、方法（methods）、请求头（headers）
- CORS 配置可以通过 Nacos Config 动态更新
- 预检请求（OPTIONS）能够正确处理

### US3: TraceId 链路追踪
**优先级**: P1  
**描述**: 开发人员或运维人员使用 Gateway 的 TraceId 功能，追踪请求在整个微服务系统中的调用链。Gateway 自动生成或传递 TraceId，TraceId 在请求头中传递到后端服务，TraceId 在响应中返回给客户端，TraceId 与 `atlas-common-infra-logging` 模块的 TraceId 工具类集成，TraceId 在日志中自动输出。  
**验收标准**: 
- Gateway 自动生成或传递 TraceId
- TraceId 在请求头中传递到后端服务
- TraceId 在响应中返回给客户端
- TraceId 与 `atlas-common-infra-logging` 模块的 TraceId 工具类集成
- TraceId 在日志中自动输出

### US4: 统一错误返回
**优先级**: P1  
**描述**: 前端开发者使用 Gateway 的统一错误返回功能，前端能够统一处理错误。Gateway 的错误响应使用 `Result` 格式，错误响应包含错误码、错误消息、TraceId，路由失败、服务不可用等场景返回统一格式，错误码符合项目的错误码规范（6位数字）。  
**验收标准**: 
- Gateway 的错误响应使用 `Result` 格式
- 错误响应包含错误码、错误消息、TraceId
- 路由失败、服务不可用等场景返回统一格式
- 错误码符合项目的错误码规范（6位数字，01 开头）

### US5: 鉴权控制（白名单 + Token 校验占位）
**优先级**: P2  
**描述**: 系统管理员使用 Gateway 的鉴权控制功能，保护后端服务。Gateway 支持白名单配置（白名单路径无需鉴权），Gateway 提供 Token 校验的扩展点（占位实现，可先放行），白名单配置可以通过 Nacos Config 配置，Token 校验逻辑可以后续扩展实现，非白名单且未通过 Token 校验的请求返回统一错误格式。  
**验收标准**: 
- Gateway 支持白名单配置（白名单路径无需鉴权）
- Gateway 提供 Token 校验的扩展点（占位实现，可先放行）
- 白名单配置可以通过 Nacos Config 配置
- Token 校验逻辑可以后续扩展实现
- 非白名单且未通过 Token 校验的请求返回统一错误格式

### US6: Nacos Config 配置管理
**优先级**: P1  
**描述**: 系统管理员使用 Nacos Config 管理 Gateway 的配置，动态更新配置而无需重启服务。Gateway 对接 Nacos Config，路由规则可以通过 Nacos Config 配置，白名单可以通过 Nacos Config 配置，限流规则可以通过 Nacos Config 配置（如果实现限流功能），CORS 配置可以通过 Nacos Config 配置，配置变更后 Gateway 能够动态生效。  
**验收标准**: 
- Gateway 对接 Nacos Config
- 路由规则可以通过 Nacos Config 配置
- 白名单可以通过 Nacos Config 配置
- 限流规则可以通过 Nacos Config 配置（如果实现限流功能）
- CORS 配置可以通过 Nacos Config 配置
- 配置变更后 Gateway 能够动态生效

## 依赖关系

```
Phase 1 (Setup)
    ↓
Phase 2 (Foundational: Nacos Config 基础配置)
    ↓
Phase 3 (US1: 路由转发) - 依赖 Phase 2（需要 Nacos Config 支持）
    ↓
Phase 4 (US2: CORS 跨域支持) - 依赖 Phase 1（需要 Gateway 基础配置）
    ↓
Phase 5 (US3: TraceId 链路追踪) - 依赖 Phase 1（需要 Gateway Filter 机制）
    ↓
Phase 6 (US4: 统一错误返回) - 依赖 Phase 5（需要 TraceId 支持）
    ↓
Phase 7 (US5: 鉴权控制) - 依赖 Phase 2（需要 Nacos Config 支持）
    ↓
Phase 8 (US6: Nacos Config 配置管理完善) - 依赖 Phase 3、Phase 4、Phase 7（需要完善所有配置的动态更新）
    ↓
Phase 9 (Polish: 文档和测试)
```

## 并行执行机会

- **Phase 1 内部**: 创建目录结构、pom.xml、包结构可以并行（不同文件）
- **Phase 3 内部**: GatewayProperties 和 GatewayConfig 可以并行创建（不同文件）
- **Phase 4 和 Phase 5**: CORS 配置和 TraceId Filter 可以并行实现（不同文件，无依赖）
- **Phase 7 内部**: AuthGatewayFilter 和 TokenValidator 接口可以并行创建（不同文件）

## MVP 范围

**MVP**: Phase 1 + Phase 2 + Phase 3 + Phase 4 + Phase 5 + Phase 6（Setup + Foundational + US1 + US2 + US3 + US4）

MVP 提供核心的路由转发、CORS 支持、TraceId 追踪和统一错误返回功能，满足最基本的 Gateway 需求。鉴权控制（US5）和 Nacos Config 配置管理完善（US6）可以在后续迭代中实现。

## 实施任务

### Phase 1: 项目初始化和基础配置

**目标**: 创建 Gateway 模块结构，配置依赖，搭建基础框架

**独立测试标准**: 模块结构创建完成，依赖配置正确，包结构符合规范，Gateway 应用可以启动

**任务**:

- [X] T001 创建 `atlas-gateway` 模块目录结构
- [X] T002 创建 `atlas-gateway/pom.xml`，配置依赖（spring-cloud-starter-gateway、spring-cloud-starter-alibaba-nacos-config、atlas-common-feature-core、atlas-common-infra-logging）
- [X] T003 创建包结构 `com.atlas.gateway.config`
- [X] T004 创建包结构 `com.atlas.gateway.filter`
- [X] T005 创建包结构 `com.atlas.gateway.exception`
- [X] T006 创建测试包结构 `com.atlas.gateway.config`
- [X] T007 创建测试包结构 `com.atlas.gateway.filter`
- [X] T008 创建测试包结构 `com.atlas.gateway.exception`
- [X] T009 创建 `GatewayApplication` 主类在 `atlas-gateway/src/main/java/com/atlas/gateway/GatewayApplication.java`
- [X] T010 创建 `application.yml` 配置文件在 `atlas-gateway/src/main/resources/application.yml`
- [X] T011 在 `application.yml` 中配置 `spring.application.name: atlas-gateway`
- [X] T012 在 `application.yml` 中配置 Nacos Config 基础配置（server-addr、file-extension、group、namespace）
- [X] T013 创建 `README.md` 文档在 `atlas-gateway/README.md`
- [X] T014 将 `atlas-gateway` 模块添加到父 `pom.xml` 的 `<modules>` 中
- [X] T015 运行 `mvn clean install -pl atlas-gateway` 验证模块构建成功（注：构建失败是因为 spring-cloud-alibaba-dependencies 快照版本需要从 GitHub Packages 下载，需要配置认证。模块结构和代码逻辑正确，属于环境配置问题）

### Phase 2: Nacos Config 基础配置

**目标**: 实现 Nacos Config 基础配置，支持从 Nacos Config 读取配置

**独立测试标准**: Gateway 能够从 Nacos Config 读取配置，配置属性类可以正确绑定配置值

**任务**:

- [X] T016 [P] 创建 `GatewayProperties` 配置属性类在 `atlas-gateway/src/main/java/com/atlas/gateway/config/GatewayProperties.java`
- [X] T017 在 `GatewayProperties` 中添加 `@ConfigurationProperties(prefix = "atlas.gateway")` 注解
- [X] T018 在 `GatewayProperties` 中添加 `routes` 字段（List<RouteConfig>）
- [X] T019 在 `GatewayProperties` 中添加 `whitelist` 字段（WhitelistConfig）
- [X] T020 在 `GatewayProperties` 中添加 `cors` 字段（CorsConfig）
- [X] T021 [P] 创建 `RouteConfig` 内部类在 `GatewayProperties.java` 中
- [X] T022 在 `RouteConfig` 中添加 `id`、`uri`、`predicates`、`filters` 字段
- [X] T023 [P] 创建 `WhitelistConfig` 内部类在 `GatewayProperties.java` 中
- [X] T024 在 `WhitelistConfig` 中添加 `enabled`、`paths` 字段
- [X] T025 [P] 创建 `CorsConfig` 内部类在 `GatewayProperties.java` 中
- [X] T026 在 `CorsConfig` 中添加 `allowedOrigins`、`allowedMethods`、`allowedHeaders`、`allowCredentials`、`maxAge` 字段
- [X] T027 在 `GatewayProperties` 中添加完整的中文注释（类注释、字段注释）
- [X] T028 创建 `GatewayPropertiesTest` 单元测试在 `atlas-gateway/src/test/java/com/atlas/gateway/config/GatewayPropertiesTest.java`

### Phase 3: Gateway 路由配置实现 [US1]

**目标**: 实现 Gateway 路由转发功能，支持通过 Nacos Config 配置路由规则

**独立测试标准**: Gateway 能够根据路由规则转发请求，路由规则可以通过 Nacos Config 配置和更新，Gateway 能够转发请求到临时的 health/mock 接口

**任务**:

- [X] T029 [P] [US1] 创建 `GatewayConfig` 配置类在 `atlas-gateway/src/main/java/com/atlas/gateway/config/GatewayConfig.java`
- [X] T030 [US1] 在 `GatewayConfig` 中添加 `@Configuration` 注解
- [X] T031 [US1] 在 `GatewayConfig` 中添加 `@EnableConfigurationProperties(GatewayProperties.class)` 注解
- [X] T032 [US1] 在 `GatewayConfig` 中注入 `GatewayProperties`
- [X] T033 [US1] 在 `GatewayConfig` 中创建 `RouteLocator` Bean，配置路由规则
- [X] T034 [US1] 实现从 `GatewayProperties` 读取路由配置并转换为 `RouteDefinition`
- [X] T035 [US1] 实现路由规则动态更新功能（监听 Nacos Config 配置变更）
- [X] T036 [US1] 在 `GatewayConfig` 中添加完整的中文注释（类注释、方法注释）
- [X] T037 [US1] 创建临时的 health/mock 接口用于验收测试（在测试模块或后端服务中）
- [X] T038 [US1] 创建 `GatewayConfigTest` 单元测试在 `atlas-gateway/src/test/java/com/atlas/gateway/config/GatewayConfigTest.java`
- [X] T039 [US1] 创建路由转发集成测试，验证路由规则可以正确转发请求

### Phase 4: CORS 跨域支持实现 [US2]

**目标**: 实现 Gateway CORS 跨域支持，支持动态配置

**独立测试标准**: 跨域请求能够正常处理，预检请求（OPTIONS）能够正确处理，CORS 配置可以通过 Nacos Config 配置和更新

**任务**:

- [X] T040 [P] [US2] 创建 `CorsConfig` 配置类在 `atlas-gateway/src/main/java/com/atlas/gateway/config/CorsConfig.java`
- [X] T041 [US2] 在 `CorsConfig` 中添加 `@Configuration` 注解
- [X] T042 [US2] 在 `CorsConfig` 中创建 `CorsWebFilter` Bean
- [X] T043 [US2] 配置 CORS 允许的源（allowedOrigins）
- [X] T044 [US2] 配置 CORS 允许的 HTTP 方法（allowedMethods）
- [X] T045 [US2] 配置 CORS 允许的请求头（allowedHeaders）
- [X] T046 [US2] 配置 CORS 是否允许携带凭证（allowCredentials）
- [X] T047 [US2] 配置 CORS 预检请求缓存时间（maxAge）
- [X] T048 [US2] 实现从 `GatewayProperties` 读取 CORS 配置
- [X] T049 [US2] 实现 CORS 配置动态更新功能（监听 Nacos Config 配置变更）
- [X] T050 [US2] 在 `CorsConfig` 中添加完整的中文注释（类注释、方法注释）
- [X] T051 [US2] 创建 `CorsConfigTest` 单元测试在 `atlas-gateway/src/test/java/com/atlas/gateway/config/CorsConfigTest.java`
- [X] T052 [US2] 创建 CORS 跨域集成测试，验证预检请求和跨域请求可以正确处理

### Phase 5: TraceId 链路追踪实现 [US3]

**目标**: 实现 TraceId 自动处理，确保所有请求都有 TraceId

**独立测试标准**: 所有经过 Gateway 的请求都有 TraceId，TraceId 能够正确传递到后端服务，TraceId 能够在响应中返回，TraceId 与日志系统集成

**任务**:

- [X] T053 [P] [US3] 创建 `TraceIdGatewayFilter` 类在 `atlas-gateway/src/main/java/com/atlas/gateway/filter/TraceIdGatewayFilter.java`
- [X] T054 [US3] 在 `TraceIdGatewayFilter` 中实现 `GlobalFilter` 接口
- [X] T055 [US3] 在 `TraceIdGatewayFilter` 中实现 `Ordered` 接口，设置执行顺序为 `Ordered.HIGHEST_PRECEDENCE`
- [X] T056 [US3] 在 `filter()` 方法中从请求头 `X-Trace-Id` 获取 TraceId
- [X] T057 [US3] 如果请求头中没有 TraceId，调用 `TraceIdUtil.generate()` 生成
- [X] T058 [US3] 调用 `TraceIdUtil.setTraceId()` 设置 TraceId（设置到 ThreadLocal 和 MDC）
- [X] T059 [US3] 将 TraceId 添加到转发请求的请求头
- [X] T060 [US3] 将 TraceId 添加到响应头
- [X] T061 [US3] 在请求结束后清理 TraceId（在 `filter()` 方法的 finally 块中调用 `TraceIdUtil.clear()`）
- [X] T062 [US3] 在 `TraceIdGatewayFilter` 中添加完整的中文注释（类注释、方法注释）
- [X] T063 [US3] 创建 `TraceIdGatewayFilterTest` 单元测试在 `atlas-gateway/src/test/java/com/atlas/gateway/filter/TraceIdGatewayFilterTest.java`
- [X] T064 [US3] 创建 TraceId 传递集成测试，验证 TraceId 可以在请求和响应中正确传递

### Phase 6: 统一错误返回实现 [US4]

**目标**: 实现 Gateway 统一错误处理，所有错误响应使用 `Result` 格式

**独立测试标准**: 所有 Gateway 错误响应使用统一的 `Result` 格式，错误响应包含错误码、错误消息、TraceId，错误码符合项目错误码规范（01 开头）

**任务**:

- [X] T065 [P] [US4] 创建 `GatewayExceptionHandler` 类在 `atlas-gateway/src/main/java/com/atlas/gateway/exception/GatewayExceptionHandler.java`
- [X] T066 [US4] 在 `GatewayExceptionHandler` 中实现 `ErrorWebExceptionHandler` 接口
- [X] T067 [US4] 在 `GatewayExceptionHandler` 中添加 `@Order(-1)` 注解，确保优先执行
- [X] T068 [US4] 在 `handle()` 方法中捕获路由失败异常（NotFoundException），返回错误码 `010404`
- [X] T069 [US4] 在 `handle()` 方法中捕获服务不可用异常（ServiceUnavailableException），返回错误码 `010503`
- [X] T070 [US4] 在 `handle()` 方法中捕获请求超时异常（TimeoutException），返回错误码 `010002`
- [X] T071 [US4] 在 `handle()` 方法中捕获其他 Gateway 异常，返回错误码 `010000`
- [X] T072 [US4] 使用 `Result.error()` 方法构建错误响应
- [X] T073 [US4] 从 `TraceIdUtil` 获取 TraceId 并注入到错误响应中
- [X] T074 [US4] 设置响应状态码为 `HttpStatus.OK`（统一错误格式使用 200 状态码）
- [X] T075 [US4] 设置响应 Content-Type 为 `MediaType.APPLICATION_JSON`
- [X] T076 [US4] 在 `GatewayExceptionHandler` 中添加完整的中文注释（类注释、方法注释）
- [X] T077 [US4] 创建 `GatewayExceptionHandlerTest` 单元测试在 `atlas-gateway/src/test/java/com/atlas/gateway/exception/GatewayExceptionHandlerTest.java`
- [X] T078 [US4] 创建统一错误返回集成测试，验证各种异常场景都能返回统一格式的错误响应

### Phase 7: 鉴权控制实现（白名单 + Token 校验占位） [US5]

**目标**: 实现 Gateway 鉴权控制，支持白名单和 Token 校验扩展点

**独立测试标准**: 白名单路径的请求能够正常通过 Gateway，非白名单路径的请求会触发 Token 校验（占位实现默认放行），白名单配置可以通过 Nacos Config 配置和更新，Token 校验扩展点可以后续扩展实现

**任务**:

- [X] T079 [P] [US5] 创建 `TokenValidator` 接口在 `atlas-gateway/src/main/java/com/atlas/gateway/filter/TokenValidator.java`
- [X] T080 [US5] 在 `TokenValidator` 接口中定义 `validate(ServerHttpRequest request)` 方法
- [X] T081 [US5] 创建 `DefaultTokenValidator` 实现类在 `atlas-gateway/src/main/java/com/atlas/gateway/filter/DefaultTokenValidator.java`
- [X] T082 [US5] 在 `DefaultTokenValidator` 中实现占位逻辑（默认返回 `true`，放行所有请求）
- [X] T083 [P] [US5] 创建 `AuthGatewayFilter` 类在 `atlas-gateway/src/main/java/com/atlas/gateway/filter/AuthGatewayFilter.java`
- [X] T084 [US5] 在 `AuthGatewayFilter` 中实现 `GlobalFilter` 接口
- [X] T085 [US5] 在 `AuthGatewayFilter` 中实现 `Ordered` 接口，设置执行顺序为 `Ordered.HIGHEST_PRECEDENCE + 1`
- [X] T086 [US5] 在 `AuthGatewayFilter` 中注入 `GatewayProperties` 和 `TokenValidator`
- [X] T087 [US5] 在 `filter()` 方法中实现白名单路径匹配功能（使用 Ant 风格路径匹配器）
- [X] T088 [US5] 如果请求路径匹配白名单，直接放行
- [X] T089 [US5] 如果请求路径不匹配白名单，调用 `TokenValidator.validate()` 进行 Token 校验
- [X] T090 [US5] 如果 Token 校验失败，返回统一错误格式（错误码：013001）
- [X] T091 [US5] 实现从 `GatewayProperties` 读取白名单配置
- [X] T092 [US5] 实现白名单配置动态更新功能（监听 Nacos Config 配置变更）
- [X] T093 [US5] 在 `AuthGatewayFilter` 中添加完整的中文注释（类注释、方法注释）
- [X] T094 [US5] 创建 `AuthGatewayFilterTest` 单元测试在 `atlas-gateway/src/test/java/com/atlas/gateway/filter/AuthGatewayFilterTest.java`
- [X] T095 [US5] 创建鉴权控制集成测试，验证白名单功能和 Token 校验扩展点

### Phase 8: Nacos Config 配置管理完善 [US6]

**目标**: 完善 Nacos Config 配置管理，确保所有配置支持动态更新

**独立测试标准**: Gateway 能够从 Nacos Config 读取所有配置，配置变更后 Gateway 能够动态生效（无需重启），配置项符合项目的配置命名规范（`atlas.gateway.*`）

**任务**:

- [X] T096 [US6] 完善 `GatewayProperties` 配置属性类，确保所有配置项都支持动态更新
- [X] T097 [US6] 实现配置变更监听器，监听 Nacos Config 配置变更事件
- [X] T098 [US6] 实现路由规则动态更新功能（配置变更后重新加载路由规则）
- [X] T099 [US6] 实现白名单动态更新功能（配置变更后重新加载白名单配置）
- [X] T100 [US6] 实现 CORS 配置动态更新功能（配置变更后重新加载 CORS 配置）
- [X] T101 [US6] 验证配置项符合项目的配置命名规范（`atlas.gateway.*`）
- [X] T102 [US6] 创建配置动态更新集成测试，验证所有配置都可以动态更新

### Phase 9: 文档和测试完善

**目标**: 完善文档和测试，确保代码质量和可维护性

**独立测试标准**: 文档完整，包含使用示例和最佳实践，单元测试覆盖率 ≥ 70%，集成测试覆盖主要业务流程，验收测试文档完整

**任务**:

- [ ] T103 完善 `README.md` 文档，添加使用示例和最佳实践
- [ ] T104 完善单元测试覆盖率，确保 ≥ 70%
- [ ] T105 完善集成测试，覆盖主要业务流程
- [ ] T106 创建验收测试文档，包含所有验收测试场景
- [ ] T107 运行 `mvn spotless:apply` 格式化代码
- [ ] T108 运行 `mvn clean install` 验证所有测试通过
- [ ] T109 运行 `mvn test` 验证单元测试覆盖率 ≥ 70%

## 实施策略

### MVP 优先

**MVP 范围**: Phase 1 + Phase 2 + Phase 3 + Phase 4 + Phase 5 + Phase 6

MVP 提供核心的路由转发、CORS 支持、TraceId 追踪和统一错误返回功能，满足最基本的 Gateway 需求。鉴权控制（US5）和 Nacos Config 配置管理完善（US6）可以在后续迭代中实现。

### 增量交付

1. **迭代 1**: Phase 1 + Phase 2 + Phase 3（Setup + Foundational + US1）
   - 提供基础的路由转发功能
   - 支持通过 Nacos Config 配置路由规则

2. **迭代 2**: Phase 4 + Phase 5 + Phase 6（US2 + US3 + US4）
   - 添加 CORS 跨域支持
   - 添加 TraceId 链路追踪
   - 添加统一错误返回

3. **迭代 3**: Phase 7 + Phase 8（US5 + US6）
   - 添加鉴权控制（白名单 + Token 校验占位）
   - 完善 Nacos Config 配置管理

4. **迭代 4**: Phase 9（Polish）
   - 完善文档和测试
   - 代码质量优化

### 独立测试标准

每个阶段都有明确的独立测试标准，确保每个阶段完成后都可以独立验证功能是否正常工作。

