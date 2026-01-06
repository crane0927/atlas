# 功能规格说明

## 宪法检查

本规格说明必须符合项目宪法要求：

- ✅ **RESTful API**: 所有接口遵循 RESTful 设计规范
- ✅ **中文注释**: 所有代码使用中文注释
- ✅ **代码复用**: 识别并复用现有公共方法（如 `atlas-common-feature-security` 的 `LoginUser`、`SecurityContext`）
- ✅ **模块化**: 功能归属 `atlas-auth` 模块
- ✅ **包结构规范**: 业务模块按业务模块再按技术分层组织（`com.atlas.auth.*`）
- ✅ **统一响应格式**: 使用 `atlas-common-feature-core` 模块的 `Result` 类
- ✅ **对象类型规范**: DTO、VO、Entity、BO、枚举等对象类型必须放在 `model` 包下

## 功能描述

实现 `atlas-auth` 认证服务，提供用户登录、登出、Token 签发与校验功能。服务通过 `atlas-system-api` 获取用户基础信息和权限，使用 Redis 存储会话和 Token 黑名单，并为 Gateway 提供 Token 校验能力（支持 JWT 公钥验证或 Introspection 接口两种方式）。

**核心目标**:
- 提供用户登录功能，验证用户身份并签发 Token
- 提供用户登出功能，将 Token 加入黑名单
- 提供 Token 签发功能，生成包含用户信息的访问令牌
- 提供 Token 校验功能，验证 Token 的有效性和权限
- 使用 Redis 存储用户会话和 Token 黑名单
- 通过 `atlas-system-api` 获取用户基础信息和权限
- 为 Gateway 提供 Token 校验能力（JWT 公钥验证或 Introspection 接口）
- 为下游服务提供 `LoginUser` 上下文信息

**使用场景**:
- 用户通过用户名密码登录系统
- 用户登出系统，使 Token 失效
- Gateway 验证请求中的 Token 是否有效
- 下游服务获取当前登录用户信息进行业务处理

## 用户场景

### 场景 1: 用户登录

**角色**: 用户（前端应用）

**前置条件**: 
- 用户已注册账户
- `atlas-auth` 服务已启动
- `atlas-system` 服务已启动并可访问

**流程**:
1. 用户在登录页面输入用户名和密码
2. 前端应用发送登录请求到 `atlas-auth` 服务
3. `atlas-auth` 服务通过 `atlas-system-api` 查询用户信息
4. `atlas-auth` 服务验证用户密码
5. `atlas-auth` 服务通过 `atlas-system-api` 查询用户权限和角色
6. `atlas-auth` 服务生成 Token（JWT 格式）
7. `atlas-auth` 服务将用户会话信息存储到 Redis
8. `atlas-auth` 服务返回 Token 给前端

**预期结果**: 
- 用户成功登录，获得有效的 Token
- Token 包含用户ID、用户名、角色、权限等信息
- 用户会话信息已存储到 Redis

### 场景 2: 用户登出

**角色**: 用户（前端应用）

**前置条件**: 
- 用户已登录，持有有效的 Token
- `atlas-auth` 服务已启动

**流程**:
1. 用户点击登出按钮
2. 前端应用发送登出请求到 `atlas-auth` 服务（携带 Token）
3. `atlas-auth` 服务验证 Token 有效性
4. `atlas-auth` 服务将 Token 加入黑名单（存储到 Redis）
5. `atlas-auth` 服务清除用户会话信息（从 Redis 删除）
6. `atlas-auth` 服务返回登出成功响应

**预期结果**: 
- Token 已加入黑名单，无法再使用
- 用户会话信息已清除
- 用户成功登出

### 场景 3: Gateway Token 校验（JWT 公钥方式）

**角色**: Gateway

**前置条件**: 
- Gateway 已配置使用 JWT 公钥验证方式
- `atlas-auth` 服务已启动并暴露 JWT 公钥接口
- 用户已登录，请求携带有效的 Token

**流程**:
1. Gateway 接收到用户请求，提取 Token
2. Gateway 调用 `atlas-auth` 服务的 JWT 公钥接口获取公钥（支持缓存）
3. Gateway 使用公钥验证 Token 的签名
4. Gateway 验证 Token 的过期时间
5. Gateway 检查 Token 是否在黑名单中（可选，通过 Redis 查询）
6. Gateway 根据验证结果决定是否放行请求

**预期结果**: 
- 有效 Token 的请求被放行（返回 200）
- 无效 Token 的请求被拒绝（返回 401）
- 无权限的请求被拒绝（返回 403）

### 场景 4: Gateway Token 校验（Introspection 接口方式）

**角色**: Gateway

**前置条件**: 
- Gateway 已配置使用 Introspection 接口方式
- `atlas-auth` 服务已启动并暴露 Introspection 接口
- 用户已登录，请求携带有效的 Token

**流程**:
1. Gateway 接收到用户请求，提取 Token
2. Gateway 调用 `atlas-auth` 服务的 Introspection 接口验证 Token
3. `atlas-auth` 服务验证 Token 的有效性（签名、过期时间、黑名单）
4. `atlas-auth` 服务返回 Token 验证结果和用户信息
5. Gateway 根据验证结果决定是否放行请求

**预期结果**: 
- 有效 Token 的请求被放行（返回 200）
- 无效 Token 的请求被拒绝（返回 401）
- 无权限的请求被拒绝（返回 403）

### 场景 5: Gateway 校验方式切换

**角色**: 系统管理员

**前置条件**: 
- `atlas-auth` 服务已启动，同时提供两种校验方式
- Gateway 已配置并运行

**流程**:
1. 系统管理员通过配置中心（Nacos）修改 Gateway 的 Token 校验方式配置
2. Gateway 检测到配置变更
3. Gateway 根据新配置切换到对应的校验方式（JWT 公钥或 Introspection）
4. Gateway 继续处理后续请求，使用新的校验方式

**预期结果**: 
- Gateway 能够动态切换校验方式，无需重启
- 切换后 Gateway 使用新的校验方式验证 Token
- 切换过程不影响正在处理的请求

### 场景 6: 下游服务获取登录用户上下文

**角色**: 下游业务服务（如 `atlas-system`）

**前置条件**: 
- 用户已登录，请求携带有效的 Token
- Gateway 已验证 Token 并放行请求
- 下游服务已集成 `atlas-common-feature-security` 模块

**流程**:
1. 下游服务接收到已通过 Gateway 验证的请求
2. 下游服务从请求头中提取 Token
3. 下游服务通过 `SecurityContext` 获取 `LoginUser` 信息
4. 下游服务使用 `LoginUser` 信息进行业务处理（如获取用户ID、检查权限等）

**预期结果**: 
- 下游服务能够获取到当前登录用户的完整信息
- `LoginUser` 包含用户ID、用户名、角色、权限等信息
- 下游服务可以基于用户信息进行业务逻辑处理

## 功能需求

### FR1: 用户登录功能

**需求描述**: 提供用户登录接口，验证用户身份并签发 Token。

**功能要求**:
- 接收用户名和密码作为登录凭证
- 通过 `atlas-system-api` 的 `UserQueryApi` 查询用户信息
- 验证用户密码（密码加密存储，需要验证加密后的密码）
- 验证用户状态（用户必须为激活状态）
- 通过 `atlas-system-api` 的 `PermissionQueryApi` 查询用户权限和角色
- 生成 JWT Token，包含用户ID、用户名、角色、权限等信息
- 将用户会话信息存储到 Redis（Key: `session:{userId}`, Value: 会话信息）
- 返回 Token 和用户基本信息给前端

**验收标准**:
- ✅ 用户使用正确的用户名和密码可以成功登录
- ✅ 用户使用错误的密码登录失败，返回错误信息
- ✅ 非激活状态的用户无法登录
- ✅ 登录成功后返回有效的 Token
- ✅ Token 包含用户ID、用户名、角色、权限等信息
- ✅ 用户会话信息已存储到 Redis

### FR2: 用户登出功能

**需求描述**: 提供用户登出接口，使 Token 失效并清除会话。

**功能要求**:
- 接收 Token 作为登出凭证
- 验证 Token 的有效性
- 将 Token 加入黑名单（存储到 Redis，Key: `token:blacklist:{tokenId}`, Value: Token 信息，设置过期时间）
- 清除用户会话信息（从 Redis 删除 `session:{userId}`）
- 返回登出成功响应

**验收标准**:
- ✅ 用户使用有效 Token 可以成功登出
- ✅ 登出后 Token 已加入黑名单，无法再使用
- ✅ 登出后用户会话信息已清除
- ✅ 使用已登出的 Token 进行请求会被拒绝

### FR3: Token 签发功能

**需求描述**: 生成包含用户信息的 JWT Token。

**功能要求**:
- 使用 JWT 标准生成 Token
- Token 包含以下信息：
  - 用户ID（`userId`）
  - 用户名（`username`）
  - 角色列表（`roles`）
  - 权限列表（`permissions`）
  - 签发时间（`iat`）
  - 过期时间（`exp`）
  - Token ID（`jti`，用于黑名单管理）
- Token 使用私钥签名
- Token 过期时间可配置（默认 2 小时）
- 支持 Refresh Token（可选，用于 Token 刷新）

**验收标准**:
- ✅ Token 格式符合 JWT 标准
- ✅ Token 包含所有必需的用户信息
- ✅ Token 签名有效，可以被公钥验证
- ✅ Token 过期时间符合配置要求

### FR4: Token 校验功能

**需求描述**: 验证 Token 的有效性，包括签名、过期时间、黑名单检查。

**功能要求**:
- 验证 Token 的格式（是否符合 JWT 标准）
- 验证 Token 的签名（使用公钥验证）
- 验证 Token 的过期时间
- 检查 Token 是否在黑名单中（查询 Redis）
- 返回 Token 验证结果和用户信息（如果有效）

**验收标准**:
- ✅ 有效 Token 验证通过
- ✅ 过期 Token 验证失败
- ✅ 签名无效的 Token 验证失败
- ✅ 黑名单中的 Token 验证失败
- ✅ 验证通过时返回用户信息

### FR5: Redis 会话存储

**需求描述**: 使用 Redis 存储用户会话信息和 Token 黑名单。

**功能要求**:
- 存储用户会话信息（Key: `session:{userId}`, Value: 会话信息 JSON，包含 Token、登录时间等）
- 存储 Token 黑名单（Key: `token:blacklist:{tokenId}`, Value: Token 信息，设置过期时间等于 Token 过期时间）
- 会话信息设置过期时间（与 Token 过期时间一致）
- 支持会话信息的查询、更新、删除操作

**验收标准**:
- ✅ 登录时用户会话信息成功存储到 Redis
- ✅ 登出时用户会话信息成功从 Redis 删除
- ✅ 登出时 Token 成功加入黑名单
- ✅ 黑名单中的 Token 在过期后自动清除
- ✅ 会话信息在过期后自动清除

### FR6: 集成 atlas-system-api

**需求描述**: 通过 `atlas-system-api` 获取用户基础信息和权限。

**功能要求**:
- 使用 `UserQueryApi` 查询用户信息（根据用户名或用户ID）
- 使用 `PermissionQueryApi` 查询用户权限和角色
- 处理 API 调用失败的情况（用户不存在、服务不可用等）
- 缓存用户信息（可选，减少 API 调用）

**验收标准**:
- ✅ 登录时能够成功查询用户信息
- ✅ 登录时能够成功查询用户权限和角色
- ✅ 用户不存在时返回适当的错误信息
- ✅ 服务不可用时返回适当的错误信息

### FR7: Gateway Token 校验支持（JWT 公钥方式）

**需求描述**: 为 Gateway 提供 JWT 公钥，支持 Gateway 自主验证 Token。

**功能要求**:
- 提供 JWT 公钥获取接口（GET `/api/v1/auth/public-key`）
- 公钥格式支持 JWK（JSON Web Key）或 PEM 格式
- Gateway 可以定期获取公钥（支持缓存）
- 公钥接口无需认证（公开接口）
- 支持公钥轮换（提供密钥版本号，Gateway 可以获取最新公钥）

**验收标准**:
- ✅ Gateway 能够成功获取 JWT 公钥
- ✅ Gateway 使用公钥能够验证 Token 签名
- ✅ 公钥格式符合标准（JWK 或 PEM）
- ✅ 支持公钥轮换，Gateway 可以获取最新公钥

### FR8: Gateway Token 校验支持（Introspection 接口方式）

**需求描述**: 为 Gateway 提供 Token Introspection 接口，Gateway 通过调用接口验证 Token。

**功能要求**:
- 提供 Token Introspection 接口（POST `/api/v1/auth/introspect`）
- 接口接收 Token 作为参数
- 接口返回 Token 验证结果和用户信息（如果有效）
- 接口需要认证（防止滥用，使用服务间认证）
- 接口响应时间要求低（< 100ms，支持缓存）
- 支持批量验证（可选，提高性能）

**验收标准**:
- ✅ Gateway 能够成功调用 Introspection 接口
- ✅ 接口能够正确验证 Token 并返回结果
- ✅ 接口响应时间满足要求（< 100ms）
- ✅ 接口支持服务间认证
- ✅ Gateway 可以通过配置选择使用哪种校验方式

### FR9: LoginUser 上下文提供

**需求描述**: 为下游服务提供 `LoginUser` 上下文信息。

**功能要求**:
- 实现 `SecurityContext` 接口，提供 `LoginUser` 信息
- 从请求头中提取 Token
- 解析 Token 获取用户信息
- 将用户信息封装为 `LoginUser` 对象
- 通过 `SecurityContextHolder` 提供全局访问

**验收标准**:
- ✅ 下游服务能够通过 `SecurityContext` 获取 `LoginUser`
- ✅ `LoginUser` 包含完整的用户信息（用户ID、用户名、角色、权限）
- ✅ 未登录时 `SecurityContext.getLoginUser()` 返回 null
- ✅ 已登录时 `SecurityContext.isAuthenticated()` 返回 true

## API 设计

### 接口列表

| 方法 | 路径 | 描述 | 请求体 | 响应体 | 认证要求 |
|------|------|------|--------|--------|----------|
| POST | `/api/v1/auth/login` | 用户登录 | `LoginRequestVO` | `Result<LoginResponseVO>` | 无需认证 |
| POST | `/api/v1/auth/logout` | 用户登出 | - | `Result<Void>` | 需要 Token |
| GET | `/api/v1/auth/public-key` | 获取 JWT 公钥 | - | `Result<PublicKeyResponseVO>` | 无需认证 |
| POST | `/api/v1/auth/introspect` | Token 验证（Introspection） | `IntrospectRequestVO` | `Result<IntrospectResponseVO>` | 服务间认证 |

### 请求/响应示例

#### 登录接口

**请求**:
```json
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

**响应**:
```json
{
  "code": "000000",
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "user": {
      "userId": 1,
      "username": "admin",
      "nickname": "管理员",
      "email": "admin@example.com"
    }
  },
  "timestamp": "2025-01-06T10:00:00Z",
  "traceId": "abc123"
}
```

#### 登出接口

**请求**:
```json
POST /api/v1/auth/logout
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

**响应**:
```json
{
  "code": "000000",
  "message": "登出成功",
  "data": null,
  "timestamp": "2025-01-06T10:00:00Z",
  "traceId": "abc123"
}
```

#### 获取公钥接口

**请求**:
```json
GET /api/v1/auth/public-key
```

**响应**:
```json
{
  "code": "000000",
  "message": "成功",
  "data": {
    "algorithm": "RS256",
    "publicKey": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...\n-----END PUBLIC KEY-----",
    "keyId": "key-2025-01-06"
  },
  "timestamp": "2025-01-06T10:00:00Z",
  "traceId": "abc123"
}
```

#### Introspection 接口

**请求**:
```json
POST /api/v1/auth/introspect
Content-Type: application/json
Authorization: Bearer <service-token>

{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**响应**:
```json
{
  "code": "000000",
  "message": "成功",
  "data": {
    "active": true,
    "userId": 1,
    "username": "admin",
    "roles": ["admin", "user"],
    "permissions": ["user:read", "user:write"],
    "expiresAt": 1704542400
  },
  "timestamp": "2025-01-06T10:00:00Z",
  "traceId": "abc123"
}
```

## 数据模型

### 核心实体

#### LoginRequestVO
- `username` (String, 必填): 用户名
- `password` (String, 必填): 密码

#### LoginResponseVO
- `token` (String): JWT Token
- `tokenType` (String): Token 类型（固定为 "Bearer"）
- `expiresIn` (Long): Token 过期时间（秒）
- `user` (UserVO): 用户基本信息

#### UserVO
- `userId` (Long): 用户ID
- `username` (String): 用户名
- `nickname` (String): 昵称
- `email` (String): 邮箱

#### PublicKeyResponseVO
- `algorithm` (String): 算法（如 "RS256"）
- `publicKey` (String): 公钥（PEM 格式或 JWK 格式）
- `keyId` (String): 密钥ID

#### IntrospectRequestVO
- `token` (String, 必填): 待验证的 Token

#### IntrospectResponseVO
- `active` (Boolean): Token 是否有效
- `userId` (Long): 用户ID（如果有效）
- `username` (String): 用户名（如果有效）
- `roles` (List<String>): 角色列表（如果有效）
- `permissions` (List<String>): 权限列表（如果有效）
- `expiresAt` (Long): Token 过期时间戳（如果有效）

### Redis 数据结构

#### 用户会话（Session）
- **Key**: `session:{userId}`
- **Value**: JSON 格式
  ```json
  {
    "userId": 1,
    "username": "admin",
    "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "loginTime": "2025-01-06T10:00:00Z",
    "expiresAt": 1704542400
  }
  ```
- **过期时间**: 与 Token 过期时间一致

#### Token 黑名单（Blacklist）
- **Key**: `token:blacklist:{tokenId}`（tokenId 从 JWT 的 `jti` 字段获取）
- **Value**: JSON 格式
  ```json
  {
    "tokenId": "jti-123456",
    "userId": 1,
    "expiresAt": 1704542400
  }
  ```
- **过期时间**: 与 Token 过期时间一致

## 业务逻辑

### 登录流程

1. 接收登录请求（用户名、密码）
2. 通过 `atlas-system-api` 的 `UserQueryApi.getUserByUsername()` 查询用户信息
3. 验证用户是否存在
4. 验证用户状态（必须为激活状态）
5. 验证用户密码（使用密码加密算法验证）
6. 通过 `atlas-system-api` 的 `PermissionQueryApi.getUserAuthorities()` 查询用户权限和角色
7. 生成 JWT Token（包含用户ID、用户名、角色、权限等信息）
8. 将用户会话信息存储到 Redis
9. 返回 Token 和用户基本信息

### 登出流程

1. 接收登出请求（从请求头提取 Token）
2. 解析 Token 获取用户ID和 Token ID
3. 验证 Token 有效性
4. 将 Token 加入黑名单（存储到 Redis，设置过期时间）
5. 清除用户会话信息（从 Redis 删除）
6. 返回登出成功响应

### Token 校验流程

1. 接收 Token
2. 验证 Token 格式（是否符合 JWT 标准）
3. 验证 Token 签名（使用公钥验证）
4. 验证 Token 过期时间
5. 检查 Token 是否在黑名单中（查询 Redis）
6. 如果验证通过，返回用户信息；否则返回验证失败

### Gateway 校验方式选择

Gateway 支持两种 Token 校验方式，通过配置选择使用哪种方式：

- **JWT 公钥方式**: Gateway 获取公钥后自主验证 Token，性能好，但需要处理公钥轮换
- **Introspection 接口方式**: Gateway 调用接口验证 Token，实现简单，但增加网络调用

**实现要求**:
- `atlas-auth` 服务同时提供两种校验方式（JWT 公钥接口和 Introspection 接口）
- Gateway 通过配置选择使用哪种方式（默认使用 JWT 公钥方式）
- 两种方式可以同时存在，Gateway 根据配置动态选择
- 配置变更后 Gateway 可以动态切换校验方式（无需重启）

**使用建议**:
- 性能要求高的场景：使用 JWT 公钥方式
- 实现简单、集中管理的场景：使用 Introspection 接口方式
- 需要灵活切换的场景：通过配置动态选择

## 异常处理

### 登录失败场景

- **用户不存在**: 返回错误码，提示"用户名或密码错误"（不暴露用户是否存在）
- **密码错误**: 返回错误码，提示"用户名或密码错误"
- **用户未激活**: 返回错误码，提示"用户未激活，请联系管理员"
- **用户已锁定**: 返回错误码，提示"用户已锁定，请联系管理员"
- **系统服务不可用**: 返回错误码，提示"系统服务暂时不可用，请稍后重试"

### Token 校验失败场景

- **Token 格式错误**: 返回 401，错误码提示"Token 格式错误"
- **Token 签名无效**: 返回 401，错误码提示"Token 签名无效"
- **Token 已过期**: 返回 401，错误码提示"Token 已过期"
- **Token 在黑名单中**: 返回 401，错误码提示"Token 已失效"
- **Token 缺失**: 返回 401，错误码提示"未提供 Token"

### 服务间调用失败场景

- **atlas-system-api 调用失败**: 记录错误日志，返回错误码，提示"系统服务暂时不可用"
- **Redis 连接失败**: 记录错误日志，返回错误码，提示"系统服务暂时不可用"

## 测试要求

### 单元测试

- 单元测试覆盖率 ≥ 70%
- 覆盖所有核心业务逻辑（登录、登出、Token 签发、Token 校验）
- 覆盖异常场景处理

### 集成测试

- 测试登录流程（包括与 `atlas-system-api` 的集成）
- 测试登出流程（包括 Redis 黑名单操作）
- 测试 Token 校验流程
- 测试 Gateway 集成（JWT 公钥方式或 Introspection 接口方式）

### API 测试

- 测试所有接口的正常流程
- 测试所有接口的异常流程
- 测试 Token 的有效期和黑名单功能
- 测试并发登录和登出场景

### 性能测试

- 登录接口响应时间 < 500ms
- Token 校验接口响应时间 < 100ms（Introspection 方式）
- 支持 1000+ 并发登录请求
- Redis 操作响应时间 < 10ms

## 实现注意事项

- [ ] 检查是否有可复用的公共方法（如 `atlas-common-feature-security` 的 `LoginUser`、`SecurityContext`）
- [ ] 确保所有类和方法添加中文注释
- [ ] 遵循 RESTful 设计规范
- [ ] 使用统一的异常处理机制（`atlas-common-feature-core` 的异常处理）
- [ ] 使用统一的响应格式（`Result<T>`）
- [ ] 遵循包结构规范（业务模块按业务模块再按技术分层组织）
- [ ] 所有对象类型（DTO、VO、Entity、BO、枚举）放在 `model` 包下
- [ ] 密码加密使用安全的加密算法（如 BCrypt）
- [ ] JWT Token 使用 RSA 算法签名（支持公钥/私钥对）
- [ ] Redis 操作使用连接池，避免连接泄漏
- [ ] 实现 Token 刷新机制（可选）
- [ ] 实现登录失败次数限制（防止暴力破解）
- [ ] 实现 Token 轮换机制（定期更换密钥对）

## 假设

1. **用户密码加密**: 假设用户密码在 `atlas-system` 服务中已加密存储，`atlas-auth` 服务需要获取加密后的密码进行验证，或 `atlas-system-api` 提供密码验证接口。
2. **Token 过期时间**: 默认 Token 过期时间为 2 小时，可通过配置调整。
3. **Redis 配置**: Redis 连接信息通过配置中心（Nacos）配置，支持集群模式。
4. **Gateway 集成**: Gateway 已实现 `TokenValidator` 接口，`atlas-auth` 服务需要提供相应的校验能力。Gateway 支持通过配置选择校验方式（JWT 公钥或 Introspection）。
5. **服务间认证**: Introspection 接口需要服务间认证，使用服务密钥或证书进行认证。
6. **校验方式配置**: Gateway 通过配置中心（Nacos）配置 Token 校验方式，支持动态切换。

## 成功标准

1. **功能完整性**: 
   - 用户能够成功登录并获得 Token
   - 用户能够成功登出并使 Token 失效
   - Gateway 能够基于 Token 进行 401/403 判断
   - 下游服务能够获取 `LoginUser` 上下文

2. **性能指标**:
   - 登录接口响应时间 < 500ms（P95）
   - Token 校验响应时间 < 100ms（P95，Introspection 方式）
   - 支持 1000+ 并发登录请求

3. **可靠性指标**:
   - 登录成功率 ≥ 99.9%
   - Token 校验准确率 100%（无误判）
   - Redis 操作成功率 ≥ 99.9%

4. **安全性指标**:
   - 密码验证失败后不暴露用户是否存在
   - Token 签名使用强加密算法（RSA 256）
   - 黑名单机制有效防止已登出 Token 的使用
   - 支持 Token 过期自动失效

5. **集成指标**:
   - 与 `atlas-system-api` 集成成功，能够获取用户信息和权限
   - 与 Gateway 集成成功，Gateway 能够验证 Token
   - 与下游服务集成成功，下游服务能够获取 `LoginUser` 上下文
