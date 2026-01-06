# 数据模型文档

## 概述

本文档定义 `atlas-auth` 服务涉及的数据模型，包括 VO 对象、DTO 对象、Redis 数据结构等。

## VO 对象（View Object）

### LoginRequestVO

**用途**: 登录请求参数

**包路径**: `com.atlas.auth.model.vo.LoginRequestVO`

**字段**:
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**示例**:
```json
{
  "username": "admin",
  "password": "password123"
}
```

### LoginResponseVO

**用途**: 登录响应数据

**包路径**: `com.atlas.auth.model.vo.LoginResponseVO`

**字段**:
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| token | String | 是 | JWT Token |
| tokenType | String | 是 | Token 类型（固定为 "Bearer"） |
| expiresIn | Long | 是 | Token 过期时间（秒） |
| user | UserVO | 是 | 用户基本信息 |

**示例**:
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 7200,
  "user": {
    "userId": 1,
    "username": "admin",
    "nickname": "管理员",
    "email": "admin@example.com"
  }
}
```

### UserVO

**用途**: 用户基本信息（用于登录响应）

**包路径**: `com.atlas.auth.model.vo.UserVO`

**字段**:
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |
| username | String | 是 | 用户名 |
| nickname | String | 否 | 昵称 |
| email | String | 否 | 邮箱 |

**示例**:
```json
{
  "userId": 1,
  "username": "admin",
  "nickname": "管理员",
  "email": "admin@example.com"
}
```

### PublicKeyResponseVO

**用途**: JWT 公钥响应数据

**包路径**: `com.atlas.auth.model.vo.PublicKeyResponseVO`

**字段**:
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| algorithm | String | 是 | 算法（如 "RS256"） |
| publicKey | String | 是 | 公钥（PEM 格式或 JWK 格式） |
| keyId | String | 是 | 密钥ID |

**示例**:
```json
{
  "algorithm": "RS256",
  "publicKey": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...\n-----END PUBLIC KEY-----",
  "keyId": "key-2025-01-06"
}
```

### IntrospectRequestVO

**用途**: Token Introspection 请求参数

**包路径**: `com.atlas.auth.model.vo.IntrospectRequestVO`

**字段**:
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| token | String | 是 | 待验证的 Token |

**示例**:
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### IntrospectResponseVO

**用途**: Token Introspection 响应数据

**包路径**: `com.atlas.auth.model.vo.IntrospectResponseVO`

**字段**:
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| active | Boolean | 是 | Token 是否有效 |
| userId | Long | 否 | 用户ID（如果有效） |
| username | String | 否 | 用户名（如果有效） |
| roles | List<String> | 否 | 角色列表（如果有效） |
| permissions | List<String> | 否 | 权限列表（如果有效） |
| expiresAt | Long | 否 | Token 过期时间戳（如果有效） |

**示例**:
```json
{
  "active": true,
  "userId": 1,
  "username": "admin",
  "roles": ["admin", "user"],
  "permissions": ["user:read", "user:write"],
  "expiresAt": 1704542400
}
```

## DTO 对象（Data Transfer Object）

### TokenInfoDTO

**用途**: Token 信息（内部使用）

**包路径**: `com.atlas.auth.model.dto.TokenInfoDTO`

**字段**:
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| tokenId | String | 是 | Token ID（JWT 的 jti） |
| userId | Long | 是 | 用户ID |
| username | String | 是 | 用户名 |
| roles | List<String> | 是 | 角色列表 |
| permissions | List<String> | 是 | 权限列表 |
| issuedAt | Long | 是 | 签发时间戳 |
| expiresAt | Long | 是 | 过期时间戳 |

**示例**:
```json
{
  "tokenId": "jti-123456",
  "userId": 1,
  "username": "admin",
  "roles": ["admin", "user"],
  "permissions": ["user:read", "user:write"],
  "issuedAt": 1704542400,
  "expiresAt": 1704549600
}
```

## 枚举常量

### TokenType

**用途**: Token 类型枚举

**包路径**: `com.atlas.auth.model.enums.TokenType`

**值**:
- `BEARER`: Bearer Token（默认）

**示例**:
```java
public enum TokenType {
  BEARER
}
```

## Redis 数据结构

### 用户会话（Session）

**Key 格式**: `session:{userId}`

**类型**: String

**Value 格式**: JSON

**字段**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| userId | Long | 用户ID |
| username | String | 用户名 |
| token | String | JWT Token |
| loginTime | String | 登录时间（ISO 8601 格式） |
| expiresAt | Long | 过期时间戳 |

**过期时间**: 与 Token 过期时间一致（默认 2 小时）

**示例**:
```json
{
  "userId": 1,
  "username": "admin",
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "loginTime": "2025-01-06T10:00:00Z",
  "expiresAt": 1704549600
}
```

### Token 黑名单（Blacklist）

**Key 格式**: `token:blacklist:{tokenId}`

**类型**: String

**Value 格式**: JSON

**字段**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| tokenId | String | Token ID（JWT 的 jti） |
| userId | Long | 用户ID |
| expiresAt | Long | Token 过期时间戳 |

**过期时间**: 与 Token 过期时间一致（默认 2 小时）

**示例**:
```json
{
  "tokenId": "jti-123456",
  "userId": 1,
  "expiresAt": 1704549600
}
```

## JWT Token 结构

### Header

**字段**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| alg | String | 算法（固定为 "RS256"） |
| typ | String | 类型（固定为 "JWT"） |
| kid | String | 密钥ID |

**示例**:
```json
{
  "alg": "RS256",
  "typ": "JWT",
  "kid": "key-2025-01-06"
}
```

### Payload

**字段**:
| 字段名 | 类型 | 说明 |
|--------|------|------|
| userId | Long | 用户ID |
| username | String | 用户名 |
| roles | List<String> | 角色列表 |
| permissions | List<String> | 权限列表 |
| iat | Long | 签发时间戳（JWT 标准字段） |
| exp | Long | 过期时间戳（JWT 标准字段） |
| jti | String | Token ID（JWT 标准字段，用于黑名单管理） |

**示例**:
```json
{
  "userId": 1,
  "username": "admin",
  "roles": ["admin", "user"],
  "permissions": ["user:read", "user:write"],
  "iat": 1704542400,
  "exp": 1704549600,
  "jti": "jti-123456"
}
```

## 数据流转

### 登录流程数据流转

```
前端 → LoginRequestVO → AuthService → UserDTO (atlas-system-api)
     → UserAuthoritiesDTO (atlas-system-api)
     → TokenInfoDTO → JWT Token
     → Session (Redis)
     → LoginResponseVO → 前端
```

### 登出流程数据流转

```
前端 → Token (请求头) → AuthService → TokenInfoDTO
     → Blacklist (Redis)
     → Session (Redis, 删除)
     → Result<Void> → 前端
```

### Token 校验流程数据流转

```
Gateway/Service → Token → TokenService → TokenInfoDTO
                → Blacklist (Redis, 查询)
                → IntrospectResponseVO / 验证结果
```

## 数据验证规则

### LoginRequestVO

- `username`: 不能为空，长度 1-50 字符
- `password`: 不能为空，长度 6-100 字符

### Token 验证规则

- Token 格式必须符合 JWT 标准
- Token 签名必须有效（使用公钥验证）
- Token 必须未过期（`exp` > 当前时间）
- Token 必须不在黑名单中（查询 Redis）

## 数据关系

### 用户与会话

- 一个用户可以有多个会话（多设备登录）
- 会话 Key: `session:{userId}`，但实际存储时可能需要区分设备（可选）

### Token 与黑名单

- 一个 Token 对应一个黑名单记录
- 黑名单 Key: `token:blacklist:{tokenId}`
- Token 过期后，黑名单记录自动清除

### 用户与权限

- 用户通过 `atlas-system-api` 获取权限信息
- 权限信息存储在 JWT Token 中，减少查询
- 权限变更后，需要重新登录获取新 Token

