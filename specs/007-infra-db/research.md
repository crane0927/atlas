# 技术调研文档

## 调研目标

为"实现 atlas-common-infra-db 模块"功能提供技术决策依据，解决规划中的技术选型和设计问题。

## 决策记录

### 决策 1: MyBatis-Plus 版本和配置方式

**问题**: 如何选择 MyBatis-Plus 版本和配置方式？

**决策**: 使用 MyBatis-Plus 3.5.8 版本，使用 `MybatisPlusInterceptor` 配置拦截器

**理由**:
1. **版本兼容性**: MyBatis-Plus 3.5.8 与 Spring Boot 4.0.1 兼容
2. **API 更新**: MyBatis-Plus 3.5.x 使用新的 `MybatisPlusInterceptor` API，替代旧的 `PaginationInterceptor`
3. **功能完整性**: 新 API 支持更多插件和拦截器，功能更强大
4. **维护性**: 使用最新稳定版本，便于后续维护和升级

**实现方式**:
```java
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }
}
```

**替代方案考虑**:
- **旧版本 API**: MyBatis-Plus 3.4.x 使用 `PaginationInterceptor`，但已废弃，不推荐
- **其他 ORM 框架**: Spring Data JPA 不符合项目宪法要求（必须使用 MyBatis-Plus）

### 决策 2: 分页插件选择

**问题**: 如何选择分页插件？

**决策**: 使用 `PaginationInnerInterceptor` 作为分页插件

**理由**:
1. **官方推荐**: MyBatis-Plus 3.5.x 官方推荐使用 `PaginationInnerInterceptor`
2. **数据库支持**: 支持多种数据库类型，包括 PostgreSQL
3. **功能完整**: 支持自定义分页参数、最大每页数量等配置
4. **性能优化**: 新版本插件性能更好，支持更多优化选项

**实现方式**:
```java
PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
paginationInterceptor.setMaxLimit(1000L);  // 最大每页数量
paginationInterceptor.setOverflow(false);   // 溢出总页数后处理
interceptor.addInnerInterceptor(paginationInterceptor);
```

**替代方案考虑**:
- **手动分页**: 需要手动编写分页 SQL，代码复杂，不推荐
- **PageHelper**: 第三方分页插件，但 MyBatis-Plus 自带分页插件，无需引入额外依赖

### 决策 3: 审计字段填充实现方式

**问题**: 如何实现审计字段自动填充？

**决策**: 使用 `MetaObjectHandler` 接口实现字段自动填充

**理由**:
1. **官方支持**: MyBatis-Plus 提供的标准接口，官方推荐使用
2. **易于实现**: 只需实现 `insertFill()` 和 `updateFill()` 方法
3. **灵活性**: 支持通过注解配置哪些字段需要自动填充
4. **可扩展性**: 可以轻松扩展支持更多审计字段

**实现方式**:
```java
@Component
public class AuditMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // 从安全上下文获取当前用户
        String currentUser = getCurrentUser();
        this.strictInsertFill(metaObject, "createBy", String.class, currentUser);
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        String currentUser = getCurrentUser();
        this.strictUpdateFill(metaObject, "updateBy", String.class, currentUser);
    }
}
```

**替代方案考虑**:
- **AOP 切面**: 可以实现，但代码复杂，不如 MyBatis-Plus 提供的接口简洁
- **数据库触发器**: 数据库层面实现，但不够灵活，不推荐

### 决策 4: 安全上下文集成方式

**问题**: 如何从安全上下文获取当前用户信息？

**决策**: 依赖 `atlas-common-feature-security` 模块的 `SecurityContextHolder` 获取当前用户信息

**理由**:
1. **模块化**: 符合模块化设计原则，复用安全模块的功能
2. **一致性**: 与项目其他模块使用相同的安全上下文获取方式
3. **可扩展性**: 如果安全模块未实现，可以填充默认值或留空

**实现方式**:
```java
private String getCurrentUser() {
    try {
        LoginUser loginUser = SecurityContextHolder.getContext().getLoginUser();
        return loginUser != null ? loginUser.getUsername() : "system";
    } catch (Exception e) {
        log.warn("获取当前用户信息失败，使用默认值", e);
        return "system";
    }
}
```

**替代方案考虑**:
- **硬编码**: 不够灵活，不推荐
- **Spring Security**: 如果项目使用 Spring Security，可以直接使用，但需要额外依赖

### 决策 5: 基础实体类设计

**问题**: 是否需要提供基础实体类 `BaseEntity`？

**决策**: 提供可选的基础实体类，包含审计字段定义

**理由**:
1. **便利性**: 业务模块可以直接继承基础实体类，无需重复定义审计字段
2. **一致性**: 确保所有业务模块使用相同的审计字段定义
3. **可选性**: 标记为可选，业务模块可以选择使用或自定义

**实现方式**:
```java
@Data
public class BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}
```

**替代方案考虑**:
- **不提供基础实体类**: 业务模块需要自行定义审计字段，代码重复，不推荐
- **强制使用基础实体类**: 不够灵活，不推荐

## 技术选型总结

| 技术点 | 选型 | 理由 |
|--------|------|------|
| MyBatis-Plus 版本 | 3.5.8 | 与 Spring Boot 4.0.1 兼容，功能完整 |
| 拦截器 API | MybatisPlusInterceptor | MyBatis-Plus 3.5.x 推荐使用的新 API |
| 分页插件 | PaginationInnerInterceptor | 官方推荐，支持多种数据库类型 |
| 审计字段填充 | MetaObjectHandler | MyBatis-Plus 提供的标准接口，易于实现 |
| 安全上下文 | SecurityContextHolder | 复用安全模块的功能，保持一致性 |
| 基础实体类 | BaseEntity（可选） | 提供便利性，但保持可选性 |

## 参考资料

- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [MyBatis-Plus 3.5.x 升级指南](https://baomidou.com/pages/fa3577/)
- [Spring Boot 4.0.1 官方文档](https://spring.io/projects/spring-boot)

