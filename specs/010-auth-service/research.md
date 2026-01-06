# 技术研究文档

## 概述

本文档记录 `atlas-auth` 服务实现过程中的技术选型、方案研究和决策过程。

## JWT 库选型

### 研究目标

选择适合 Spring Boot 4.0.1 和 JDK 21 的 JWT 库，支持 RSA 算法（RS256）和 JWK 格式。

### 候选方案

**方案 A: io.jsonwebtoken:jjwt (0.12.x)**
- **优点**:
  - 社区活跃，文档完善
  - 支持 RSA 算法（RS256）
  - 支持 JWK 格式
  - API 简洁易用
  - 与 Spring Boot 兼容性好
- **缺点**:
  - 0.12.x 版本较新，可能存在兼容性问题
  - 需要额外依赖（Jackson）

**方案 B: com.nimbusds:nimbus-jose-jwt**
- **优点**:
  - 功能全面，支持多种算法
  - 成熟稳定
  - 支持 JWK 格式
- **缺点**:
  - API 相对复杂
  - 依赖较多
  - 文档相对较少

### 决策

**选择**: 方案 A - `io.jsonwebtoken:jjwt (0.12.x)`

**理由**:
1. API 简洁，易于使用和维护
2. 社区活跃，问题解决及时
3. 与 Spring Boot 生态集成良好
4. 支持项目所需的所有功能（RSA、JWK）

**备选方案**: 如果遇到兼容性问题，考虑使用 `nimbus-jose-jwt`

## RSA 密钥管理方案

### 研究目标

确定 RSA 密钥对的生成、存储和轮换方案。

### 候选方案

**方案 A: 配置文件存储（Nacos Config）**
- **优点**:
  - 集中管理，易于更新
  - 支持动态配置
  - 无需额外基础设施
- **缺点**:
  - 密钥以明文形式存储（需要加密）
  - 配置中心需要高可用

**方案 B: Java KeyStore 文件**
- **优点**:
  - 密钥加密存储
  - 标准 Java 方案
- **缺点**:
  - 文件管理复杂
  - 密钥轮换需要重启服务

**方案 C: 密钥管理服务（KMS）**
- **优点**:
  - 专业密钥管理
  - 支持密钥轮换
- **缺点**:
  - 需要额外基础设施
  - 增加系统复杂度

### 决策

**选择**: 方案 A - 配置文件存储（Nacos Config）

**理由**:
1. 项目已使用 Nacos Config，无需额外基础设施
2. 支持动态配置更新，便于密钥轮换
3. 实现简单，维护成本低

**实现细节**:
- 密钥以 PEM 格式存储在 Nacos Config
- 使用环境变量或加密配置存储密钥（生产环境）
- 支持密钥版本号（`keyId`），Gateway 可以获取最新密钥
- 密钥轮换：更新配置后，新 Token 使用新密钥，旧 Token 仍可使用旧密钥验证（过渡期）

## 密码加密方案

### 研究目标

确定密码加密算法和验证方式。

### 候选方案

**方案 A: BCrypt**
- **优点**:
  - Spring Security 内置支持
  - 安全性高，抗彩虹表攻击
  - 自动生成盐值
  - 计算成本可调
- **缺点**:
  - 计算成本较高（但可接受）

**方案 B: Argon2**
- **优点**:
  - 最新的密码哈希算法
  - 安全性更高
- **缺点**:
  - Spring Security 不直接支持
  - 需要额外依赖

**方案 C: PBKDF2**
- **优点**:
  - 标准算法
  - Spring Security 支持
- **缺点**:
  - 安全性略低于 BCrypt

### 决策

**选择**: 方案 A - BCrypt

**理由**:
1. Spring Security 内置支持，无需额外依赖
2. 安全性足够，满足项目需求
3. 实现简单，维护成本低
4. 与 Spring Boot 4.0.1 完全兼容

**实现细节**:
- 使用 `BCryptPasswordEncoder`（Spring Security）
- 强度参数：10（默认，可配置）
- 密码验证：使用 `matches()` 方法

## Redis 会话存储方案

### 研究目标

确定 Redis 会话存储和黑名单的实现方案。

### 研究内容

**Key 设计**:
- 会话信息: `session:{userId}` (String, JSON)
- Token 黑名单: `token:blacklist:{tokenId}` (String, JSON, 带过期时间)

**数据结构**:
- 使用 String 类型存储 JSON 格式数据
- 使用过期时间自动清理过期数据
- 使用 `RedisTemplate` 进行操作

**性能优化**:
- 使用连接池（通过 `atlas-common-infra-redis`）
- 批量操作减少网络往返
- 使用 Pipeline 提高性能（如果需要）

### 决策

**选择**: 使用 `atlas-common-infra-redis` 模块的 `RedisTemplate`

**理由**:
1. 项目已有 Redis 基础设施模块
2. 统一使用 `RedisTemplate` 进行 Redis 操作
3. 支持连接池和集群模式
4. 与 Spring Boot 4.0.1 兼容

**实现细节**:
- 使用 `RedisTemplate<String, Object>` 进行操作
- 会话信息使用 JSON 序列化
- 黑名单使用带过期时间的 Key
- 过期时间与 Token 过期时间一致

## Gateway Token 校验方案

### 研究目标

确定 Gateway Token 校验的实现方案（JWT 公钥 vs Introspection）。

### 研究内容

**方案 A: JWT 公钥方式**
- **优点**:
  - 性能好，无需网络调用
  - Gateway 自主验证，减少 auth 服务压力
- **缺点**:
  - 需要处理公钥轮换
  - Gateway 需要实现 JWT 解析逻辑

**方案 B: Introspection 接口方式**
- **优点**:
  - 实现简单，集中管理
  - 支持实时黑名单检查
- **缺点**:
  - 增加网络调用，性能略低
  - 需要服务间认证

### 决策

**选择**: 两种方式都支持，通过配置选择

**理由**:
1. 提供灵活性，可根据场景选择
2. 性能要求高的场景使用 JWT 公钥方式
3. 实现简单、集中管理的场景使用 Introspection 接口方式
4. Gateway 可以通过配置动态切换

**实现细节**:
- JWT 公钥接口：`GET /api/v1/auth/public-key`（公开接口）
- Introspection 接口：`POST /api/v1/auth/introspect`（服务间认证）
- Gateway 通过配置选择使用哪种方式
- 支持动态切换（无需重启）

## SecurityContext 实现方案

### 研究目标

确定 `SecurityContext` 的实现方式，为下游服务提供 `LoginUser` 上下文。

### 候选方案

**方案 A: ThreadLocal**
- **优点**:
  - 实现简单
  - 性能好
  - 线程安全
- **缺点**:
  - 需要手动清理（避免内存泄漏）
  - 不支持异步场景

**方案 B: Request Scope**
- **优点**:
  - Spring 管理生命周期
  - 支持异步场景
- **缺点**:
  - 实现相对复杂
  - 性能略低

**方案 C: Request Attributes**
- **优点**:
  - 简单直接
  - Spring 原生支持
- **缺点**:
  - 需要手动传递
  - 不够优雅

### 决策

**选择**: 方案 A - ThreadLocal

**理由**:
1. 实现简单，易于维护
2. 性能好，适合同步请求处理
3. 使用 Filter 在请求结束时清理，避免内存泄漏
4. 项目当前主要是同步请求，异步场景较少

**实现细节**:
- 使用 `ThreadLocal<LoginUser>` 存储用户信息
- 在 `SecurityContextFilter` 中设置和清理
- 实现 `SecurityContext` 接口，提供 `getLoginUser()` 和 `isAuthenticated()` 方法
- 通过 `SecurityContextHolder` 提供全局访问

## 密码验证方式

### 研究目标

确定如何验证用户密码（`atlas-system-api` 是否提供密码字段或验证接口）。

### 研究内容

**方案 A: 获取加密后的密码进行验证**
- **前提**: `atlas-system-api` 的 `UserDTO` 包含密码字段
- **实现**: 使用 `BCryptPasswordEncoder.matches()` 验证
- **优点**: 实现简单
- **缺点**: 需要传输密码字段（安全风险）

**方案 B: 提供密码验证接口**
- **前提**: `atlas-system-api` 提供密码验证接口
- **实现**: 调用验证接口
- **优点**: 密码不离开 system 服务，更安全
- **缺点**: 需要修改 `atlas-system-api`

### 决策

**选择**: 方案 A（当前），方案 B（未来优化）

**理由**:
1. 当前 `atlas-system-api` 的 `UserDTO` 可能不包含密码字段
2. 需要与 `atlas-system` 团队确认密码验证方式
3. 如果 `UserDTO` 不包含密码，需要在 `atlas-system-api` 添加密码验证接口
4. 优先使用方案 B（更安全），如果不可行则使用方案 A

**实现细节**:
- 如果 `UserDTO` 包含密码字段：使用 `BCryptPasswordEncoder.matches()` 验证
- 如果 `UserDTO` 不包含密码字段：调用 `atlas-system-api` 的密码验证接口（需要添加）
- 密码验证失败时，不暴露用户是否存在（统一返回"用户名或密码错误"）

## 总结

### 技术选型总结

| 技术点 | 选择 | 理由 |
|--------|------|------|
| JWT 库 | jjwt 0.12.x | API 简洁，社区活跃，功能完整 |
| RSA 密钥管理 | Nacos Config | 集中管理，支持动态配置 |
| 密码加密 | BCrypt | Spring Security 内置，安全性高 |
| Redis 客户端 | Spring Data Redis | 项目已有基础设施模块 |
| Gateway 校验 | 两种方式都支持 | 提供灵活性，可根据场景选择 |
| SecurityContext | ThreadLocal | 实现简单，性能好 |

### 待确认事项

1. **密码验证方式**: 需要与 `atlas-system` 团队确认 `UserDTO` 是否包含密码字段，或是否需要添加密码验证接口
2. **JWT 库兼容性**: 需要验证 jjwt 0.12.x 与 Spring Boot 4.0.1 的兼容性
3. **密钥存储安全**: 生产环境需要考虑密钥加密存储方案

### 后续优化方向

1. **Refresh Token**: 根据实际需求决定是否实现
2. **登录失败限制**: 实现登录失败次数限制，防止暴力破解
3. **Token 轮换机制**: 实现定期更换密钥对的机制
4. **性能优化**: 使用 Redis Pipeline 批量操作，提高性能

