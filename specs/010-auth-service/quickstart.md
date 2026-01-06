# 快速开始指南

## 概述

本文档提供 `atlas-auth` 服务的快速开始指南，包括环境准备、配置说明、启动步骤和使用示例。

## 环境要求

### 基础环境

- JDK 21
- Maven 3.8+
- Redis 6.0+（用于会话存储和黑名单）
- Nacos 2.0+（用于配置管理和服务注册）

### 依赖服务

- `atlas-system` 服务（提供用户和权限查询接口）
- `atlas-gateway` 服务（可选，用于 Gateway 集成）

## 配置说明

### Nacos 配置

**DataId**: `atlas-auth-dev.yaml`  
**Group**: `DEV_GROUP`

```yaml
atlas:
  auth:
    # JWT 配置
    jwt:
      # RSA 私钥（PEM 格式）
      private-key: |
        -----BEGIN PRIVATE KEY-----
        MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC...
        -----END PRIVATE KEY-----
      # RSA 公钥（PEM 格式）
      public-key: |
        -----BEGIN PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...
        -----END PUBLIC KEY-----
      # 密钥ID
      key-id: key-2025-01-06
      # Token 过期时间（秒，默认 7200）
      expire: 7200
      # 算法（固定为 RS256）
      algorithm: RS256
    
    # Redis 配置（复用 atlas-common-infra-redis）
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0
      timeout: 3000
    
    # 服务配置
    service:
      # 服务名称
      name: atlas-auth
      # 服务端口
      port: 8080
```

### 应用配置

**application.yml**:
```yaml
spring:
  application:
    name: atlas-auth
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: dev
        group: DEV_GROUP
      config:
        server-addr: localhost:8848
        namespace: dev
        group: DEV_GROUP
        file-extension: yaml
        shared-configs:
          - data-id: atlas-common.yaml
            group: COMMON_GROUP
            refresh: true
```

## 启动步骤

### 1. 启动依赖服务

```bash
# 启动 Redis
redis-server

# 启动 Nacos
# 参考 Nacos 官方文档

# 启动 atlas-system 服务
cd atlas-system
mvn spring-boot:run
```

### 2. 配置 Nacos

1. 登录 Nacos 控制台（http://localhost:8848/nacos）
2. 创建配置：`atlas-auth-dev.yaml`
3. 配置内容参考上面的配置示例
4. 确保 `atlas-system-api` 的配置已存在

### 3. 启动 atlas-auth 服务

```bash
cd atlas-auth
mvn spring-boot:run
```

### 4. 验证服务启动

```bash
# 检查服务注册
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=atlas-auth

# 检查健康状态
curl http://localhost:8080/actuator/health
```

## 使用示例

### 1. 用户登录

**请求**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
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

### 2. 用户登出

**请求**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
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

### 3. 获取 JWT 公钥

**请求**:
```bash
curl -X GET http://localhost:8080/api/v1/auth/public-key
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

### 4. Token Introspection

**请求**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/introspect \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {service-token}" \
  -d '{
    "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
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

## Gateway 集成

### JWT 公钥方式

**配置** (Gateway):
```yaml
atlas:
  gateway:
    auth:
      # Token 校验方式
      validation-mode: jwt-public-key
      # 公钥获取地址
      public-key-url: http://atlas-auth/api/v1/auth/public-key
      # 公钥缓存时间（秒）
      public-key-cache-ttl: 3600
```

**实现**: Gateway 实现 `TokenValidator` 接口，使用 JWT 公钥验证 Token。

### Introspection 接口方式

**配置** (Gateway):
```yaml
atlas:
  gateway:
    auth:
      # Token 校验方式
      validation-mode: introspection
      # Introspection 接口地址
      introspection-url: http://atlas-auth/api/v1/auth/introspect
      # 服务间认证 Token
      service-token: {service-token}
```

**实现**: Gateway 实现 `TokenValidator` 接口，调用 Introspection 接口验证 Token。

## 下游服务集成

### 获取 LoginUser 上下文

**依赖**:
```xml
<dependency>
  <groupId>com.atlas</groupId>
  <artifactId>atlas-common-feature-security</artifactId>
</dependency>
```

**使用**:
```java
import com.atlas.common.feature.security.context.SecurityContext;
import com.atlas.common.feature.security.context.SecurityContextHolder;
import com.atlas.common.feature.security.user.LoginUser;

@RestController
public class UserController {
  
  @GetMapping("/api/v1/users/me")
  public Result<UserVO> getCurrentUser() {
    SecurityContext context = SecurityContextHolder.getContext();
    LoginUser loginUser = context.getLoginUser();
    
    if (loginUser == null) {
      return Result.error("401001", "未登录");
    }
    
    // 使用 loginUser 进行业务处理
    Long userId = (Long) loginUser.getUserId();
    String username = loginUser.getUsername();
    List<String> roles = loginUser.getRoles();
    List<String> permissions = loginUser.getPermissions();
    
    // ...
  }
}
```

## 常见问题

### 1. 登录失败：用户名或密码错误

**原因**: 
- 用户名或密码不正确
- 用户不存在
- 密码加密方式不匹配

**解决**:
- 检查用户名和密码是否正确
- 确认 `atlas-system` 服务的密码加密方式
- 检查 `atlas-system-api` 是否返回密码字段

### 2. Token 验证失败

**原因**:
- Token 已过期
- Token 签名无效
- Token 在黑名单中

**解决**:
- 检查 Token 是否过期（`exp` 字段）
- 确认公钥是否正确
- 检查 Token 是否已登出（黑名单）

### 3. Redis 连接失败

**原因**:
- Redis 服务未启动
- Redis 配置错误
- 网络连接问题

**解决**:
- 检查 Redis 服务是否启动
- 验证 Redis 配置（host、port、password）
- 检查网络连接

### 4. atlas-system-api 调用失败

**原因**:
- `atlas-system` 服务未启动
- 服务注册失败
- Feign 配置错误

**解决**:
- 检查 `atlas-system` 服务是否启动
- 验证服务注册（Nacos 控制台）
- 检查 Feign 客户端配置

## 开发建议

### 1. 本地开发

- 使用本地 Redis（Docker 启动）
- 使用本地 Nacos（Docker 启动）
- 使用 IDE 直接运行服务

### 2. 测试

- 使用 Postman 或 curl 测试接口
- 编写单元测试覆盖核心逻辑
- 编写集成测试验证服务间调用

### 3. 调试

- 启用 DEBUG 日志级别
- 使用 TraceId 追踪请求链路
- 检查 Redis 数据（使用 Redis CLI）

## 下一步

- 查看 [API 契约文档](./contracts/README.md) 了解接口详情
- 查看 [数据模型文档](./data-model.md) 了解数据结构
- 查看 [规划文档](./plan.md) 了解实现计划

