# 数据模型

## 概述

本文档定义了 `atlas-common-infra-db` 模块涉及的所有数据实体、配置参数和工具类。

## 核心实体

### MyBatisPlusConfig（MyBatis-Plus 配置类）

**描述**: MyBatis-Plus 基础配置类，提供统一的配置项。

**包名**: `com.atlas.common.infra.db.config`

**字段定义**:

| 字段名 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| interceptor | MybatisPlusInterceptor | MyBatis-Plus 拦截器 | - |
| globalConfig | GlobalConfig | 全局配置 | - |

**方法定义**:

| 方法名 | 参数 | 返回类型 | 说明 | 必填 |
|--------|------|----------|------|------|
| mybatisPlusInterceptor | - | MybatisPlusInterceptor | 创建 MyBatis-Plus 拦截器 | 是 |
| paginationInnerInterceptor | - | PaginationInnerInterceptor | 创建分页插件 | 是 |

**约束规则**:
- 配置类使用 `@Configuration` 注解，自动注册到 Spring 容器
- 拦截器必须包含分页插件
- 支持通过配置文件自定义配置参数

### MyBatisPlusProperties（MyBatis-Plus 配置属性类）

**描述**: MyBatis-Plus 配置属性类，用于读取配置文件中的配置参数。

**包名**: `com.atlas.common.infra.db.config`

**字段定义**:

| 字段名 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| pagination | PaginationProperties | 分页配置 | - |
| global | GlobalProperties | 全局配置 | - |

**约束规则**:
- 使用 `@ConfigurationProperties` 注解绑定配置
- prefix 为 "atlas.mybatis-plus"
- 提供默认值，简化配置

### PaginationProperties（分页配置属性类）

**描述**: 分页插件配置属性类。

**包名**: `com.atlas.common.infra.db.config`

**字段定义**:

| 字段名 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| maxLimit | Long | 最大每页数量 | 1000 |
| overflow | Boolean | 溢出总页数后处理 | false |
| dbType | DbType | 数据库类型 | POSTGRE_SQL |

**约束规则**:
- 最大每页数量必须大于 0
- 数据库类型必须与项目使用的数据库一致

### AuditMetaObjectHandler（审计字段填充处理器）

**描述**: 审计字段自动填充处理器，实现 `MetaObjectHandler` 接口。

**包名**: `com.atlas.common.infra.db.handler`

**方法定义**:

| 方法名 | 参数 | 返回类型 | 说明 | 必填 |
|--------|------|----------|------|------|
| insertFill | MetaObject metaObject | void | 插入时填充字段 | 是 |
| updateFill | MetaObject metaObject |  void | 更新时填充字段 | 是 |
| getCurrentUser | - | String | 获取当前用户信息 | 是 |

**处理的字段**:

| 字段名 | 类型 | 填充时机 | 说明 |
|--------|------|----------|------|
| createTime | LocalDateTime | INSERT | 创建时间 |
| updateTime | LocalDateTime | INSERT, UPDATE | 更新时间 |
| createBy | String | INSERT | 创建人（从安全上下文获取） |
| updateBy | String | UPDATE | 更新人（从安全上下文获取） |

**约束规则**:
- 处理器使用 `@Component` 注解，自动注册到 Spring 容器
- 如果安全上下文获取失败，使用默认值 "system"
- 字段填充失败不阻塞主流程，只记录警告日志

### BaseEntity（基础实体类）

**描述**: 基础实体类，包含审计字段定义（可选）。

**包名**: `com.atlas.common.infra.db.entity`

**字段定义**:

| 字段名 | 类型 | 说明 | 注解 |
|--------|------|------|------|
| id | Long | 主键 ID | @TableId(type = IdType.AUTO) |
| createTime | LocalDateTime | 创建时间 | @TableField(fill = FieldFill.INSERT) |
| updateTime | LocalDateTime | 更新时间 | @TableField(fill = FieldFill.INSERT_UPDATE) |
| createBy | String | 创建人 | @TableField(fill = FieldFill.INSERT) |
| updateBy | String | 更新人 | @TableField(fill = FieldFill.INSERT_UPDATE) |

**约束规则**:
- 基础实体类为可选，业务模块可以选择使用或自定义
- 审计字段使用 `@TableField` 注解标记自动填充
- ID 字段使用自增策略

## 配置参数

### application.yml 配置示例

```yaml
# MyBatis-Plus 配置
atlas:
  mybatis-plus:
    pagination:
      max-limit: 1000      # 最大每页数量，默认值为 1000
      overflow: false      # 溢出总页数后处理，默认值为 false
      db-type: POSTGRE_SQL # 数据库类型，默认值为 POSTGRE_SQL
    global:
      db-config:
        logic-delete-value: 1    # 逻辑删除值（可选）
        not-logic-delete-value: 0 # 非逻辑删除值（可选）
        id-type: AUTO             # ID 生成策略（可选）
```

## 数据关系

### MyBatisPlusConfig 与 MybatisPlusInterceptor

- `MyBatisPlusConfig` 创建并配置 `MybatisPlusInterceptor` Bean
- `MybatisPlusInterceptor` 包含 `PaginationInnerInterceptor` 分页插件

### AuditMetaObjectHandler 与 BaseEntity

- `AuditMetaObjectHandler` 处理 `BaseEntity` 中定义的审计字段
- 业务实体类可以继承 `BaseEntity` 使用审计字段

### MyBatisPlusProperties 与 MyBatisPlusConfig

- `MyBatisPlusProperties` 提供配置参数
- `MyBatisPlusConfig` 使用配置参数进行配置

## 使用示例

### BaseEntity 使用示例

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    private String username;
    private String email;
    // 其他业务字段...
}
```

### 分页查询使用示例

```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public Page<User> getUserList(int page, int size) {
        Page<User> pageParam = new Page<>(page, size);
        return userMapper.selectPage(pageParam, null);
    }
}
```

### 审计字段填充使用示例

```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public void createUser(User user) {
        // createTime、createBy 会自动填充
        userMapper.insert(user);
    }
    
    public void updateUser(User user) {
        // updateTime、updateBy 会自动填充
        userMapper.updateById(user);
    }
}
```

