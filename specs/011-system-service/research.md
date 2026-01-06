# 技术调研文档

## 概述

本文档记录 `atlas-system` 服务实现过程中的技术选型决策和调研结果。

## 技术选型决策

### 1. 数据库迁移工具选择：Flyway vs Liquibase

**决策**: 选择 **Flyway**

**理由**:
1. **简单易用**: Flyway 使用简单的 SQL 脚本，无需额外的 XML 配置，学习成本低
2. **版本控制**: Flyway 使用文件命名约定（V{version}__{description}.sql）进行版本控制，直观明了
3. **Spring Boot 集成**: Flyway 与 Spring Boot 集成更简单，只需添加依赖即可自动执行
4. **社区活跃**: Flyway 社区活跃，文档完善，问题解决速度快
5. **符合项目需求**: 项目需要简单的 SQL 脚本管理，Flyway 完全满足需求

**替代方案**: Liquibase
- **优点**: 支持多种数据库，可以使用 XML/YAML/JSON 格式，功能更强大
- **缺点**: 配置复杂，学习成本高，对于简单的 SQL 脚本管理显得过于复杂

**实现方式**:
- 添加 `flyway-core` 依赖
- 在 `src/main/resources/db/migration/` 目录下创建 SQL 脚本
- 脚本命名格式：`V{version}__{description}.sql`（如 `V1__Create_user_role_permission_tables.sql`）
- Flyway 会在应用启动时自动执行未执行的迁移脚本

**参考资源**:
- [Flyway 官方文档](https://flywaydb.org/documentation/)
- [Spring Boot Flyway 集成](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)

### 2. DTO 转换方案：MapStruct vs 手动转换

**决策**: 使用 **手动转换**（初期），后续可考虑引入 MapStruct

**理由**:
1. **简单直接**: 手动转换代码清晰，易于理解和维护
2. **灵活性高**: 可以灵活处理复杂的转换逻辑，不受框架限制
3. **减少依赖**: 不引入额外的依赖，减少项目复杂度
4. **符合最小闭环**: 当前功能需求简单，手动转换完全满足需求

**替代方案**: MapStruct
- **优点**: 编译时生成代码，性能好，类型安全
- **缺点**: 需要额外配置，增加项目复杂度，对于简单转换可能过度设计

**实现方式**:
- 在 Service 层实现 Entity 到 DTO 的转换方法
- 使用 Builder 模式或直接构造 DTO 对象
- 后续如果转换逻辑复杂，可以考虑引入 MapStruct

**参考资源**:
- [MapStruct 官方文档](https://mapstruct.org/documentation/stable/reference/html/)

### 3. 实体类设计：继承 BaseEntity vs 独立设计

**决策**: **继承 BaseEntity**（如果 `atlas-common-infra-db` 模块提供了 `BaseEntity`）

**理由**:
1. **代码复用**: 继承 `BaseEntity` 可以复用审计字段（创建时间、更新时间等）和逻辑删除字段
2. **统一规范**: 所有实体类使用统一的审计字段，符合项目规范
3. **减少重复**: 避免在每个实体类中重复定义审计字段

**实现方式**:
- 检查 `atlas-common-infra-db` 模块是否提供了 `BaseEntity`
- 如果提供了，实体类继承 `BaseEntity`
- 如果没有提供，实体类独立设计，但遵循相同的字段命名规范

**参考资源**:
- `atlas-common-infra-db` 模块文档

### 4. MyBatis-Plus 使用方式

**决策**: 使用 **MyBatis-Plus 标准方式**

**理由**:
1. **符合宪法要求**: 项目宪法要求使用 MyBatis-Plus 进行数据访问
2. **复用基础设施**: `atlas-common-infra-db` 模块已提供 MyBatis-Plus 配置
3. **简化开发**: MyBatis-Plus 提供了丰富的 CRUD 方法，减少代码量

**实现方式**:
- Mapper 接口继承 `BaseMapper<T>`
- 使用 MyBatis-Plus 提供的 CRUD 方法（如 `selectById`、`selectOne` 等）
- 复杂查询使用 `@Select` 注解或 XML 映射文件
- 复用 `atlas-common-infra-db` 模块的 MyBatis-Plus 配置

**参考资源**:
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- `atlas-common-infra-db` 模块文档

### 5. PostgreSQL 配置方式

**决策**: 使用 **Spring Boot 标准配置**

**理由**:
1. **简单直接**: Spring Boot 提供了标准的数据库配置方式
2. **符合规范**: 使用 YAML 格式配置文件，符合项目宪法要求
3. **易于维护**: 配置集中管理，便于维护

**实现方式**:
- 在 `application.yml` 中配置数据库连接信息
- 使用 Spring Boot 的自动配置功能
- 连接池使用 HikariCP（Spring Boot 默认）

**配置示例**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/atlas
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
```

**参考资源**:
- [Spring Boot 数据库配置](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.datasource)

### 6. Nacos 服务注册与发现配置

**决策**: 使用 **Spring Cloud Alibaba Nacos**

**理由**:
1. **符合宪法要求**: 项目宪法要求使用 Nacos 进行服务注册与发现
2. **Spring Cloud 集成**: Spring Cloud Alibaba 提供了完整的 Nacos 集成
3. **配置简单**: 只需添加依赖和配置即可

**实现方式**:
- 添加 `spring-cloud-starter-alibaba-nacos-discovery` 依赖
- 在 `application.yml` 中配置 Nacos 服务器地址
- 在启动类上添加 `@EnableDiscoveryClient` 注解（Spring Cloud 2025.1.0 可能不需要）

**配置示例**:
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: atlas
        group: DEFAULT_GROUP
```

**参考资源**:
- [Spring Cloud Alibaba Nacos 文档](https://github.com/alibaba/spring-cloud-alibaba/wiki/Nacos-discovery)

## 技术难点与解决方案

### 1. 权限查询性能优化

**问题**: 权限查询涉及多表关联（user -> user_role -> role -> role_permission -> permission），可能影响性能

**解决方案**:
1. **添加索引**: 在关联表的 `user_id`、`role_id`、`permission_id` 字段上添加索引
2. **优化 SQL**: 使用 JOIN 查询，减少数据库往返次数
3. **考虑缓存**: 如果性能仍不满足，可以考虑使用 Redis 缓存，但需确保数据实时性（当前需求是立即生效，暂不使用缓存）

### 2. 数据一致性保证

**问题**: 用户、角色、权限的关联关系复杂，可能出现数据不一致

**解决方案**:
1. **使用事务**: 所有关联操作使用数据库事务，确保原子性
2. **外键约束**: 在数据库层面添加外键约束，确保数据完整性
3. **业务校验**: 在 Service 层进行业务校验，确保关联关系正确

### 3. 接口契约一致性

**问题**: Controller 实现需要与 `atlas-system-api` 中定义的接口完全一致

**解决方案**:
1. **实现接口**: Controller 直接实现 API 模块中定义的 Feign 接口
2. **接口测试**: 编写接口测试，验证接口契约一致性
3. **代码审查**: 在代码审查时检查接口实现是否符合契约

## 总结

所有技术选型决策已完成，主要决策如下：

1. **数据库迁移工具**: Flyway（简单易用，符合项目需求）
2. **DTO 转换**: 手动转换（初期），后续可考虑 MapStruct
3. **实体类设计**: 继承 BaseEntity（如果提供）
4. **数据访问**: MyBatis-Plus 标准方式
5. **数据库配置**: Spring Boot 标准配置
6. **服务注册**: Spring Cloud Alibaba Nacos

所有技术选型都符合项目宪法要求，可以开始实施。

