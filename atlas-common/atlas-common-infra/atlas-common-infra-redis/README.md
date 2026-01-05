# atlas-common-infra-redis

## 模块简介

`atlas-common-infra-redis` 是 Atlas 项目的 Redis 基础设施模块，提供统一的 Redis 序列化配置、Key 命名规范和基础缓存工具类。该模块为所有业务模块提供统一的 Redis 使用规范，确保缓存数据的一致性、可维护性和可追溯性。

## 主要功能

### 1. Redis 序列化配置

提供统一的 Redis 序列化配置，支持多种序列化方式：
- **Key 序列化**: 使用 `StringRedisSerializer`，Key 在 Redis 中可读
- **Value 序列化**: 使用 `GenericJackson2JsonRedisSerializer`，Value 以 JSON 格式存储，便于调试和维护
- **Hash 序列化**: Hash Key 和 Hash Value 使用相同的序列化方式
- **自动配置**: 配置类使用 `@Configuration` 注解，自动注册到 Spring 容器
- **自定义支持**: 支持通过配置文件自定义序列化方式

### 2. Key 命名规范

提供统一的 Redis Key 命名规范工具类 `RedisKeyBuilder`：
- **Key 格式**: `{prefix}:{module}:{business}:{id}`（如：`atlas:user:info:123`）
- **前缀配置**: 支持 Key 前缀配置，统一管理 Key 命名空间，默认值为 "atlas"
- **Builder 模式**: 提供链式调用的 Builder 模式，使用简单直观
- **TTL 支持**: 支持 Key 过期时间设置（TTL），便于缓存管理

### 3. 基础缓存工具

提供常用的缓存操作方法封装 `CacheUtil`：
- **基本操作**: 支持缓存的设置、获取、删除操作
- **过期时间**: 支持缓存过期时间设置和查询
- **批量操作**: 支持按模式批量删除缓存（使用 SCAN 命令，避免阻塞 Redis）
- **存在性检查**: 支持缓存存在性检查
- **异常处理**: 所有方法统一处理异常，记录日志但不抛出异常，确保业务逻辑不受影响
- **类型安全**: 支持泛型，提供类型安全的缓存操作

## 快速开始

### 添加依赖

在子模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.atlas</groupId>
    <artifactId>atlas-common-infra-redis</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 配置 Redis 连接

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

### 使用示例

#### 1. Redis 序列化配置

Redis 序列化配置会自动应用，无需手动配置。所有 Redis 操作都会使用统一的序列化方式：
- Key 使用 String 序列化（`StringRedisSerializer`）
- Value 使用 JSON 序列化（`GenericJackson2JsonRedisSerializer`）

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

#### 2. Key 命名规范

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

// 设置过期时间（可选，仅用于标记）
RedisKeyBuilder builder = RedisKeyBuilder.builder()
    .module("user")
    .business("info")
    .id("123")
    .withTtl(3600);  // 1 小时过期
String key = builder.build();
```

#### 3. 基础缓存工具

使用 `CacheUtil` 进行缓存操作：

```java
import com.atlas.common.infra.redis.util.CacheUtil;

// 设置缓存（不过期）
CacheUtil.set("atlas:user:info:123", userInfo);

// 设置缓存并指定过期时间（1 小时）
CacheUtil.set("atlas:user:info:123", userInfo, 3600);

// 获取缓存
UserInfo userInfo = CacheUtil.get("atlas:user:info:123", UserInfo.class);

// 检查缓存是否存在
boolean exists = CacheUtil.exists("atlas:user:info:123");

// 设置缓存过期时间
CacheUtil.expire("atlas:user:info:123", 3600);

// 获取缓存剩余过期时间（秒）
long ttl = CacheUtil.getExpire("atlas:user:info:123");

// 删除缓存
CacheUtil.delete("atlas:user:info:123");

// 按模式删除缓存（删除所有匹配的 Key）
CacheUtil.deletePattern("atlas:user:*");
```

#### 4. 完整业务场景示例

**用户信息缓存服务**:

```java
import com.atlas.common.infra.redis.key.RedisKeyBuilder;
import com.atlas.common.infra.redis.util.CacheUtil;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private static final int CACHE_EXPIRE_SECONDS = 3600;  // 1 小时
    
    /**
     * 根据 ID 获取用户信息（带缓存）
     */
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
    
    /**
     * 更新用户信息（更新数据库并删除缓存）
     */
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
    
    /**
     * 删除用户（删除数据库记录并删除缓存）
     */
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
    
    /**
     * 清除所有用户缓存
     */
    public void clearAllUserCache() {
        // 按模式删除所有用户相关的缓存
        CacheUtil.deletePattern("atlas:user:*");
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
      lettuce:
        pool:
          max-active: 8   # 连接池最大连接数
          max-idle: 8     # 连接池最大空闲连接数
          min-idle: 0     # 连接池最小空闲连接数
          max-wait: -1ms  # 连接池最大等待时间
```

### Key 前缀配置

Key 前缀配置用于统一管理 Key 命名空间：

```yaml
atlas:
  redis:
    key-prefix: "atlas"  # Key 前缀，默认值为 "atlas"
```

不同环境可以使用不同的前缀，例如：
- 开发环境：`dev`
- 测试环境：`test`
- 生产环境：`prod`

## 注意事项

1. **异常处理**: `CacheUtil` 的所有方法都会捕获异常并记录日志，不会抛出异常，确保缓存操作失败不会影响主业务流程。

2. **Key 命名规范**: 建议使用 `RedisKeyBuilder` 构建 Key，确保 Key 命名的一致性和规范性。

3. **过期时间**: 设置合理的缓存过期时间，避免缓存数据过期或占用过多内存。

4. **批量删除**: `deletePattern()` 方法使用 `keys()` 命令，对于大量 Key 的场景可能会有性能问题，建议谨慎使用。

5. **类型安全**: `get()` 方法使用泛型，确保类型安全，但需要确保缓存中的数据类型与指定的类型匹配。

## 相关文档

- [快速开始指南](../../../specs/006-infra-redis/quickstart.md) - 详细的使用指南和示例代码
- [功能规格说明](../../../specs/006-infra-redis/spec.md) - 完整的功能需求说明
- [技术规划文档](../../../specs/006-infra-redis/plan.md) - 技术实现方案
- [数据模型定义](../../../specs/006-infra-redis/data-model.md) - 数据模型定义
- [技术调研文档](../../../specs/006-infra-redis/research.md) - 技术决策和选型说明

## 技术栈

- **Java**: JDK 21
- **Spring Boot**: 4.0.1
- **Spring Data Redis**: Redis 操作支持（Spring Boot 内置）
- **Jackson**: JSON 序列化支持（Spring Boot 内置）
- **Lombok**: 代码简化（可选）

## 版本信息

- **当前版本**: 1.0.0
- **最后更新**: 2026-01-05
