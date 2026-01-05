# 任务清单

## 功能概述

实现 `atlas-common-infra-redis` 模块，提供 Redis 基础设施功能，包括 Redis 序列化配置、Key 命名规范和基础缓存工具类。

## 用户故事

### US1: Redis 序列化配置
**优先级**: P1  
**描述**: 系统使用统一的 Redis 序列化配置，所有 Redis 操作都使用统一的序列化方式（Key 使用 String 序列化，Value 使用 JSON 序列化），序列化后的数据在 Redis 中可读，便于调试和维护。  
**验收标准**: 
- RedisTemplate Bean 可以正确创建
- Key 和 Value 可以使用不同的序列化方式
- 序列化后的数据在 Redis 中可读（JSON 格式）
- 配置可以通过 application.yml 自定义
- RedisTemplate 可以正确序列化和反序列化 Java 对象

### US2: Key 命名规范
**优先级**: P1  
**描述**: 开发人员使用统一的 Key 命名规范工具类，所有 Redis Key 都符合命名规范（格式：`{prefix}:{module}:{business}:{id}`），Key 命名规范集中管理，便于维护和扩展。  
**验收标准**: 
- Key 命名符合统一规范格式
- Key 前缀可以统一配置
- Key 生成方法使用简单，支持链式调用
- Key 可以设置过期时间（TTL）
- Key 构建工具类包含完整的中文注释

### US3: 基础缓存工具
**优先级**: P2  
**描述**: 开发人员使用缓存工具类进行缓存操作，无需深入了解 Redis 底层实现，缓存操作统一处理异常，不影响主业务流程。  
**验收标准**: 
- 可以正确执行缓存的增删改查操作
- 支持设置和查询缓存过期时间
- 支持批量删除和模式匹配删除
- 异常情况可以正确处理并记录日志
- 缓存工具类包含完整的中文注释

## 依赖关系

```
Phase 1 (Setup)
    ↓
Phase 2 (US1: Redis 序列化配置)
    ↓
Phase 3 (US2: Key 命名规范) - 依赖 US1（需要 RedisTemplate）
    ↓
Phase 4 (US3: 基础缓存工具) - 依赖 US1 和 US2（需要 RedisTemplate 和 Key 构建）
    ↓
Phase 5 (Polish: 文档和测试)
```

## 并行执行机会

- **Phase 2 内部**: RedisConfig 和 RedisProperties 可以并行创建（不同文件）
- **Phase 3 内部**: RedisKeyBuilder 和 RedisProperties（如果未在 Phase 2 创建）可以并行创建
- **Phase 4 内部**: CacheUtil 的不同方法可以并行实现（但需要先创建类结构）

## MVP 范围

**MVP**: Phase 1 + Phase 2 + Phase 3（Setup + US1 + US2）

MVP 提供核心的 Redis 序列化配置和 Key 命名规范功能，满足最基本的 Redis 基础设施需求。基础缓存工具可以在后续迭代中实现。

## 实施任务

### Phase 1: 项目初始化和基础配置

**目标**: 创建模块结构，配置依赖，搭建基础框架

**独立测试标准**: 模块可以成功编译，依赖配置正确

- [x] T001 Create module directory structure `atlas-common/atlas-common-infra/atlas-common-infra-redis/`
- [x] T002 Create `pom.xml` with dependencies in `atlas-common/atlas-common-infra/atlas-common-infra-redis/pom.xml`
- [x] T003 Create package structure `com/atlas/common/infra/redis/config/` in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/`
- [x] T004 Create package structure `com/atlas/common/infra/redis/key/` in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/`
- [x] T005 Create package structure `com/atlas/common/infra/redis/util/` in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/`
- [x] T006 Create test package structure `com/atlas/common/infra/redis/config/` in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/test/java/`
- [x] T007 Create test package structure `com/atlas/common/infra/redis/key/` in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/test/java/`
- [x] T008 Create test package structure `com/atlas/common/infra/redis/util/` in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/test/java/`
- [x] T009 Create initial `README.md` in `atlas-common/atlas-common-infra/atlas-common-infra-redis/README.md`

### Phase 2: US1 - Redis 序列化配置

**目标**: 实现统一的 Redis 序列化配置

**独立测试标准**: RedisTemplate Bean 可以正确创建，Key 和 Value 可以使用不同的序列化方式，序列化后的数据在 Redis 中可读（JSON 格式）

- [x] T010 [P] [US1] Create `RedisProperties` class in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/config/RedisProperties.java`
- [x] T011 [US1] Create `RedisConfig` class in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/config/RedisConfig.java`
- [x] T012 [US1] Configure `RedisTemplate` Bean in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/config/RedisConfig.java`
- [x] T013 [US1] Configure Key serializer (StringRedisSerializer) in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/config/RedisConfig.java`
- [x] T014 [US1] Configure Value serializer (GenericJackson2JsonRedisSerializer) in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/config/RedisConfig.java`
- [x] T015 [US1] Configure Hash Key and Hash Value serializers in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/config/RedisConfig.java`
- [x] T016 [US1] Implement configuration property binding in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/config/RedisConfig.java`
- [x] T017 [P] [US1] Write unit tests for `RedisProperties` in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/test/java/com/atlas/common/infra/redis/config/RedisPropertiesTest.java`
- [x] T018 [US1] Write unit tests for `RedisConfig` in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/test/java/com/atlas/common/infra/redis/config/RedisConfigTest.java`

### Phase 3: US2 - Key 命名规范

**目标**: 实现统一的 Redis Key 命名规范工具类

**独立测试标准**: Key 命名符合统一规范格式，Key 前缀可以统一配置，Key 生成方法使用简单，支持链式调用，Key 可以设置过期时间

- [x] T019 [US2] Create `RedisKeyBuilder` class in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/key/RedisKeyBuilder.java`
- [x] T020 [US2] Implement Builder pattern with chain methods in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/key/RedisKeyBuilder.java`
- [x] T021 [US2] Implement `module(String module)` method in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/key/RedisKeyBuilder.java`
- [x] T022 [US2] Implement `business(String business)` method in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/key/RedisKeyBuilder.java`
- [x] T023 [US2] Implement `id(String id)` method in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/key/RedisKeyBuilder.java`
- [x] T024 [US2] Implement `build()` method to generate complete Key string in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/key/RedisKeyBuilder.java`
- [x] T025 [US2] Implement Key prefix configuration support in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/key/RedisKeyBuilder.java`
- [x] T026 [US2] Implement `withTtl(int seconds)` method in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/key/RedisKeyBuilder.java`
- [x] T027 [P] [US2] Write unit tests for `RedisKeyBuilder` in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/test/java/com/atlas/common/infra/redis/key/RedisKeyBuilderTest.java`

### Phase 4: US3 - 基础缓存工具

**目标**: 实现常用的缓存操作方法封装

**独立测试标准**: 可以正确执行缓存的增删改查操作，支持设置和查询缓存过期时间，支持批量删除和模式匹配删除，异常情况可以正确处理并记录日志

- [x] T028 [US3] Create `CacheUtil` class in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/util/CacheUtil.java`
- [x] T029 [US3] Implement static RedisTemplate injection in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/util/CacheUtil.java`
- [x] T030 [US3] Implement `set(String key, Object value)` method in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/util/CacheUtil.java`
- [x] T031 [US3] Implement `set(String key, Object value, int seconds)` method in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/util/CacheUtil.java`
- [x] T032 [US3] Implement `get(String key, Class<T> clazz)` method in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/util/CacheUtil.java`
- [x] T033 [US3] Implement `delete(String key)` method in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/util/CacheUtil.java`
- [x] T034 [US3] Implement `deletePattern(String pattern)` method in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/util/CacheUtil.java`
- [x] T035 [US3] Implement `exists(String key)` method in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/util/CacheUtil.java`
- [x] T036 [US3] Implement `expire(String key, int seconds)` method in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/util/CacheUtil.java`
- [x] T037 [US3] Implement `getExpire(String key)` method in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/util/CacheUtil.java`
- [x] T038 [US3] Implement exception handling and logging in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/main/java/com/atlas/common/infra/redis/util/CacheUtil.java`
- [x] T039 [P] [US3] Write unit tests for `CacheUtil` in `atlas-common/atlas-common-infra/atlas-common-infra-redis/src/test/java/com/atlas/common/infra/redis/util/CacheUtilTest.java`

### Phase 5: 文档和测试完善

**目标**: 完善文档，提供使用示例，确保测试覆盖率

**独立测试标准**: 文档完整清晰，使用示例可以正常运行，单元测试覆盖率 ≥ 80%

- [x] T040 Update `README.md` with module introduction in `atlas-common/atlas-common-infra/atlas-common-infra-redis/README.md`
- [x] T041 Update `README.md` with feature descriptions in `atlas-common/atlas-common-infra/atlas-common-infra-redis/README.md`
- [x] T042 Update `README.md` with quick start guide in `atlas-common/atlas-common-infra/atlas-common-infra-redis/README.md`
- [x] T043 Update `README.md` with usage examples in `atlas-common/atlas-common-infra/atlas-common-infra-redis/README.md`
- [x] T044 Update `README.md` with related documentation links in `atlas-common/atlas-common-infra/atlas-common-infra-redis/README.md`
- [x] T045 Run Spotless formatting check and fix any violations
- [x] T046 Run Maven Enforcer check and verify compliance
- [x] T047 Verify unit test coverage ≥ 80%
- [x] T048 Run integration tests for Redis connection and basic operations

## 实施策略

### MVP 优先

**MVP 范围**: Phase 1 + Phase 2 + Phase 3

MVP 提供核心的 Redis 序列化配置和 Key 命名规范功能，满足最基本的 Redis 基础设施需求。基础缓存工具可以在后续迭代中实现。

### 增量交付

1. **迭代 1**: Phase 1 + Phase 2（项目初始化和 Redis 序列化配置）
2. **迭代 2**: Phase 3（Key 命名规范）
3. **迭代 3**: Phase 4（基础缓存工具）
4. **迭代 4**: Phase 5（文档和测试完善）

### 并行执行策略

- **Phase 2 内部**: T010（RedisProperties）和 T011（RedisConfig）可以并行创建（不同文件）
- **Phase 3 内部**: RedisKeyBuilder 的不同方法可以并行实现（但需要先创建类结构）
- **Phase 4 内部**: CacheUtil 的不同方法可以并行实现（但需要先创建类结构）

### 测试策略

- **单元测试**: 每个组件都有对应的单元测试
- **集成测试**: 测试 Redis 连接和基本操作场景
- **测试覆盖率**: 目标 ≥ 80%

