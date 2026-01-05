# 数据模型

## 概述

本文档定义了 `atlas-common-infra-redis` 模块涉及的所有数据实体、配置参数和工具类。

## 核心实体

### RedisConfig（Redis 配置类）

**描述**: Redis 序列化配置类，提供统一的 `RedisTemplate` Bean。

**包名**: `com.atlas.common.infra.redis.config`

**字段定义**:

| 字段名 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| keySerializer | RedisSerializer<String> | Key 序列化器 | StringRedisSerializer |
| valueSerializer | RedisSerializer<Object> | Value 序列化器 | GenericJackson2JsonRedisSerializer |
| hashKeySerializer | RedisSerializer<String> | Hash Key 序列化器 | StringRedisSerializer |
| hashValueSerializer | RedisSerializer<Object> | Hash Value 序列化器 | GenericJackson2JsonRedisSerializer |

**约束规则**:
- Key 序列化器必须使用 StringRedisSerializer
- Value 序列化器推荐使用 GenericJackson2JsonRedisSerializer
- 配置类使用 `@Configuration` 注解，自动注册到 Spring 容器

### RedisKeyBuilder（Key 构建工具类）

**描述**: Redis Key 命名规范工具类，提供统一的 Key 生成方法。

**包名**: `com.atlas.common.infra.redis.key`

**字段定义**:

| 字段名 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| module | String | 模块名 | - |
| business | String | 业务标识 | - |
| id | String | 唯一标识 | - |
| prefix | String | Key 前缀 | 从配置读取 |

**方法定义**:

| 方法名 | 参数 | 返回类型 | 说明 | 必填 |
|--------|------|----------|------|------|
| builder | - | RedisKeyBuilder | 创建 Key 构建器 | 是 |
| module | String module | RedisKeyBuilder | 设置模块名 | 是 |
| business | String business | RedisKeyBuilder | 设置业务标识 | 是 |
| id | String id | RedisKeyBuilder | 设置唯一标识 | 是 |
| build | - | String | 构建完整的 Key 字符串 | 是 |
| withTtl | int seconds | RedisKeyBuilder | 设置 Key 过期时间（秒） | 否 |

**约束规则**:
- Key 格式：`{prefix}:{module}:{business}:{id}`
- 所有字段不能为 null 或空字符串
- prefix 从配置文件读取，默认值为 "atlas"

### CacheUtil（缓存工具类）

**描述**: 基础缓存操作工具类，封装常用的缓存方法。

**包名**: `com.atlas.common.infra.redis.util`

**方法定义**:

| 方法名 | 参数 | 返回类型 | 说明 | 必填 |
|--------|------|----------|------|------|
| set | String key, Object value | void | 设置缓存 | 是 |
| set | String key, Object value, int seconds | void | 设置缓存并指定过期时间 | 是 |
| get | String key, Class<T> clazz | T | 获取缓存 | 是 |
| delete | String key | void | 删除缓存 | 是 |
| deletePattern | String pattern | void | 按模式删除缓存 | 是 |
| exists | String key | boolean | 检查缓存是否存在 | 是 |
| expire | String key, int seconds | void | 设置缓存过期时间 | 是 |
| getExpire | String key | long | 获取缓存剩余过期时间（秒） | 是 |

**约束规则**:
- 所有方法统一处理异常，记录日志但不抛出异常
- get 方法如果 Key 不存在，返回 null
- deletePattern 方法使用 SCAN 命令，避免阻塞 Redis

### RedisProperties（Redis 配置属性类）

**描述**: Redis 配置属性类，用于读取配置文件中的 Redis 相关配置。

**包名**: `com.atlas.common.infra.redis.config`

**字段定义**:

| 字段名 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| keyPrefix | String | Key 前缀 | "atlas" |

**约束规则**:
- 使用 `@ConfigurationProperties` 注解绑定配置
- prefix 为 "atlas.redis"
- 提供默认值，简化配置

## 配置参数

### application.yml 配置示例

```yaml
atlas:
  redis:
    key-prefix: "atlas"  # Key 前缀，默认值为 "atlas"
```

## 数据关系

### RedisConfig 与 RedisTemplate

- `RedisConfig` 创建并配置 `RedisTemplate` Bean
- `RedisTemplate` 被 `CacheUtil` 使用

### RedisKeyBuilder 与 CacheUtil

- `RedisKeyBuilder` 生成符合规范的 Key
- `CacheUtil` 使用生成的 Key 进行缓存操作

### RedisProperties 与 RedisKeyBuilder

- `RedisProperties` 提供 Key 前缀配置
- `RedisKeyBuilder` 使用配置的前缀构建 Key

## 使用示例

### RedisKeyBuilder 使用示例

```java
// 构建 Key
String key = RedisKeyBuilder.builder()
    .module("user")
    .business("info")
    .id("123")
    .build();
// 结果: "atlas:user:info:123"

// 设置过期时间
RedisKeyBuilder builder = RedisKeyBuilder.builder()
    .module("user")
    .business("info")
    .id("123")
    .withTtl(3600);  // 1 小时过期
```

### CacheUtil 使用示例

```java
// 设置缓存
CacheUtil.set("atlas:user:info:123", userInfo);

// 设置缓存并指定过期时间
CacheUtil.set("atlas:user:info:123", userInfo, 3600);

// 获取缓存
UserInfo userInfo = CacheUtil.get("atlas:user:info:123", UserInfo.class);

// 删除缓存
CacheUtil.delete("atlas:user:info:123");

// 检查缓存是否存在
boolean exists = CacheUtil.exists("atlas:user:info:123");

// 设置过期时间
CacheUtil.expire("atlas:user:info:123", 3600);

// 获取剩余过期时间
long ttl = CacheUtil.getExpire("atlas:user:info:123");
```

