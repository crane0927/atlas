# 任务清单

## 宪法合规检查清单

在开始实施前，确认以下事项：

- [ ] 技术栈版本符合宪法要求（JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0）
- [ ] API 设计遵循 RESTful 规范
- [ ] 代码注释使用中文
- [ ] 已识别可复用的公共方法（`atlas-common-feature-core`、`atlas-common-feature-security`、`atlas-common-infra-redis`）
- [ ] 包结构符合规范（业务模块按业务模块再按技术分层组织）
- [ ] 所有对象类型（DTO、VO、Entity、BO、枚举）放在 `model` 包下

## 依赖关系图

```
Phase 1 (Setup) → Phase 2 (Foundational) → Phase 3 (US1: 登录) → Phase 4 (US2: 登出)
                                                                    ↓
Phase 5 (US3: Gateway JWT 公钥) ← Phase 6 (US4: Gateway Introspection) → Phase 7 (US5: 校验方式切换)
                                                                    ↓
                                                          Phase 8 (US6: 下游上下文)
```

**说明**:
- Phase 1-2: 必须完成，为所有后续阶段提供基础
- Phase 3-4: 核心功能，可以并行开发（US1 和 US2 相对独立）
- Phase 5-6: Gateway 集成，可以并行开发（两种校验方式独立）
- Phase 7: 依赖 Phase 5-6
- Phase 8: 依赖 Phase 3（需要登录功能）

## 并行执行示例

### US1 和 US2 可以并行
- US1: 登录功能（Token 签发）
- US2: 登出功能（Token 黑名单）
- 共享组件：TokenService、SessionService、JwtUtil

### US3 和 US4 可以并行
- US3: JWT 公钥接口
- US4: Introspection 接口
- 共享组件：TokenService

## 实施策略

**MVP 范围**: Phase 1 + Phase 2 + Phase 3（用户登录）
- 最小可行产品：用户能够登录并获得 Token
- 后续增量：登出、Gateway 集成、下游上下文

**增量交付**:
1. **增量 1**: 登录功能（US1）
2. **增量 2**: 登出功能（US2）
3. **增量 3**: Gateway 集成（US3/US4）
4. **增量 4**: 下游上下文（US6）

---

## Phase 1: 项目初始化

**目标**: 创建 `atlas-auth` 模块，配置基础依赖和项目结构。

**独立测试标准**: 项目可以成功编译，依赖配置正确，目录结构符合规范。

### 任务列表

- [ ] T001 在根 `pom.xml` 中添加 `atlas-auth` 模块
- [ ] T002 创建 `atlas-auth/pom.xml`，配置父模块和基础依赖
- [ ] T003 在 `atlas-auth/pom.xml` 中添加 `atlas-common-feature-core` 依赖
- [ ] T004 在 `atlas-auth/pom.xml` 中添加 `atlas-common-feature-security` 依赖
- [ ] T005 在 `atlas-auth/pom.xml` 中添加 `atlas-common-infra-redis` 依赖
- [ ] T006 在 `atlas-auth/pom.xml` 中添加 `atlas-service-api/atlas-system-api` 依赖
- [ ] T007 在 `atlas-auth/pom.xml` 中添加 `spring-cloud-starter-openfeign` 依赖
- [ ] T008 在 `atlas-auth/pom.xml` 中添加 `io.jsonwebtoken:jjwt` 依赖（版本 0.12.x）
- [ ] T009 创建目录结构：`atlas-auth/src/main/java/com/atlas/auth/controller`
- [ ] T010 创建目录结构：`atlas-auth/src/main/java/com/atlas/auth/service`
- [ ] T011 创建目录结构：`atlas-auth/src/main/java/com/atlas/auth/model/dto`
- [ ] T012 创建目录结构：`atlas-auth/src/main/java/com/atlas/auth/model/vo`
- [ ] T013 创建目录结构：`atlas-auth/src/main/java/com/atlas/auth/model/enums`
- [ ] T014 创建目录结构：`atlas-auth/src/main/java/com/atlas/auth/config`
- [ ] T015 创建目录结构：`atlas-auth/src/main/java/com/atlas/auth/util`
- [ ] T016 创建目录结构：`atlas-auth/src/main/java/com/atlas/auth/filter`
- [ ] T017 创建目录结构：`atlas-auth/src/test/java/com/atlas/auth`
- [ ] T018 创建 `atlas-auth/src/main/resources/application.yml`
- [ ] T019 验证项目可以成功编译（`mvn clean compile`）

---

## Phase 2: 基础组件

**目标**: 实现基础工具类和配置类，为业务功能提供支撑。

**独立测试标准**: 工具类可以独立测试，配置类可以正确加载，JWT 工具可以生成和解析 Token。

### 任务列表

- [ ] T020 创建 `JwtConfig` 配置类在 `atlas-auth/src/main/java/com/atlas/auth/config/JwtConfig.java`
- [ ] T021 在 `JwtConfig` 中配置 RSA 密钥对（从 Nacos Config 读取）
- [ ] T022 在 `JwtConfig` 中配置 Token 过期时间（默认 7200 秒）
- [ ] T023 创建 `JwtUtil` 工具类在 `atlas-auth/src/main/java/com/atlas/auth/util/JwtUtil.java`
- [ ] T024 在 `JwtUtil` 中实现 `generateToken()` 方法（生成 JWT Token）
- [ ] T025 在 `JwtUtil` 中实现 `parseToken()` 方法（解析 JWT Token）
- [ ] T026 在 `JwtUtil` 中实现 `validateToken()` 方法（验证 Token 签名和过期时间）
- [ ] T027 创建 `PasswordUtil` 工具类在 `atlas-auth/src/main/java/com/atlas/auth/util/PasswordUtil.java`
- [ ] T028 在 `PasswordUtil` 中实现 `encode()` 方法（密码加密，使用 BCrypt）
- [ ] T029 在 `PasswordUtil` 中实现 `matches()` 方法（密码验证）
- [ ] T030 创建 `TokenType` 枚举在 `atlas-auth/src/main/java/com/atlas/auth/model/enums/TokenType.java`
- [ ] T031 创建 `JwtUtil` 的单元测试在 `atlas-auth/src/test/java/com/atlas/auth/util/JwtUtilTest.java`
- [ ] T032 创建 `PasswordUtil` 的单元测试在 `atlas-auth/src/test/java/com/atlas/auth/util/PasswordUtilTest.java`

---

## Phase 3: US1 - 用户登录

**目标**: 实现用户登录功能，验证用户身份并签发 Token。

**独立测试标准**: 用户可以使用正确的用户名和密码登录，获得有效的 Token，Token 包含用户信息，会话信息已存储到 Redis。

### 任务列表

- [ ] T033 [US1] 创建 `LoginRequestVO` 在 `atlas-auth/src/main/java/com/atlas/auth/model/vo/LoginRequestVO.java`
- [ ] T034 [US1] 创建 `LoginResponseVO` 在 `atlas-auth/src/main/java/com/atlas/auth/model/vo/LoginResponseVO.java`
- [ ] T035 [US1] 创建 `UserVO` 在 `atlas-auth/src/main/java/com/atlas/auth/model/vo/UserVO.java`
- [ ] T036 [US1] 创建 `TokenInfoDTO` 在 `atlas-auth/src/main/java/com/atlas/auth/model/dto/TokenInfoDTO.java`
- [ ] T037 [US1] 创建 `SessionService` 接口在 `atlas-auth/src/main/java/com/atlas/auth/service/SessionService.java`
- [ ] T038 [US1] 实现 `SessionService` 在 `atlas-auth/src/main/java/com/atlas/auth/service/impl/SessionServiceImpl.java`
- [ ] T039 [US1] 在 `SessionService` 中实现 `saveSession()` 方法（存储用户会话到 Redis）
- [ ] T040 [US1] 在 `SessionService` 中实现 `getSession()` 方法（从 Redis 获取会话）
- [ ] T041 [US1] 在 `SessionService` 中实现 `deleteSession()` 方法（删除会话）
- [ ] T042 [US1] 创建 `TokenService` 接口在 `atlas-auth/src/main/java/com/atlas/auth/service/TokenService.java`
- [ ] T043 [US1] 实现 `TokenService` 在 `atlas-auth/src/main/java/com/atlas/auth/service/impl/TokenServiceImpl.java`
- [ ] T044 [US1] 在 `TokenService` 中实现 `generateToken()` 方法（生成 JWT Token）
- [ ] T045 [US1] 在 `TokenService` 中实现 `parseToken()` 方法（解析 Token 获取用户信息）
- [ ] T046 [US1] 创建 `AuthService` 接口在 `atlas-auth/src/main/java/com/atlas/auth/service/AuthService.java`
- [ ] T047 [US1] 实现 `AuthService` 在 `atlas-auth/src/main/java/com/atlas/auth/service/impl/AuthServiceImpl.java`
- [ ] T048 [US1] 在 `AuthService` 中注入 `UserQueryApi` Feign 客户端
- [ ] T049 [US1] 在 `AuthService` 中注入 `PermissionQueryApi` Feign 客户端
- [ ] T050 [US1] 在 `AuthService` 中实现 `login()` 方法（登录业务逻辑）
- [ ] T051 [US1] 在 `login()` 方法中调用 `UserQueryApi.getUserByUsername()` 查询用户信息
- [ ] T052 [US1] 在 `login()` 方法中验证用户状态（必须为激活状态）
- [ ] T053 [US1] 在 `login()` 方法中验证用户密码（使用 `PasswordUtil.matches()`）
- [ ] T054 [US1] 在 `login()` 方法中调用 `PermissionQueryApi.getUserAuthorities()` 查询用户权限
- [ ] T055 [US1] 在 `login()` 方法中调用 `TokenService.generateToken()` 生成 Token
- [ ] T056 [US1] 在 `login()` 方法中调用 `SessionService.saveSession()` 存储会话
- [ ] T057 [US1] 创建 `AuthController` 在 `atlas-auth/src/main/java/com/atlas/auth/controller/AuthController.java`
- [ ] T058 [US1] 在 `AuthController` 中实现 `login()` 接口（POST `/api/v1/auth/login`）
- [ ] T059 [US1] 在 `login()` 接口中处理异常（用户不存在、密码错误、用户未激活等）
- [ ] T060 [US1] 创建登录接口的单元测试在 `atlas-auth/src/test/java/com/atlas/auth/controller/AuthControllerTest.java`
- [ ] T061 [US1] 创建 `AuthService` 的单元测试在 `atlas-auth/src/test/java/com/atlas/auth/service/AuthServiceTest.java`

---

## Phase 4: US2 - 用户登出

**目标**: 实现用户登出功能，使 Token 失效并清除会话。

**独立测试标准**: 用户可以使用有效 Token 登出，Token 已加入黑名单，会话信息已清除，已登出的 Token 无法再使用。

### 任务列表

- [ ] T062 [US2] 在 `SessionService` 中实现 `addToBlacklist()` 方法（将 Token 加入黑名单）
- [ ] T063 [US2] 在 `SessionService` 中实现 `isBlacklisted()` 方法（检查 Token 是否在黑名单中）
- [ ] T064 [US2] 在 `AuthService` 中实现 `logout()` 方法（登出业务逻辑）
- [ ] T065 [US2] 在 `logout()` 方法中从请求头提取 Token
- [ ] T066 [US2] 在 `logout()` 方法中调用 `TokenService.parseToken()` 解析 Token
- [ ] T067 [US2] 在 `logout()` 方法中调用 `SessionService.addToBlacklist()` 加入黑名单
- [ ] T068 [US2] 在 `logout()` 方法中调用 `SessionService.deleteSession()` 删除会话
- [ ] T069 [US2] 在 `AuthController` 中实现 `logout()` 接口（POST `/api/v1/auth/logout`）
- [ ] T070 [US2] 在 `logout()` 接口中添加 `@PreAuthorize` 或自定义拦截器验证 Token
- [ ] T071 [US2] 在 `logout()` 接口中处理异常（Token 无效、缺失等）
- [ ] T072 [US2] 创建登出接口的单元测试在 `atlas-auth/src/test/java/com/atlas/auth/controller/AuthControllerTest.java`
- [ ] T073 [US2] 创建 `AuthService.logout()` 的单元测试

---

## Phase 5: US3 - Gateway Token 校验（JWT 公钥方式）

**目标**: 为 Gateway 提供 JWT 公钥接口，支持 Gateway 自主验证 Token。

**独立测试标准**: Gateway 能够成功获取 JWT 公钥，使用公钥能够验证 Token 签名，支持公钥轮换。

### 任务列表

- [ ] T074 [US3] 创建 `PublicKeyResponseVO` 在 `atlas-auth/src/main/java/com/atlas/auth/model/vo/PublicKeyResponseVO.java`
- [ ] T075 [US3] 在 `JwtConfig` 中实现 `getPublicKey()` 方法（获取公钥）
- [ ] T076 [US3] 在 `JwtConfig` 中实现 `getKeyId()` 方法（获取密钥ID）
- [ ] T077 [US3] 在 `AuthController` 中实现 `getPublicKey()` 接口（GET `/api/v1/auth/public-key`）
- [ ] T078 [US3] 在 `getPublicKey()` 接口中返回公钥（PEM 格式或 JWK 格式）
- [ ] T079 [US3] 在 `getPublicKey()` 接口中返回密钥ID（支持公钥轮换）
- [ ] T080 [US3] 创建公钥接口的单元测试在 `atlas-auth/src/test/java/com/atlas/auth/controller/AuthControllerTest.java`
- [ ] T081 [US3] 验证公钥格式符合标准（JWK 或 PEM）

---

## Phase 6: US4 - Gateway Token 校验（Introspection 接口方式）

**目标**: 为 Gateway 提供 Token Introspection 接口，Gateway 通过调用接口验证 Token。

**独立测试标准**: Gateway 能够成功调用 Introspection 接口，接口能够正确验证 Token 并返回结果，接口响应时间满足要求（< 100ms），接口支持服务间认证。

### 任务列表

- [ ] T082 [US4] 创建 `IntrospectRequestVO` 在 `atlas-auth/src/main/java/com/atlas/auth/model/vo/IntrospectRequestVO.java`
- [ ] T083 [US4] 创建 `IntrospectResponseVO` 在 `atlas-auth/src/main/java/com/atlas/auth/model/vo/IntrospectResponseVO.java`
- [ ] T084 [US4] 在 `TokenService` 中实现 `validateToken()` 方法（验证 Token 有效性）
- [ ] T085 [US4] 在 `validateToken()` 方法中验证 Token 格式（JWT 标准）
- [ ] T086 [US4] 在 `validateToken()` 方法中验证 Token 签名（使用公钥）
- [ ] T087 [US4] 在 `validateToken()` 方法中验证 Token 过期时间
- [ ] T088 [US4] 在 `validateToken()` 方法中检查 Token 是否在黑名单中（调用 `SessionService.isBlacklisted()`）
- [ ] T089 [US4] 在 `AuthController` 中实现 `introspect()` 接口（POST `/api/v1/auth/introspect`）
- [ ] T090 [US4] 在 `introspect()` 接口中添加服务间认证（使用 API Key 或服务 Token）
- [ ] T091 [US4] 在 `introspect()` 接口中调用 `TokenService.validateToken()` 验证 Token
- [ ] T092 [US4] 在 `introspect()` 接口中返回 Token 验证结果和用户信息
- [ ] T093 [US4] 创建 Introspection 接口的单元测试在 `atlas-auth/src/test/java/com/atlas/auth/controller/AuthControllerTest.java`
- [ ] T094 [US4] 验证接口响应时间 < 100ms（性能测试）

---

## Phase 7: US5 - Gateway 校验方式切换

**目标**: Gateway 支持通过配置动态切换 Token 校验方式（JWT 公钥或 Introspection）。

**独立测试标准**: Gateway 能够动态切换校验方式，无需重启，切换后使用新的校验方式验证 Token。

### 任务列表

- [ ] T095 [US5] 验证 JWT 公钥接口和 Introspection 接口都已实现
- [ ] T096 [US5] 在 Gateway 配置中添加校验方式选择配置（JWT 公钥或 Introspection）
- [ ] T097 [US5] 验证 Gateway 可以通过配置动态切换校验方式（无需重启）
- [ ] T098 [US5] 创建集成测试验证切换功能

**说明**: 此阶段主要是在 Gateway 侧实现配置切换逻辑，`atlas-auth` 服务已提供两种校验方式，无需额外开发。

---

## Phase 8: US6 - 下游服务获取登录用户上下文

**目标**: 为下游服务提供 `LoginUser` 上下文信息。

**独立测试标准**: 下游服务能够通过 `SecurityContext` 获取 `LoginUser`，`LoginUser` 包含完整的用户信息，未登录时返回 null，已登录时 `isAuthenticated()` 返回 true。

### 任务列表

- [ ] T099 [US6] 创建 `LoginUserImpl` 实现类在 `atlas-auth/src/main/java/com/atlas/auth/model/dto/LoginUserImpl.java`
- [ ] T100 [US6] 实现 `LoginUser` 接口的所有方法（`getUserId()`, `getUsername()`, `getRoles()`, `getPermissions()`, `hasRole()`, `hasPermission()`）
- [ ] T101 [US6] 创建 `SecurityContextImpl` 实现类在 `atlas-auth/src/main/java/com/atlas/auth/context/SecurityContextImpl.java`
- [ ] T102 [US6] 在 `SecurityContextImpl` 中使用 `ThreadLocal<LoginUser>` 存储用户信息
- [ ] T103 [US6] 实现 `SecurityContext` 接口的所有方法（`getLoginUser()`, `isAuthenticated()`, `clear()`）
- [ ] T104 [US6] 创建 `SecurityContextFilter` 过滤器在 `atlas-auth/src/main/java/com/atlas/auth/filter/SecurityContextFilter.java`
- [ ] T105 [US6] 在 `SecurityContextFilter` 中从请求头提取 Token（`Authorization: Bearer {token}`）
- [ ] T106 [US6] 在 `SecurityContextFilter` 中调用 `TokenService.parseToken()` 解析 Token
- [ ] T107 [US6] 在 `SecurityContextFilter` 中调用 `TokenService.validateToken()` 验证 Token
- [ ] T108 [US6] 在 `SecurityContextFilter` 中将用户信息封装为 `LoginUserImpl` 对象
- [ ] T109 [US6] 在 `SecurityContextFilter` 中设置 `SecurityContext`（使用 `SecurityContextHolder.setContext()`）
- [ ] T110 [US6] 在 `SecurityContextFilter` 中在请求结束时清理 `SecurityContext`（`finally` 块中调用 `clear()`）
- [ ] T111 [US6] 创建 `SecurityConfig` 配置类在 `atlas-auth/src/main/java/com/atlas/auth/config/SecurityConfig.java`
- [ ] T112 [US6] 在 `SecurityConfig` 中注册 `SecurityContextFilter`（使用 `FilterRegistrationBean`）
- [ ] T113 [US6] 创建 `SecurityContextFilter` 的单元测试在 `atlas-auth/src/test/java/com/atlas/auth/filter/SecurityContextFilterTest.java`
- [ ] T114 [US6] 创建下游服务使用示例（在文档中说明如何使用 `SecurityContext`）

---

## Phase 9: 完善与验收

**目标**: 完善异常处理、错误码、文档，进行集成测试和性能测试。

**独立测试标准**: 所有接口正常工作，异常处理正确，文档完整，性能指标满足要求。

### 任务列表

- [ ] T115 创建错误码常量类在 `atlas-auth/src/main/java/com/atlas/auth/constant/AuthErrorCode.java`
- [ ] T116 定义所有业务错误码（登录失败、Token 无效、用户未激活等）
- [ ] T117 在 `AuthController` 中使用统一异常处理（`@ControllerAdvice`）
- [ ] T118 创建异常处理类在 `atlas-auth/src/main/java/com/atlas/auth/exception/AuthExceptionHandler.java`
- [ ] T119 在异常处理中返回统一的错误响应格式（`Result<T>`）
- [ ] T120 创建集成测试在 `atlas-auth/src/test/java/com/atlas/auth/integration/AuthIntegrationTest.java`
- [ ] T121 测试登录流程（包括与 `atlas-system-api` 的集成）
- [ ] T122 测试登出流程（包括 Redis 黑名单操作）
- [ ] T123 测试 Token 校验流程
- [ ] T124 测试 Gateway 集成（JWT 公钥方式和 Introspection 方式）
- [ ] T125 性能测试：登录接口响应时间 < 500ms（P95）
- [ ] T126 性能测试：Token 校验接口响应时间 < 100ms（P95）
- [ ] T127 性能测试：支持 1000+ 并发登录请求
- [ ] T128 性能测试：Redis 操作响应时间 < 10ms
- [ ] T129 创建 API 文档（使用 SpringDoc OpenAPI）
- [ ] T130 在 `AuthController` 中添加 Swagger 注解（`@Operation`, `@ApiResponse` 等）
- [ ] T131 验证所有类和方法包含完整的中文注释
- [ ] T132 验证代码符合包结构规范（所有对象类型在 `model` 包下）
- [ ] T133 验证代码符合 RESTful 设计规范
- [ ] T134 创建 README.md 文档在 `atlas-auth/README.md`
- [ ] T135 在 README.md 中添加模块说明、快速开始、配置说明

---

## 任务统计

- **总任务数**: 135
- **Phase 1 (Setup)**: 19 个任务
- **Phase 2 (Foundational)**: 13 个任务
- **Phase 3 (US1: 登录)**: 29 个任务
- **Phase 4 (US2: 登出)**: 12 个任务
- **Phase 5 (US3: Gateway JWT 公钥)**: 8 个任务
- **Phase 6 (US4: Gateway Introspection)**: 13 个任务
- **Phase 7 (US5: 校验方式切换)**: 4 个任务（主要在 Gateway 侧）
- **Phase 8 (US6: 下游上下文)**: 16 个任务
- **Phase 9 (完善与验收)**: 21 个任务

## MVP 范围建议

**最小可行产品**: Phase 1 + Phase 2 + Phase 3（用户登录功能）
- 任务数: 61 个任务
- 功能: 用户能够登录并获得 Token
- 验收: 登录接口正常工作，Token 包含用户信息

**后续增量**:
1. **增量 1**: Phase 4（登出功能）- 12 个任务
2. **增量 2**: Phase 5 + Phase 6（Gateway 集成）- 21 个任务
3. **增量 3**: Phase 8（下游上下文）- 16 个任务
4. **增量 4**: Phase 9（完善与验收）- 21 个任务

