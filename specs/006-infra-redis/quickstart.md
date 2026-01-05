# 快速开始指南

## 概述

本指南介绍如何使用 `atlas-common-infra-redis` 模块提供的 Redis 基础设施功能，包括 Redis 序列化配置、Key 命名规范和基础缓存工具。

## 前置条件

- JDK 21
- Spring Boot 4.0.1
- Maven 3.8+
- Redis 服务器（本地或远程）
- 已安装 `atlas-common-feature-core` 模块（可选）

## 添加依赖

在子模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.atlas</groupId>
    <artifactId>atlas-common-infra-redis</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 配置 Redis 连接

在 `application.yml` 中配置 Redis 连接信息：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password:  # 如果有密码，填写密码
      database: 0
      timeout: 3000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms

# Atlas Redis 配置
atlas:
  redis:
    key-prefix: "atlas"  # Key 前缀，默认值为 "atlas"
```

## 使用示例

### 1. Redis 序列化配置

Redis 序列化配置会自动应用，无需手动配置。所有 Redis 操作都会使用统一的序列化方式。

**自动配置**:
- Key 使用 String 序列化（StringRedisSerializer）
- Value 使用 JSON 序列化（GenericJackson2JsonRedisSerializer）

**自定义配置**（可选）:

如果需要自定义序列化方式，可以创建自己的 `RedisConfig`：

```java
@Configuration
public class CustomRedisConfig {
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // 自定义序列化配置
        return template;
    }
}
```

### 2. Key 命名规范

使用 `RedisKeyBuilder` 构建符合规范的 Key：

```java
import com.atlas.common.infra.redis.key.RedisKeyBuilder;

// 构建 Key
String key = RedisKeyBuilder.builder()
    .module("user")
    .business("info")
    .id("123")
    .build();
// 结果: "atlas:user:info:123"

// 设置过期时间（可选）
RedisKeyBuilder builder = RedisKeyBuilder.builder()
    .module("user")
    .business("info")
    .id("123")
    .withTtl(3600);  // 1 小时过期
```

### 3. 基础缓存工具

使用 `CacheUtil` 进行缓存操作：

**设置缓存**:

```java
import com.atlas.common.infra.redis.util.CacheUtil;

// 设置缓存（不过期）
CacheUtil.set("atlas:user:info:123", userInfo);

// 设置缓存并指定过期时间（秒）
CacheUtil.set("atlas:user:info:123", userInfo, 3600);
```

**获取缓存**:

```java
// 获取缓存
UserInfo userInfo = CacheUtil.get("atlas:user:info:123", UserInfo.class);

// 如果缓存不存在，返回 null
if (userInfo == null) {
    // 从数据库查询
    userInfo = userService.getUserById(123L);
    // 设置缓存
    CacheUtil.set("atlas:user:info:123", userInfo, 3600);
}
```

**删除缓存**:

```java
// 删除单个缓存
CacheUtil.delete("atlas:user:info:123");

// 按模式删除缓存（删除所有 user:info:* 的缓存）
CacheUtil.deletePattern("atlas:user:info:*");
```

**检查缓存是否存在**:

```java
// 检查缓存是否存在
boolean exists = CacheUtil.exists("atlas:user:info:123");
```

**设置和查询过期时间**:

```java
// 设置缓存过期时间
CacheUtil.expire("atlas:user:info:123", 3600);

// 获取缓存剩余过期时间（秒）
long ttl = CacheUtil.getExpire("atlas:user:info:123");
// -1 表示永不过期，-2 表示 Key 不存在
```

## 完整示例

**Service 层使用缓存**:

```java
package com.atlas.system.service;

import com.atlas.common.infra.redis.key.RedisKeyBuilder;
import com.atlas.common.infra.redis.util.CacheUtil;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private static final int CACHE_EXPIRE_SECONDS = 3600;  // 1 小时
    
    public UserInfo getUserById(Long id) {
        // 构建 Key
        String key = RedisKeyBuilder.builder()
            .module("user")
            .business("info")
            .id(String.valueOf(id))
            .build();
        
        // 从缓存获取
        UserInfo userInfo = CacheUtil.get(key, UserInfo.class);
        if (userInfo != null) {
            return userInfo;
        }
        
        // 缓存不存在，从数据库查询
        userInfo = userMapper.selectById(id);
        if (userInfo != null) {
            // 设置缓存
            CacheUtil.set(key, userInfo, CACHE_EXPIRE_SECONDS);
        }
        
        return userInfo;
    }
    
    public void updateUser(UserInfo userInfo) {
        // 更新数据库
        userMapper.updateById(userInfo);
        
        // 删除缓存
        String key = RedisKeyBuilder.builder()
            .module("user")
            .business("info")
            .id(String.valueOf(userInfo.getId()))
            .build();
        CacheUtil.delete(key);
    }
    
    public void deleteUser(Long id) {
        // 删除数据库记录
        userMapper.deleteById(id);
        
        // 删除缓存
        String key = RedisKeyBuilder.builder()
            .module("user")
            .business("info")
            .id(String.valueOf(id))
            .build();
        CacheUtil.delete(key);
    }
}
```

## 配置说明

### Redis 连接配置

Redis 连接配置使用 Spring Boot 的标准配置：

```yaml
spring:
  data:
    redis:
      host: localhost      # Redis 服务器地址
      port: 6379          # Redis 服务器端口
      password:           # Redis 密码（可选）
      database: 0         # Redis 数据库索引
      timeout: 3000ms     # 连接超时时间
```

### Key 前缀配置

Key 前缀配置用于统一管理 Key 命名空间：

```yaml
atlas:
  redis:
    key-prefix: "atlas"  # Key 前缀，默认值为 "atlas"
```

## 最佳实践

### 1. Key 命名规范

- **使用 RedisKeyBuilder**: 统一使用 `RedisKeyBuilder` 构建 Key，避免手动拼接
- **模块名清晰**: 模块名应该清晰明确，便于识别和管理
- **业务标识明确**: 业务标识应该明确业务含义，避免歧义
- **唯一标识准确**: 唯一标识应该准确唯一，避免冲突

### 2. 缓存策略

- **设置过期时间**: 所有缓存都应该设置合理的过期时间，避免数据过期
- **缓存更新**: 更新数据时应该删除相关缓存，保证数据一致性
- **缓存穿透**: 对于不存在的 Key，也应该缓存 null 值，避免缓存穿透
- **缓存雪崩**: 设置随机的过期时间，避免大量缓存同时过期

### 3. 异常处理

- **统一处理**: `CacheUtil` 已经统一处理异常，业务代码无需处理
- **降级方案**: 缓存失败时应该直接查询数据库，不影响主业务流程
- **监控告警**: 监控 Redis 连接状态，及时告警

## 常见问题

### Q1: 如何自定义序列化方式？

A: 可以创建自己的 `RedisConfig`，使用 `@Primary` 注解覆盖默认配置。

### Q2: Key 前缀如何配置？

A: 在 `application.yml` 中配置 `atlas.redis.key-prefix`，默认值为 "atlas"。

### Q3: 缓存操作失败怎么办？

A: `CacheUtil` 已经统一处理异常，记录日志但不抛出异常。业务代码应该实现降级方案，缓存失败时直接查询数据库。

### Q4: 如何批量删除缓存？

A: 使用 `CacheUtil.deletePattern()` 方法，传入模式匹配的 Key（如 `"atlas:user:info:*"`）。

### Q5: 如何设置缓存过期时间？

A: 可以在设置缓存时指定过期时间，也可以使用 `CacheUtil.expire()` 方法单独设置。

## 相关文档

- [功能规格说明](spec.md)
- [技术规划文档](plan.md)
- [数据模型定义](data-model.md)
- [技术调研文档](research.md)

