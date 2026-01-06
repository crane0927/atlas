# API 契约定义

## 概述

本文档定义了 `atlas-auth` 服务的 API 接口契约，包括登录、登出、Token 校验等接口。这些接口遵循 RESTful 设计规范，使用统一的响应格式 `Result<T>`。

## 接口列表

### 1. 用户登录

**接口名称**: `login`

**HTTP 方法**: `POST`

**路径**: `/api/v1/auth/login`

**描述**: 用户登录接口，验证用户身份并签发 Token。

**请求头**:
```
Content-Type: application/json
```

**请求体**: `LoginRequestVO`
```json
{
  "username": "admin",
  "password": "password123"
}
```

**响应体**: `Result<LoginResponseVO>`
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

**错误响应**:
- `400`: 参数错误（用户名或密码为空）
- `401`: 用户名或密码错误
- `403`: 用户未激活或已锁定
- `500`: 系统服务不可用

**示例**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

### 2. 用户登出

**接口名称**: `logout`

**HTTP 方法**: `POST`

**路径**: `/api/v1/auth/logout`

**描述**: 用户登出接口，使 Token 失效并清除会话。

**请求头**:
```
Authorization: Bearer {token}
```

**请求体**: 无

**响应体**: `Result<Void>`
```json
{
  "code": "000000",
  "message": "登出成功",
  "data": null,
  "timestamp": "2025-01-06T10:00:00Z",
  "traceId": "abc123"
}
```

**错误响应**:
- `401`: Token 无效或缺失
- `500`: 系统服务不可用

**示例**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 3. 获取 JWT 公钥

**接口名称**: `getPublicKey`

**HTTP 方法**: `GET`

**路径**: `/api/v1/auth/public-key`

**描述**: 获取 JWT 公钥，供 Gateway 验证 Token 签名。

**请求头**: 无

**请求参数**: 无

**响应体**: `Result<PublicKeyResponseVO>`
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

**错误响应**:
- `500`: 系统服务不可用

**示例**:
```bash
curl -X GET http://localhost:8080/api/v1/auth/public-key
```

### 4. Token Introspection

**接口名称**: `introspect`

**HTTP 方法**: `POST`

**路径**: `/api/v1/auth/introspect`

**描述**: Token 验证接口，供 Gateway 验证 Token 有效性。

**请求头**:
```
Content-Type: application/json
Authorization: Bearer {service-token}
```

**请求体**: `IntrospectRequestVO`
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**响应体**: `Result<IntrospectResponseVO>`
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

**错误响应**:
- `400`: 参数错误（Token 为空）
- `401`: 服务间认证失败
- `500`: 系统服务不可用

**示例**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/introspect \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {service-token}" \
  -d '{
    "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

## 错误码定义

### 通用错误码

| 错误码 | HTTP 状态码 | 说明 |
|--------|------------|------|
| 000000 | 200 | 成功 |
| 400001 | 400 | 参数错误 |
| 401001 | 401 | 未认证 |
| 401002 | 401 | Token 无效 |
| 401003 | 401 | Token 已过期 |
| 401004 | 401 | Token 已失效（黑名单） |
| 403001 | 403 | 用户未激活 |
| 403002 | 403 | 用户已锁定 |
| 500001 | 500 | 系统服务不可用 |

### 业务错误码

| 错误码 | HTTP 状态码 | 说明 |
|--------|------------|------|
| 010001 | 401 | 用户名或密码错误 |
| 010002 | 401 | 用户不存在 |
| 010003 | 403 | 用户未激活 |
| 010004 | 403 | 用户已锁定 |
| 010005 | 500 | 系统服务调用失败 |

## 接口设计原则

### RESTful 设计

- 使用 HTTP 方法表示操作（POST、GET）
- 使用路径表示资源（`/api/v1/auth/login`）
- 使用状态码表示结果（200、400、401、403、500）

### 统一响应格式

- 所有接口使用 `Result<T>` 包装响应数据
- 成功响应：`code = "000000"`，`data` 包含业务数据
- 失败响应：`code` 为错误码，`message` 为错误消息

### 认证要求

- 登录接口：无需认证
- 登出接口：需要 Bearer Token
- 公钥接口：无需认证（公开接口）
- Introspection 接口：需要服务间认证

### 性能要求

- 登录接口响应时间 < 500ms（P95）
- Token 校验接口响应时间 < 100ms（P95）
- 支持 1000+ 并发请求

## 接口版本管理

当前版本：`v1`

版本管理策略：
- 使用路径版本：`/api/v1/auth/*`
- 破坏性变更通过新版本路径实现（如 `/api/v2/auth/*`）
- 非破坏性变更在现有版本中实现（向后兼容）

## 接口兼容性规则

1. **新增字段**: 响应对象新增字段必须可空或提供默认值
2. **删除字段**: 禁止删除现有字段，必须通过新版本实现
3. **修改字段类型**: 禁止修改现有字段类型，必须通过新版本实现
4. **新增接口**: 可以在现有版本中新增接口
5. **删除接口**: 禁止删除现有接口，必须通过新版本实现

## 接口测试

### 测试工具

- Postman
- curl
- HTTPie

### 测试场景

1. **正常流程**:
   - 登录成功
   - 登出成功
   - 获取公钥成功
   - Token 校验成功

2. **异常流程**:
   - 用户名或密码错误
   - Token 无效
   - Token 已过期
   - Token 在黑名单中
   - 用户未激活

3. **性能测试**:
   - 并发登录
   - 并发 Token 校验
   - 高并发场景

## 接口文档

### OpenAPI/Swagger

接口文档使用 OpenAPI 3.0 规范，可以通过 Swagger UI 查看和测试。

访问地址：`http://localhost:8080/swagger-ui.html`

### 文档生成

使用 SpringDoc OpenAPI 自动生成接口文档。

