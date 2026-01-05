# atlas-common-infra-redis

## 模块简介

`atlas-common-infra-redis` 是 Atlas 项目的 Redis 基础设施模块，提供统一的 Redis 序列化配置、Key 命名规范和基础缓存工具类。该模块为所有业务模块提供统一的 Redis 使用规范，确保缓存数据的一致性、可维护性和可追溯性。

## 主要功能

### 1. Redis 序列化配置

提供统一的 Redis 序列化配置，支持多种序列化方式：
- Key 使用 String 序列化（StringRedisSerializer）
- Value 使用 JSON 序列化（GenericJackson2JsonRedisSerializer）
- 支持通过配置文件自定义序列化方式
- 配置类使用 `@Configuration` 注解，自动注册到 Spring 容器

### 2. Key 命名规范

提供统一的 Redis Key 命名规范工具类：
- Key 格式：`{prefix}:{module}:{business}:{id}`（如：`atlas:user:info:123`）
- 支持 Key 前缀配置，统一管理 Key 命名空间
- 提供链式调用的 Builder 模式，使用简单
- 支持 Key 过期时间设置（TTL）

### 3. 基础缓存工具

提供常用的缓存操作方法封装：
- 支持 String、Object、List、Map 等数据类型的缓存操作
- 支持缓存过期时间设置和查询
- 支持缓存删除（单个、批量、按模式匹配）
- 支持缓存存在性检查
- 提供统一的异常处理和日志记录

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

# Atlas Redis 配置
atlas:
  redis:
    key-prefix: "atlas"  # Key 前缀，默认值为 "atlas"
```

### 使用示例

#### 1. Redis 序列化配置

Redis 序列化配置会自动应用，无需手动配置。

#### 2. Key 命名规范

使用 `RedisKeyBuilder` 构建符合规范的 Key：

```java
import com.atlas.common.infra.redis.key.RedisKeyBuilder;

String key = RedisKeyBuilder.builder()
    .module("user")
    .business("info")
    .id("123")
    .build();
// 结果: "atlas:user:info:123"
```

#### 3. 基础缓存工具

使用 `CacheUtil` 进行缓存操作：

```java
import com.atlas.common.infra.redis.util.CacheUtil;

// 设置缓存
CacheUtil.set("atlas:user:info:123", userInfo, 3600);

// 获取缓存
UserInfo userInfo = CacheUtil.get("atlas:user:info:123", UserInfo.class);

// 删除缓存
CacheUtil.delete("atlas:user:info:123");
```

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

## 版本信息

- **当前版本**: 1.0.0
- **最后更新**: 2026-01-05

