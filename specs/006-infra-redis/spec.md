# 功能规格说明

## 宪法检查

本规格说明必须符合项目宪法要求：

- ✅ **中文注释**: 所有代码使用中文注释
- ✅ **代码复用**: 识别并复用现有公共方法
- ✅ **模块化**: 功能归属正确的模块
- ✅ **YAML 配置**: 使用 YAML 格式配置文件

## 功能描述

`atlas-common-infra-redis` 是 Atlas 项目的 Redis 基础设施模块，提供统一的 Redis 序列化配置、Key 命名规范和基础缓存工具类。该模块为所有业务模块提供统一的 Redis 使用规范，确保缓存数据的一致性、可维护性和可追溯性。

### 核心功能

1. **Redis 序列化配置**: 提供统一的 Redis 序列化配置，支持 JSON、String、GenericJackson2JsonRedisSerializer 等多种序列化方式
2. **Key 命名规范**: 提供统一的 Redis Key 命名规范工具类，确保 Key 命名的一致性和可管理性
3. **基础缓存工具**: 提供常用的缓存操作方法封装，简化业务代码中的缓存使用

## 功能需求

### US1: Redis 序列化配置

**描述**: 提供统一的 Redis 序列化配置，支持多种序列化方式。

**需求**:
- 支持 JSON 序列化（GenericJackson2JsonRedisSerializer）
- 支持 String 序列化（StringRedisSerializer）
- 支持 Key 和 Value 分别配置不同的序列化方式
- 配置类使用 `@Configuration` 注解，自动注册到 Spring 容器
- 支持通过配置文件自定义序列化方式

**验收标准**:
- RedisTemplate 可以正确序列化和反序列化 Java 对象
- Key 和 Value 可以使用不同的序列化方式
- 序列化后的数据在 Redis 中可读（JSON 格式）
- 配置可以通过 application.yml 自定义

### US2: Key 命名规范

**描述**: 提供统一的 Redis Key 命名规范工具类。

**需求**:
- 提供 Key 构建工具类，支持模块名、业务标识、唯一标识等层级结构
- Key 格式：`{模块名}:{业务标识}:{唯一标识}`（如：`user:info:123`）
- 支持 Key 前缀配置，统一管理 Key 命名空间
- 提供 Key 生成方法，自动拼接各层级
- 支持 Key 过期时间设置（TTL）

**验收标准**:
- Key 命名符合统一规范格式
- Key 前缀可以统一配置
- Key 生成方法使用简单，支持链式调用
- Key 可以设置过期时间

### US3: 基础缓存工具

**描述**: 提供常用的缓存操作方法封装。

**需求**:
- 提供 `CacheUtil` 工具类，封装常用的缓存操作
- 支持 String、Object、List、Map 等数据类型的缓存操作
- 支持缓存过期时间设置
- 支持缓存删除（单个、批量、按模式匹配）
- 支持缓存存在性检查
- 支持缓存过期时间查询和设置
- 提供缓存操作异常处理

**验收标准**:
- 可以正确执行缓存的增删改查操作
- 支持设置和查询缓存过期时间
- 支持批量删除和模式匹配删除
- 异常情况可以正确处理并记录日志

## 数据模型

### RedisConfig（Redis 配置类）

**描述**: Redis 序列化配置类，提供统一的 RedisTemplate Bean。

**字段**:
- `keySerializer`: Key 序列化器（默认 StringRedisSerializer）
- `valueSerializer`: Value 序列化器（默认 GenericJackson2JsonRedisSerializer）
- `hashKeySerializer`: Hash Key 序列化器（默认 StringRedisSerializer）
- `hashValueSerializer`: Hash Value 序列化器（默认 GenericJackson2JsonRedisSerializer）

### RedisKeyBuilder（Key 构建工具类）

**描述**: Redis Key 命名规范工具类，提供统一的 Key 生成方法。

**方法**:
- `builder()`: 创建 Key 构建器
- `module(String module)`: 设置模块名
- `business(String business)`: 设置业务标识
- `id(String id)`: 设置唯一标识
- `build()`: 构建完整的 Key 字符串
- `withTtl(int seconds)`: 设置 Key 过期时间（秒）

### CacheUtil（缓存工具类）

**描述**: 基础缓存操作工具类，封装常用的缓存方法。

**方法**:
- `set(String key, Object value)`: 设置缓存
- `set(String key, Object value, int seconds)`: 设置缓存并指定过期时间
- `get(String key, Class<T> clazz)`: 获取缓存
- `delete(String key)`: 删除缓存
- `deletePattern(String pattern)`: 按模式删除缓存
- `exists(String key)`: 检查缓存是否存在
- `expire(String key, int seconds)`: 设置缓存过期时间
- `getExpire(String key)`: 获取缓存剩余过期时间

## 业务逻辑

### Redis 序列化配置流程

1. 创建 `RedisConfig` 配置类
2. 配置 Key 和 Value 的序列化器
3. 创建 `RedisTemplate` Bean
4. 注册到 Spring 容器

### Key 命名规范流程

1. 使用 `RedisKeyBuilder` 创建 Key 构建器
2. 设置模块名、业务标识、唯一标识
3. 调用 `build()` 方法生成完整的 Key
4. 可选：设置 Key 过期时间

### 缓存操作流程

1. 使用 `CacheUtil` 工具类进行缓存操作
2. 设置缓存时指定 Key、Value 和过期时间
3. 获取缓存时指定 Key 和数据类型
4. 删除缓存时指定 Key 或模式

## 异常处理

### Redis 连接异常

- **场景**: Redis 服务器不可用或网络异常
- **处理**: 记录错误日志，抛出 `RedisConnectionException`
- **影响**: 缓存操作失败，但不影响主业务流程

### 序列化异常

- **场景**: 对象无法序列化或反序列化
- **处理**: 记录错误日志，抛出 `SerializationException`
- **影响**: 缓存操作失败，返回 null 或抛出异常

### Key 不存在

- **场景**: 查询不存在的 Key
- **处理**: 返回 null，不抛出异常
- **影响**: 业务代码需要处理 null 值

## 依赖关系

### 模块依赖

- **spring-boot-starter-data-redis**: Spring Boot Redis 支持
- **jackson-databind**: JSON 序列化支持（Spring Boot 内置）

### 内部依赖

- **atlas-common-feature-core**: 依赖 `Result` 类和异常类（可选）

## 测试要求

- 单元测试覆盖率 ≥ 80%
- 测试 Redis 序列化配置的正确性
- 测试 Key 命名规范工具类的功能
- 测试缓存工具类的所有方法
- 集成测试覆盖 Redis 连接和基本操作场景

## 成功标准

1. **统一性**: 所有模块使用统一的 Redis 序列化配置，配置一致性达到 100%
2. **规范性**: 所有 Redis Key 符合命名规范，Key 命名规范性达到 100%
3. **易用性**: 开发人员可以在 5 分钟内理解并使用缓存工具类，无需深入了解 Redis 底层实现
4. **可维护性**: Key 命名规范集中管理，新增 Key 类型处理时间不超过 5 分钟
5. **性能**: 缓存操作响应时间 ≤ 10ms（本地 Redis），缓存命中率 ≥ 80%

## 实现注意事项

- [ ] 检查是否有可复用的公共方法（如 Jackson 配置）
- [ ] 确保所有类和方法添加中文注释
- [ ] 遵循 Redis 最佳实践
- [ ] 使用统一的异常处理机制
- [ ] 提供清晰的配置示例和使用文档
