# 功能规划文档

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 提取公共方法，避免重复代码
- ✅ **模块化**: 遵循分层架构原则
- ✅ **包名规范**: 遵循 `com.atlas.common.infra.redis` 包名结构
- ✅ **配置文件格式**: 使用 YAML 格式

**宪法合规性评估**:
- ✅ 所有技术栈版本符合宪法要求
- ✅ 模块归属 `atlas-common-infra-redis`，符合模块化设计原则
- ✅ 提供可复用的 Redis 基础设施组件，符合代码复用原则
- ✅ 所有代码将使用中文注释
- ✅ 遵循包名规范
- ✅ 复用 `atlas-common-feature-core` 模块的异常类（可选）
- ✅ 复用 `atlas-common-infra-web` 模块的 Jackson 配置（序列化器）

## 功能概述

实现 `atlas-common-infra-redis` 模块，提供 Redis 基础设施功能，为所有业务模块提供统一的 Redis 序列化配置、Key 命名规范和基础缓存工具类。该模块确保：

1. **统一性**: 所有模块使用统一的 Redis 序列化配置，配置一致性达到 100%
2. **规范性**: 所有 Redis Key 符合命名规范，Key 命名规范性达到 100%
3. **易用性**: 开发人员可以在 5 分钟内理解并使用缓存工具类，无需深入了解 Redis 底层实现
4. **可维护性**: Key 命名规范集中管理，新增 Key 类型处理时间不超过 5 分钟
5. **性能**: 缓存操作响应时间 ≤ 10ms（本地 Redis），缓存命中率 ≥ 80%

## 技术方案

### 架构设计

**模块结构**:
```
atlas/
└── atlas-common/              # 公共模块
    └── atlas-common-infra/    # 基础设施模块
        └── atlas-common-infra-redis/  # Redis 基础设施模块
            ├── pom.xml                    # 模块 POM
            ├── README.md                  # 模块文档
            ├── src/
            │   ├── main/java/
            │   │   └── com/atlas/common/infra/redis/
            │   │       ├── config/        # Redis 配置
            │   │       │   └── RedisConfig.java
            │   │       ├── key/           # Key 命名规范
            │   │       │   └── RedisKeyBuilder.java
            │   │       └── util/          # 缓存工具类
            │   │           └── CacheUtil.java
            │   └── test/java/             # 测试代码
            │       └── com/atlas/common/infra/redis/
            │           ├── config/
            │           ├── key/
            │           └── util/
```

**核心组件**:

1. **Redis 序列化配置**: `RedisConfig`
   - 使用 `@Configuration` 注解
   - 配置 `RedisTemplate` Bean
   - 设置 Key 和 Value 的序列化器
   - 支持通过配置文件自定义序列化方式

2. **Key 命名规范工具**: `RedisKeyBuilder`
   - 提供链式调用的 Builder 模式
   - 支持模块名、业务标识、唯一标识等层级结构
   - 支持 Key 前缀配置
   - 支持 TTL 设置

3. **缓存工具类**: `CacheUtil`
   - 封装常用的缓存操作方法
   - 支持 String、Object、List、Map 等数据类型
   - 支持过期时间设置和查询
   - 支持批量删除和模式匹配删除
   - 提供异常处理和日志记录

### 技术选型

**构建工具**: Maven 3.8+
- **理由**: 项目使用 Maven，继承父 POM 配置

**核心依赖**:
- **spring-boot-starter-data-redis**: Spring Boot Redis 支持（Spring Boot 内置）
- **jackson-databind**: JSON 序列化支持（Spring Boot 内置）
- **atlas-common-feature-core**: 依赖异常类（可选）

**技术决策**:

1. **Redis 序列化方式**: 使用 GenericJackson2JsonRedisSerializer（Value）和 StringRedisSerializer（Key）
   - **理由**: JSON 格式可读性好，便于调试和维护；String 格式的 Key 便于管理和查询
   - **替代方案**: JdkSerializationRedisSerializer（二进制格式，不可读，不推荐）

2. **Key 命名规范**: 使用 Builder 模式构建 Key
   - **理由**: 链式调用简洁易用，符合现代 Java 编程习惯
   - **格式**: `{模块名}:{业务标识}:{唯一标识}`（如：`user:info:123`）

3. **缓存工具类设计**: 使用静态工具类封装 RedisTemplate
   - **理由**: 简化业务代码使用，统一异常处理
   - **注意**: 需要注入 RedisTemplate，使用 Spring 的静态方法注入或实例方法

## 实施计划

### 阶段 1: 项目初始化和基础配置

**目标**: 创建模块结构，配置依赖，搭建基础框架

**任务**:
1. 创建 `atlas-common-infra-redis` 模块目录结构
2. 创建 `pom.xml`，配置依赖（spring-boot-starter-data-redis、atlas-common-feature-core）
3. 创建包结构（config、key、util）
4. 创建 `README.md` 文档

**验收标准**:
- 模块结构创建完成
- 依赖配置正确
- 包结构符合规范

### 阶段 2: Redis 序列化配置实现

**目标**: 实现统一的 Redis 序列化配置

**任务**:
1. 创建 `RedisConfig` 配置类
2. 配置 `RedisTemplate` Bean
3. 设置 Key 序列化器（StringRedisSerializer）
4. 设置 Value 序列化器（GenericJackson2JsonRedisSerializer）
5. 设置 Hash Key 和 Hash Value 序列化器
6. 支持通过配置文件自定义序列化方式
7. 编写单元测试

**验收标准**:
- RedisTemplate Bean 可以正确创建
- Key 和 Value 可以使用不同的序列化方式
- 序列化后的数据在 Redis 中可读（JSON 格式）
- 配置可以通过 application.yml 自定义

### 阶段 3: Key 命名规范实现

**目标**: 实现统一的 Redis Key 命名规范工具类

**任务**:
1. 创建 `RedisKeyBuilder` 工具类
2. 实现 Builder 模式，支持链式调用
3. 实现 Key 前缀配置
4. 实现 Key 生成方法（module、business、id）
5. 实现 TTL 设置方法
6. 编写单元测试

**验收标准**:
- Key 命名符合统一规范格式
- Key 前缀可以统一配置
- Key 生成方法使用简单，支持链式调用
- Key 可以设置过期时间

### 阶段 4: 基础缓存工具实现

**目标**: 实现常用的缓存操作方法封装

**任务**:
1. 创建 `CacheUtil` 工具类
2. 实现 String 类型缓存操作（set、get、delete）
3. 实现 Object 类型缓存操作（set、get、delete）
4. 实现 List 类型缓存操作（set、get、delete）
5. 实现 Map 类型缓存操作（set、get、delete）
6. 实现过期时间设置和查询
7. 实现批量删除和模式匹配删除
8. 实现异常处理和日志记录
9. 编写单元测试

**验收标准**:
- 可以正确执行缓存的增删改查操作
- 支持设置和查询缓存过期时间
- 支持批量删除和模式匹配删除
- 异常情况可以正确处理并记录日志

### 阶段 5: 文档和测试完善

**目标**: 完善文档，提供使用示例，确保测试覆盖率

**任务**:
1. 更新 `README.md`，添加使用示例
2. 创建 `quickstart.md` 快速开始指南
3. 运行 Spotless 格式化检查
4. 运行 Maven Enforcer 检查
5. 验证单元测试覆盖率 ≥ 80%

**验收标准**:
- 文档完整清晰
- 使用示例可以正常运行
- 单元测试覆盖率 ≥ 80%

## 风险评估

### 风险 1: Redis 连接异常

**描述**: Redis 服务器不可用或网络异常导致缓存操作失败

**影响**: 缓存操作失败，但不影响主业务流程

**应对措施**:
- 在 `CacheUtil` 中统一处理异常，记录日志但不抛出异常
- 提供降级方案，缓存失败时直接查询数据库
- 监控 Redis 连接状态，及时告警

### 风险 2: 序列化异常

**描述**: 对象无法序列化或反序列化

**影响**: 缓存操作失败，返回 null 或抛出异常

**应对措施**:
- 确保所有缓存对象实现 Serializable 接口（如果使用 JDK 序列化）
- 使用 JSON 序列化，避免序列化问题
- 在 `CacheUtil` 中统一处理异常，记录日志

### 风险 3: Key 命名冲突

**描述**: 不同模块使用相同的 Key 导致数据冲突

**影响**: 缓存数据被覆盖，导致数据不一致

**应对措施**:
- 使用统一的 Key 命名规范工具类
- 强制使用模块名作为 Key 前缀
- 代码审查时检查 Key 命名是否符合规范

## 验收标准

1. **功能完整性**: 所有功能需求都已实现
2. **代码质量**: 代码通过 Spotless 格式化检查和 Maven Enforcer 检查
3. **测试覆盖率**: 单元测试覆盖率 ≥ 80%
4. **文档完整性**: README.md 和 quickstart.md 文档完整清晰
5. **性能指标**: 缓存操作响应时间 ≤ 10ms（本地 Redis），缓存命中率 ≥ 80%
