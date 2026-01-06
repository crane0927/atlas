# 功能规划文档

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0
- ✅ **API 设计**: 遵循 RESTful 风格
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 提取公共方法，避免重复代码
- ✅ **模块化**: 业务模块按业务模块再按技术分层组织（`com.atlas.auth.*`）
- ✅ **包结构规范**: 所有对象类型（DTO、VO、Entity、BO、枚举）放在 `model` 包下
- ✅ **统一响应格式**: 使用 `atlas-common-feature-core` 的 `Result<T>`
- ✅ **服务调用**: 使用 OpenFeign 调用 `atlas-system-api`
- ✅ **Redis 使用**: 使用 `atlas-common-infra-redis` 模块
- ✅ **安全组件**: 复用 `atlas-common-feature-security` 的 `LoginUser` 和 `SecurityContext`

## 功能概述

实现 `atlas-auth` 认证服务，提供用户登录、登出、Token 签发与校验功能。服务通过 `atlas-system-api` 获取用户基础信息和权限，使用 Redis 存储会话和 Token 黑名单，并为 Gateway 提供 Token 校验能力（支持 JWT 公钥验证或 Introspection 接口两种方式）。

**核心价值**:
- 提供统一的用户认证和授权服务
- 支持 JWT Token 的签发、校验和黑名单管理
- 为 Gateway 和下游服务提供 Token 校验和用户上下文
- 使用 Redis 实现高性能的会话管理和黑名单机制

## 技术方案

### 架构设计

**模块结构**:
```
atlas-auth/
├── src/main/java/com/atlas/auth/
│   ├── controller/          # Controller 层
│   │   └── AuthController.java
│   ├── service/             # Service 层
│   │   ├── AuthService.java
│   │   ├── TokenService.java
│   │   └── SessionService.java
│   ├── mapper/              # Mapper 层（如果需要数据库存储）
│   ├── model/               # 模型层（所有对象类型）
│   │   ├── dto/             # DTO 对象
│   │   ├── vo/              # VO 对象
│   │   ├── entity/          # Entity 实体（如果需要）
│   │   ├── bo/              # BO 业务对象（如果需要）
│   │   └── enums/           # 枚举常量
│   ├── config/              # 配置类
│   │   ├── JwtConfig.java
│   │   ├── SecurityConfig.java
│   │   └── RedisConfig.java
│   ├── util/                # 工具类
│   │   ├── JwtUtil.java
│   │   └── PasswordUtil.java
│   └── filter/              # 过滤器（如果需要）
│       └── SecurityContextFilter.java
```

**核心组件**:
1. **AuthController**: 提供登录、登出、公钥、Introspection 接口
2. **AuthService**: 实现登录、登出业务逻辑
3. **TokenService**: 实现 Token 签发、校验逻辑
4. **SessionService**: 实现 Redis 会话和黑名单管理
5. **JwtUtil**: JWT Token 生成和解析工具
6. **PasswordUtil**: 密码加密和验证工具
7. **SecurityContextFilter**: 为下游服务提供 `LoginUser` 上下文

**依赖关系**:
- `atlas-common-feature-core`: 统一响应格式、异常处理
- `atlas-common-feature-security`: `LoginUser`、`SecurityContext` 接口
- `atlas-common-infra-redis`: Redis 操作工具
- `atlas-service-api/atlas-system-api`: 用户和权限查询接口
- `spring-cloud-starter-openfeign`: Feign 客户端

### 技术选型

**JWT 库**:
- **选择**: `io.jsonwebtoken:jjwt` (0.12.x)
- **理由**: 
  - 支持 RSA 算法（RS256）
  - 支持 JWT 标准（RFC 7519）
  - 支持 JWK 格式
  - 与 Spring Boot 4.0.1 兼容
  - 社区活跃，文档完善

**密码加密**:
- **选择**: Spring Security 的 `BCryptPasswordEncoder`
- **理由**:
  - Spring Security 已包含在 Spring Boot 中
  - BCrypt 是安全的密码哈希算法
  - 支持盐值自动生成
  - 与 Spring Boot 4.0.1 兼容

**Redis 客户端**:
- **选择**: Spring Data Redis (通过 `atlas-common-infra-redis`)
- **理由**:
  - 项目已有 Redis 基础设施模块
  - 使用 `RedisTemplate` 进行统一操作
  - 支持连接池和集群模式
  - 与 Spring Boot 4.0.1 兼容

**RSA 密钥管理**:
- **选择**: Java `KeyPairGenerator` + 配置文件存储
- **理由**:
  - JDK 21 原生支持
  - 密钥存储在配置中心（Nacos）
  - 支持密钥轮换（通过配置更新）
  - 无需额外依赖

**Feign 客户端**:
- **选择**: Spring Cloud OpenFeign
- **理由**:
  - Spring Cloud 官方组件
  - 与 Spring Boot 4.0.1 兼容
  - 支持负载均衡和熔断
  - 已集成在项目中

### 关键技术点

**JWT Token 结构**:
```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT",
    "kid": "key-2025-01-06"
  },
  "payload": {
    "userId": 1,
    "username": "admin",
    "roles": ["admin", "user"],
    "permissions": ["user:read", "user:write"],
    "iat": 1704542400,
    "exp": 1704549600,
    "jti": "jti-123456"
  }
}
```

**Redis Key 设计**:
- 会话信息: `session:{userId}` (String, JSON)
- Token 黑名单: `token:blacklist:{tokenId}` (String, JSON, 带过期时间)

**密码验证流程**:
1. 从 `atlas-system-api` 获取用户信息（包含加密后的密码）
2. 使用 `BCryptPasswordEncoder.matches()` 验证密码
3. 验证通过后继续登录流程

**Token 校验流程**:
1. 解析 JWT Token（验证格式）
2. 验证签名（使用公钥）
3. 验证过期时间
4. 检查黑名单（查询 Redis）
5. 返回验证结果和用户信息

## 实施计划

### 阶段 0: 研究与技术调研

**目标**: 完成技术选型和关键技术点研究

**任务**:
- [ ] 研究 JWT 库选型（jjwt vs nimbus-jose-jwt）
- [ ] 研究 RSA 密钥生成和管理方案
- [ ] 研究 BCrypt 密码加密最佳实践
- [ ] 研究 Redis 会话存储和黑名单实现方案
- [ ] 研究 Gateway Token 校验集成方案（JWT 公钥 vs Introspection）
- [ ] 研究 SecurityContext 实现方案（ThreadLocal vs Request Scope）

**输出**: `research.md`

### 阶段 1: 基础组件与数据模型

**目标**: 创建模块结构、数据模型和基础组件

**任务**:
- [ ] 创建 `atlas-auth` 模块和目录结构
- [ ] 创建 `pom.xml` 并配置依赖
- [ ] 定义数据模型（VO、DTO、枚举）
- [ ] 实现 JWT 工具类（`JwtUtil`）
- [ ] 实现密码工具类（`PasswordUtil`）
- [ ] 配置 JWT 密钥管理（`JwtConfig`）
- [ ] 配置 Redis（复用 `atlas-common-infra-redis`）
- [ ] 集成 `atlas-system-api` Feign 客户端

**输出**: `data-model.md`, 基础代码结构

### 阶段 2: 核心业务逻辑

**目标**: 实现登录、登出、Token 签发与校验

**任务**:
- [ ] 实现 `AuthService`（登录、登出逻辑）
- [ ] 实现 `TokenService`（Token 签发、校验）
- [ ] 实现 `SessionService`（Redis 会话和黑名单管理）
- [ ] 实现 `AuthController`（登录、登出接口）
- [ ] 实现异常处理和错误码
- [ ] 编写单元测试

**输出**: 核心业务代码、单元测试

### 阶段 3: Gateway 集成

**目标**: 实现 Gateway Token 校验支持

**任务**:
- [ ] 实现 JWT 公钥接口（`GET /api/v1/auth/public-key`）
- [ ] 实现 Introspection 接口（`POST /api/v1/auth/introspect`）
- [ ] 实现服务间认证（Introspection 接口）
- [ ] 实现公钥轮换机制
- [ ] 编写接口测试

**输出**: Gateway 集成代码、接口测试

### 阶段 4: 下游服务上下文

**目标**: 为下游服务提供 `LoginUser` 上下文

**任务**:
- [ ] 实现 `SecurityContextFilter`（从请求头提取 Token）
- [ ] 实现 `LoginUser` 实现类（封装用户信息）
- [ ] 实现 `SecurityContext` 实现类（ThreadLocal 方式）
- [ ] 配置 Filter 注册
- [ ] 编写集成测试

**输出**: 上下文提供代码、集成测试

### 阶段 5: 文档与验收

**目标**: 完成文档和验收测试

**任务**:
- [ ] 编写 API 文档（OpenAPI/Swagger）
- [ ] 编写使用文档（`quickstart.md`）
- [ ] 编写集成测试
- [ ] 性能测试（登录、Token 校验）
- [ ] 验收测试（登录、登出、Gateway 校验、下游上下文）

**输出**: 文档、测试报告

## 风险评估

### 技术风险

**风险 1: JWT 库兼容性问题**
- **描述**: jjwt 0.12.x 可能与 Spring Boot 4.0.1 存在兼容性问题
- **影响**: 高
- **应对**: 
  - 提前进行兼容性测试
  - 准备备选方案（nimbus-jose-jwt）
  - 关注 Spring Boot 和 jjwt 的版本兼容性

**风险 2: Redis 性能瓶颈**
- **描述**: 高并发场景下 Redis 可能成为性能瓶颈
- **影响**: 中
- **应对**:
  - 使用 Redis 连接池
  - 优化 Redis Key 设计
  - 考虑使用 Redis 集群模式
  - 实现缓存预热和降级策略

**风险 3: 密码验证方式不明确**
- **描述**: `atlas-system-api` 可能不提供密码字段，需要密码验证接口
- **影响**: 中
- **应对**:
  - 与 `atlas-system` 团队确认密码验证方式
  - 如果需要在 `atlas-system-api` 添加密码验证接口
  - 或使用密码哈希值进行验证

### 集成风险

**风险 4: Gateway 集成复杂度**
- **描述**: Gateway 需要同时支持两种校验方式，实现复杂度较高
- **影响**: 中
- **应对**:
  - 先实现一种方式（JWT 公钥），验证可行性
  - 再实现第二种方式（Introspection）
  - 通过配置切换，降低复杂度

**风险 5: 服务间认证实现**
- **描述**: Introspection 接口的服务间认证实现可能复杂
- **影响**: 低
- **应对**:
  - 使用简单的 API Key 认证
  - 或使用 Spring Cloud 的服务间认证机制
  - 参考 Gateway 的服务间认证实现

### 业务风险

**风险 6: Token 刷新机制**
- **描述**: 规范中提到 Refresh Token 是可选的，但实际可能需要
- **影响**: 低
- **应对**:
  - 第一阶段不实现 Refresh Token
  - 根据实际需求决定是否实现
  - 预留扩展接口

## 验收标准

### 功能验收

1. **登录功能**:
   - ✅ 用户使用正确的用户名和密码可以成功登录
   - ✅ 用户使用错误的密码登录失败，返回错误信息
   - ✅ 非激活状态的用户无法登录
   - ✅ 登录成功后返回有效的 Token
   - ✅ Token 包含用户ID、用户名、角色、权限等信息
   - ✅ 用户会话信息已存储到 Redis

2. **登出功能**:
   - ✅ 用户使用有效 Token 可以成功登出
   - ✅ 登出后 Token 已加入黑名单，无法再使用
   - ✅ 登出后用户会话信息已清除
   - ✅ 使用已登出的 Token 进行请求会被拒绝

3. **Token 校验功能**:
   - ✅ 有效 Token 验证通过
   - ✅ 过期 Token 验证失败
   - ✅ 签名无效的 Token 验证失败
   - ✅ 黑名单中的 Token 验证失败
   - ✅ 验证通过时返回用户信息

4. **Gateway 集成**:
   - ✅ Gateway 能够成功获取 JWT 公钥
   - ✅ Gateway 使用公钥能够验证 Token 签名
   - ✅ Gateway 能够成功调用 Introspection 接口
   - ✅ Gateway 可以通过配置选择使用哪种校验方式

5. **下游服务上下文**:
   - ✅ 下游服务能够通过 `SecurityContext` 获取 `LoginUser`
   - ✅ `LoginUser` 包含完整的用户信息（用户ID、用户名、角色、权限）
   - ✅ 未登录时 `SecurityContext.getLoginUser()` 返回 null
   - ✅ 已登录时 `SecurityContext.isAuthenticated()` 返回 true

### 性能验收

- ✅ 登录接口响应时间 < 500ms（P95）
- ✅ Token 校验接口响应时间 < 100ms（P95，Introspection 方式）
- ✅ 支持 1000+ 并发登录请求
- ✅ Redis 操作响应时间 < 10ms

### 可靠性验收

- ✅ 登录成功率 ≥ 99.9%
- ✅ Token 校验准确率 100%（无误判）
- ✅ Redis 操作成功率 ≥ 99.9%

### 安全性验收

- ✅ 密码验证失败后不暴露用户是否存在
- ✅ Token 签名使用强加密算法（RSA 256）
- ✅ 黑名单机制有效防止已登出 Token 的使用
- ✅ 支持 Token 过期自动失效

### 集成验收

- ✅ 与 `atlas-system-api` 集成成功，能够获取用户信息和权限
- ✅ 与 Gateway 集成成功，Gateway 能够验证 Token
- ✅ 与下游服务集成成功，下游服务能够获取 `LoginUser` 上下文
