# Atlas Auth Service

## 模块简介

`atlas-auth` 是 Atlas 项目的认证服务模块，提供用户登录、登出、Token 签发与校验功能。服务通过 `atlas-system-api` 获取用户基础信息和权限，使用 Redis 存储会话和 Token 黑名单，并为 Gateway 提供 Token 校验能力（支持 JWT 公钥验证或 Introspection 接口两种方式）。

## 主要功能

### 1. 用户认证

- **用户登录**: 验证用户身份并签发 JWT Token
- **用户登出**: 使 Token 失效并清除会话
- **密码验证**: 使用 BCrypt 算法验证密码

### 2. Token 管理

- **Token 签发**: 生成包含用户信息的 JWT Token（RS256 算法）
- **Token 验证**: 验证 Token 的签名、过期时间和黑名单状态
- **Token 黑名单**: 使用 Redis 管理 Token 黑名单

### 3. Gateway 集成

- **JWT 公钥接口**: 为 Gateway 提供公钥，支持 Gateway 自主验证 Token
- **Introspection 接口**: 为 Gateway 提供 Token 验证接口
- **动态切换**: 支持通过配置动态切换校验方式

### 4. 下游服务上下文

- **SecurityContext**: 为下游服务提供 `LoginUser` 上下文信息
- **ThreadLocal 存储**: 使用 ThreadLocal 存储用户信息，线程安全
- **自动清理**: 请求结束时自动清理上下文，避免内存泄漏

## 快速开始

### 添加依赖

在子模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.atlas</groupId>
    <artifactId>atlas-auth</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 配置

在 `application.yml` 中配置：

```yaml
spring:
  application:
    name: atlas-auth
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER_ADDR:localhost:8848}
        namespace: ${NACOS_NAMESPACE:dev}
        group: ${NACOS_GROUP:DEV_GROUP}
      config:
        server-addr: ${NACOS_SERVER_ADDR:localhost:8848}
        namespace: ${NACOS_NAMESPACE:dev}
        group: ${NACOS_GROUP:DEV_GROUP}
        file-extension: yaml

# Atlas Auth 配置（在 Nacos Config 中配置）
atlas:
  auth:
    jwt:
      private-key: |
        -----BEGIN PRIVATE KEY-----
        MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC...
        -----END PRIVATE KEY-----
      public-key: |
        -----BEGIN PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...
        -----END PUBLIC KEY-----
      key-id: key-2025-01-06
      expire: 7200
      algorithm: RS256
```

### 使用示例

#### 1. 用户登录

```java
// 前端调用
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}

// 响应
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
  }
}
```

#### 2. 用户登出

```java
// 前端调用
POST /api/v1/auth/logout
Authorization: Bearer {token}
```

#### 3. Gateway 获取公钥

```java
// Gateway 调用
GET /api/v1/auth/public-key

// 响应
{
  "code": "000000",
  "message": "操作成功",
  "data": {
    "algorithm": "RS256",
    "publicKey": "-----BEGIN PUBLIC KEY-----\n...\n-----END PUBLIC KEY-----",
    "keyId": "key-2025-01-06"
  }
}
```

#### 4. Gateway Token Introspection

```java
// Gateway 调用
POST /api/v1/auth/introspect
Content-Type: application/json

{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}

// 响应（Token 有效）
{
  "code": "000000",
  "message": "操作成功",
  "data": {
    "active": true,
    "userId": 1,
    "username": "admin",
    "roles": ["admin", "user"],
    "permissions": ["user:read", "user:write"],
    "expiresAt": 1704549600
  }
}
```

#### 5. 下游服务获取登录用户上下文

```java
package com.atlas.example.controller;

import com.atlas.auth.context.AuthSecurityContextHolder;
import com.atlas.common.feature.security.user.LoginUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/example")
public class ExampleController {

  @GetMapping("/current-user")
  public String getCurrentUser() {
    // 获取当前登录用户
    LoginUser user = AuthSecurityContextHolder.getLoginUser();
    
    if (user != null) {
      return "当前用户: " + user.getUsername() + " (ID: " + user.getUserId() + ")";
    } else {
      return "未登录";
    }
  }

  @GetMapping("/check-auth")
  public String checkAuth() {
    // 判断是否已认证
    if (AuthSecurityContextHolder.isAuthenticated()) {
      LoginUser user = AuthSecurityContextHolder.getLoginUser();
      return "已认证: " + user.getUsername();
    } else {
      return "未认证";
    }
  }

  @GetMapping("/check-role")
  public String checkRole() {
    LoginUser user = AuthSecurityContextHolder.getLoginUser();
    if (user != null && user.hasRole("admin")) {
      return "用户拥有 admin 角色";
    } else {
      return "用户没有 admin 角色";
    }
  }

  @GetMapping("/check-permission")
  public String checkPermission() {
    LoginUser user = AuthSecurityContextHolder.getLoginUser();
    if (user != null && user.hasPermission("user:write")) {
      return "用户拥有 user:write 权限";
    } else {
      return "用户没有 user:write 权限";
    }
  }
}
```

## API 接口

### 1. 用户登录

- **接口**: `POST /api/v1/auth/login`
- **请求体**: `LoginRequestVO`
- **响应**: `Result<LoginResponseVO>`

### 2. 用户登出

- **接口**: `POST /api/v1/auth/logout`
- **请求头**: `Authorization: Bearer {token}`
- **响应**: `Result<Void>`

### 3. 获取 JWT 公钥

- **接口**: `GET /api/v1/auth/public-key`
- **响应**: `Result<PublicKeyResponseVO>`

### 4. Token Introspection

- **接口**: `POST /api/v1/auth/introspect`
- **请求体**: `IntrospectRequestVO`
- **响应**: `Result<IntrospectResponseVO>`

## 依赖关系

- `atlas-common-feature-core`: 统一响应格式、异常处理
- `atlas-common-feature-security`: `LoginUser`、`SecurityContext` 接口
- `atlas-common-infra-redis`: Redis 操作工具
- `atlas-service-api/atlas-system-api`: 用户和权限查询接口
- `spring-cloud-starter-openfeign`: Feign 客户端
- `io.jsonwebtoken:jjwt`: JWT 库

## 配置说明

### JWT 配置

在 Nacos Config 中配置 JWT 密钥对：

```yaml
atlas:
  auth:
    jwt:
      private-key: |
        -----BEGIN PRIVATE KEY-----
        MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC...
        -----END PRIVATE KEY-----
      public-key: |
        -----BEGIN PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...
        -----END PUBLIC KEY-----
      key-id: key-2025-01-06
      expire: 7200  # Token 过期时间（秒）
      algorithm: RS256
```

### Redis 配置

在 `application.yml` 中配置 Redis 连接：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password:  # 如果有密码，填写密码
      database: 0
      timeout: 3000ms
```

## 注意事项

1. **密码验证**: 当前实现中 `AuthServiceImpl.getStoredPassword()` 方法需要根据实际架构调整。建议在 `atlas-system` 服务中提供密码验证接口。

2. **SecurityContextHolder**: 下游服务需要使用 `AuthSecurityContextHolder.getLoginUser()` 而不是 `SecurityContextHolder.getLoginUser()`，因为 `SecurityContextHolder` 是抽象类，需要具体实现。

3. **Token 格式**: Token 必须通过 `Authorization: Bearer {token}` 请求头传递。

4. **服务依赖**: 服务依赖 `atlas-system` 服务，确保该服务已启动并注册到服务注册中心。

## 参考资源

- [API 契约定义](../../specs/010-auth-service/contracts/README.md)
- [数据模型定义](../../specs/010-auth-service/data-model.md)
- [快速开始指南](../../specs/010-auth-service/quickstart.md)
- [技术调研文档](../../specs/010-auth-service/research.md)

