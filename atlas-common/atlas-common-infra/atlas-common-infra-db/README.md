# atlas-common-infra-db

## 模块简介

`atlas-common-infra-db` 是 Atlas 项目的数据库访问基础设施模块，提供统一的 MyBatis-Plus 基础配置、分页插件、审计字段填充等功能。该模块为所有业务模块提供统一的数据库访问规范，确保数据访问的一致性、可维护性和可追溯性。

## 主要功能

### 1. MyBatis-Plus 基础配置

提供统一的 MyBatis-Plus 配置，包括：
- **拦截器配置**: 配置 `MybatisPlusInterceptor` 拦截器，用于添加各种插件
- **自动注册**: 配置类使用 `@Configuration` 注解，自动注册到 Spring 容器
- **自定义支持**: 支持通过配置文件自定义配置参数
- **全局设置**: 支持配置全局设置（字段策略、逻辑删除等，可选）

### 2. 分页插件

提供统一的分页插件配置，简化分页查询的实现：
- **自动拦截**: 分页插件自动拦截带有 `Page` 参数的方法，无需手动编写分页 SQL
- **完整信息**: 分页查询结果包含总记录数、当前页码、每页数量、总页数等信息
- **参数配置**: 支持自定义分页参数（最大每页数量、溢出处理、数据库类型等）
- **数据库支持**: 支持 PostgreSQL 数据库（符合项目宪法要求）

### 3. 审计字段填充

提供审计字段自动填充功能，简化数据审计字段的管理：
- **自动填充**: 插入和更新数据时自动填充审计字段（创建时间、更新时间、创建人、更新人）
- **安全集成**: 集成安全模块，从安全上下文获取当前用户信息（可选）
- **基础实体**: 提供 `BaseEntity` 基础实体类，业务实体类可以继承使用
- **逻辑删除**: `BaseEntity` 包含逻辑删除字段，支持逻辑删除功能
- **异常处理**: 如果获取用户信息失败，使用默认值 "system"，不阻塞主流程

## 快速开始

### 添加依赖

在子模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.atlas</groupId>
    <artifactId>atlas-common-infra-db</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 配置数据库连接

在 `application.yml` 中配置数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/atlas
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

# MyBatis-Plus 配置（可选）
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 开启驼峰命名转换
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 打印 SQL（开发环境）

# Atlas MyBatis-Plus 配置
atlas:
  mybatis-plus:
    pagination:
      max-limit: 1000      # 最大每页数量，默认值为 1000
      overflow: false     # 溢出总页数后处理，默认值为 false
      db-type: POSTGRE_SQL # 数据库类型，默认值为 POSTGRE_SQL
```

### 使用示例

#### 1. MyBatis-Plus 基础配置

MyBatis-Plus 配置会自动应用，无需手动配置。所有数据库操作都会使用统一的配置。

**自动配置**:
- MyBatis-Plus 拦截器自动注册
- 分页插件自动配置
- 全局配置自动应用

#### 2. 创建实体类

创建实体类，可以选择继承 `BaseEntity`：

```java
import com.atlas.common.infra.db.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    private String email;
    private String phone;
    // 其他业务字段...
    // 审计字段（createTime、updateTime、createBy、updateBy）和逻辑删除字段（deleted）已从 BaseEntity 继承
}
```

或者自定义实体类：

```java
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    private String email;
    
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

#### 3. 创建 Mapper 接口

创建 Mapper 接口，继承 `BaseMapper<T>`：

```java
import com.atlas.system.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 可以使用 MyBatis-Plus 提供的 CRUD 方法
    // 也可以自定义查询方法
}
```

#### 4. 使用 MyBatis-Plus 进行数据库操作

在 Service 中使用 Mapper 进行数据库操作：

```java
import com.atlas.system.entity.User;
import com.atlas.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 根据 ID 查询用户
     */
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
    
    /**
     * 创建用户
     */
    public void createUser(User user) {
        // createTime、createBy 会自动填充（如果实现了审计字段填充）
        userMapper.insert(user);
    }
    
    /**
     * 更新用户
     */
    public void updateUser(User user) {
        // updateTime、updateBy 会自动填充（如果实现了审计字段填充）
        userMapper.updateById(user);
    }
    
    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
}
```

#### 5. 使用分页插件进行分页查询

使用分页插件进行分页查询：

```java
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.atlas.system.entity.User;
import com.atlas.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 分页查询用户列表
     */
    public Page<User> getUserList(int page, int size) {
        // 创建分页对象
        Page<User> pageParam = new Page<>(page, size);
        
        // 创建查询条件（可选）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", "admin");
        
        // 执行分页查询
        Page<User> result = userMapper.selectPage(pageParam, queryWrapper);
        
        // 分页结果包含：
        // - result.getRecords(): 当前页数据列表
        // - result.getTotal(): 总记录数
        // - result.getCurrent(): 当前页码
        // - result.getSize(): 每页数量
        // - result.getPages(): 总页数
        
        return result;
    }
}
```

#### 6. 完整业务场景示例

**用户管理服务**:

```java
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.atlas.common.feature.core.page.PageResult;
import com.atlas.system.entity.User;
import com.atlas.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 分页查询用户列表
     */
    public PageResult<User> getUserList(int page, int size, String keyword) {
        // 创建分页对象
        Page<User> pageParam = new Page<>(page, size);
        
        // 创建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("username", keyword)
                       .or()
                       .like("email", keyword);
        }
        queryWrapper.orderByDesc("create_time");
        
        // 执行分页查询
        Page<User> result = userMapper.selectPage(pageParam, queryWrapper);
        
        // 转换为 PageResult（使用 atlas-common-feature-core 模块的 PageResult）
        return PageResult.of(result.getRecords(), result.getTotal());
    }
    
    /**
     * 创建用户
     */
    public void createUser(User user) {
        // createTime、createBy 会自动填充（如果实现了审计字段填充）
        userMapper.insert(user);
    }
    
    /**
     * 更新用户
     */
    public void updateUser(User user) {
        // updateTime、updateBy 会自动填充（如果实现了审计字段填充）
        userMapper.updateById(user);
    }
    
    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
}
```

## 配置说明

### 数据库连接配置

数据库连接配置使用 Spring Boot 的标准配置：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/atlas  # 数据库连接 URL
    username: postgres                          # 数据库用户名
    password: postgres                          # 数据库密码
    driver-class-name: org.postgresql.Driver    # 数据库驱动
```

### MyBatis-Plus 配置

MyBatis-Plus 配置使用 Spring Boot 的标准配置：

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 开启驼峰命名转换
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 打印 SQL（开发环境）
  global-config:
    db-config:
      logic-delete-value: 1    # 逻辑删除值（可选）
      not-logic-delete-value: 0 # 非逻辑删除值（可选）
      id-type: AUTO             # ID 生成策略（可选）
```

### Atlas MyBatis-Plus 配置

Atlas 自定义配置：

```yaml
atlas:
  mybatis-plus:
    pagination:
      max-limit: 1000      # 最大每页数量，默认值为 1000
      overflow: false     # 溢出总页数后处理，默认值为 false
      db-type: POSTGRE_SQL # 数据库类型，默认值为 POSTGRE_SQL
```

## 注意事项

1. **数据库连接**: 数据库连接配置由 Spring Boot 自动配置，本模块不涉及连接配置。

2. **审计字段填充**: 审计字段填充功能依赖 `atlas-common-feature-security` 模块获取当前用户信息。如果安全模块未实现，创建人和更新人字段可以填充默认值 "system"。

3. **分页查询**: 分页查询使用 MyBatis-Plus 的 `Page<T>` 对象，分页插件会自动拦截并添加分页 SQL。

4. **实体类设计**: 建议使用 `BaseEntity` 基础实体类，确保审计字段和逻辑删除字段的一致性。`BaseEntity` 不包含主键字段，业务实体类需要自行定义主键字段。如果业务需要自定义审计字段，可以自行定义。

5. **Mapper 扫描**: 确保 Mapper 接口被 Spring 扫描到，可以使用 `@Mapper` 注解或 `@MapperScan` 注解。

6. **数据库类型**: 根据项目宪法要求，必须使用 PostgreSQL 作为主要关系型数据库。

## 相关文档

- [功能规格说明](../../../../specs/007-infra-db/spec.md) - 完整的功能需求说明
- [技术规划文档](../../../../specs/007-infra-db/plan.md) - 技术实现方案
- [数据模型定义](../../../../specs/007-infra-db/data-model.md) - 数据模型定义
- [技术调研文档](../../../../specs/007-infra-db/research.md) - 技术决策和选型说明
- [快速开始指南](../../../../specs/007-infra-db/quickstart.md) - 快速开始指南

## 版本历史

- **1.0.0** (2026-01-05)
  - 初始版本
  - 实现 MyBatis-Plus 基础配置
  - 实现分页插件配置
  - 实现审计字段填充功能

