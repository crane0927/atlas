# 数据模型

## 概述

本文档定义了 `atlas-common-feature-security` 模块涉及的所有接口、注解和枚举。

## 核心接口

### LoginUser（接口）

**描述**: 登录用户信息模型接口，封装当前登录用户的基本信息和权限信息。

**包名**: `com.atlas.common.feature.security.user`

**方法定义**:

| 方法名 | 返回类型 | 说明 | 必填 |
|--------|----------|------|------|
| getUserId | Object | 获取用户ID（支持 String、Long 等类型） | 是 |
| getUsername | String | 获取用户名 | 是 |
| getRoles | List<String> | 获取用户角色列表 | 是 |
| getPermissions | List<String> | 获取用户权限列表 | 是 |
| hasRole | boolean | 判断是否拥有指定角色 | 是 |
| hasPermission | boolean | 判断是否拥有指定权限 | 是 |

**约束规则**:
- getUserId() 不能返回 null
- getUsername() 不能返回 null 或空字符串
- getRoles() 不能返回 null（可以为空列表）
- getPermissions() 不能返回 null（可以为空列表）
- hasRole() 和 hasPermission() 方法应该基于 getRoles() 和 getPermissions() 的结果进行判断

**扩展性**:
- 业务模块可以实现 LoginUser 接口并添加自定义字段和方法
- 实现类可以添加额外的业务字段（如部门、邮箱等）

**示例实现**:
```java
public class DefaultLoginUser implements LoginUser {
    private Long userId;
    private String username;
    private List<String> roles;
    private List<String> permissions;
    
    // 实现所有必需的方法
    // 可以添加自定义字段和方法
}
```

### SecurityContext（接口）

**描述**: 安全上下文接口，提供获取当前登录用户信息和认证状态的方法。

**包名**: `com.atlas.common.feature.security.context`

**方法定义**:

| 方法名 | 返回类型 | 说明 | 必填 |
|--------|----------|------|------|
| getLoginUser | LoginUser | 获取当前登录用户（未登录返回 null） | 是 |
| isAuthenticated | boolean | 判断当前是否已认证 | 是 |
| clear | void | 清除当前安全上下文（可选） | 否 |

**约束规则**:
- getLoginUser() 未登录时应返回 null
- isAuthenticated() 应该与 getLoginUser() != null 保持一致
- clear() 方法由实现类决定是否提供

**实现方式**:
- ThreadLocal: 线程本地存储，适用于单线程请求处理
- Request Scope: 请求作用域，适用于 Web 应用
- 全局单例: 适用于单用户应用（不推荐）

**示例实现**:
```java
public class ThreadLocalSecurityContext implements SecurityContext {
    private static final ThreadLocal<LoginUser> context = new ThreadLocal<>();
    
    @Override
    public LoginUser getLoginUser() {
        return context.get();
    }
    
    @Override
    public boolean isAuthenticated() {
        return context.get() != null;
    }
    
    @Override
    public void clear() {
        context.remove();
    }
}
```

### SecurityContextHolder（抽象类）

**描述**: 安全上下文持有者，提供静态方法获取安全上下文。

**包名**: `com.atlas.common.feature.security.context`

**方法定义**:

| 方法名 | 返回类型 | 说明 | 必填 |
|--------|----------|------|------|
| getContext | SecurityContext | 获取当前安全上下文（静态方法） | 是 |
| getLoginUser | LoginUser | 获取当前登录用户（静态方法） | 是 |
| isAuthenticated | boolean | 判断当前是否已认证（静态方法） | 是 |

**设计说明**:
- 抽象类，不包含具体实现
- getContext() 方法由具体实现提供
- getLoginUser() 和 isAuthenticated() 提供便捷方法，基于 getContext() 实现

**使用示例**:
```java
// 获取当前登录用户
LoginUser user = SecurityContextHolder.getLoginUser();

// 判断是否已认证
if (SecurityContextHolder.isAuthenticated()) {
    // 已认证逻辑
}
```

## 权限注解

### @RequiresPermission（注解）

**描述**: 权限检查注解，用于在方法或类上声明权限要求。

**包名**: `com.atlas.common.feature.security.annotation`

**属性定义**:

| 属性名 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| value | String[] | 所需的权限列表 | 是 | {} |
| logical | Logical | 权限之间的逻辑关系（AND/OR） | 否 | Logical.AND |

**使用规则**:
- 可以应用于类级别和方法级别
- 方法级别的注解会覆盖类级别的注解
- value 数组为空时表示不需要权限（通常不推荐）
- logical = AND 表示需要拥有所有权限
- logical = OR 表示需要拥有任一权限

**使用示例**:
```java
// 类级别：需要 user:read 权限
@RequiresPermission("user:read")
public class UserController {
    
    // 方法级别：需要 user:write 权限（覆盖类级别）
    @RequiresPermission(value = "user:write", logical = Logical.OR)
    public void updateUser() {
        // ...
    }
    
    // 需要多个权限（AND 关系）
    @RequiresPermission({"user:read", "user:write"})
    public void readAndWrite() {
        // ...
    }
}
```

### @RequiresRole（注解）

**描述**: 角色检查注解，用于在方法或类上声明角色要求。

**包名**: `com.atlas.common.feature.security.annotation`

**属性定义**:

| 属性名 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| value | String[] | 所需的角色列表 | 是 | {} |
| logical | Logical | 角色之间的逻辑关系（AND/OR） | 否 | Logical.AND |

**使用规则**:
- 可以应用于类级别和方法级别
- 方法级别的注解会覆盖类级别的注解
- value 数组为空时表示不需要角色（通常不推荐）
- logical = AND 表示需要拥有所有角色
- logical = OR 表示需要拥有任一角色

**使用示例**:
```java
// 需要 ADMIN 角色
@RequiresRole("ADMIN")
public class AdminController {
    
    // 需要 ADMIN 或 MANAGER 角色
    @RequiresRole(value = {"ADMIN", "MANAGER"}, logical = Logical.OR)
    public void manageUsers() {
        // ...
    }
}
```

### Logical（枚举）

**描述**: 逻辑关系枚举，用于定义权限或角色之间的逻辑关系。

**包名**: `com.atlas.common.feature.security.annotation`

**枚举值**:

| 枚举值 | 说明 |
|--------|------|
| AND | 逻辑与，需要满足所有条件 |
| OR | 逻辑或，满足任一条件即可 |

**使用场景**:
- 多个权限需要同时满足：使用 AND
- 多个权限满足任一即可：使用 OR
- 多个角色需要同时拥有：使用 AND
- 多个角色拥有任一即可：使用 OR

## 异常接口（可选）

### AuthenticationException（接口）

**描述**: 认证异常接口，表示用户未认证或认证失败。

**包名**: `com.atlas.common.feature.security.exception`

**方法定义**:

| 方法名 | 返回类型 | 说明 | 必填 |
|--------|----------|------|------|
| getMessage | String | 获取异常消息 | 是 |

**使用场景**:
- 用户未登录时访问需要认证的资源
- Token 无效或过期
- 认证信息缺失

### AuthorizationException（接口）

**描述**: 授权异常接口，表示用户权限不足。

**包名**: `com.atlas.common.feature.security.exception`

**方法定义**:

| 方法名 | 返回类型 | 说明 | 必填 |
|--------|----------|------|------|
| getMessage | String | 获取异常消息 | 是 |

**使用场景**:
- 用户权限不足时访问受保护资源
- 用户角色不符合要求
- 权限检查失败

## 实体关系

### LoginUser 与 SecurityContext 的关系

```
SecurityContext
  ├── getLoginUser(): LoginUser
  └── 安全上下文持有当前登录用户
```

### SecurityContextHolder 与 SecurityContext 的关系

```
SecurityContextHolder (静态方法)
  ├── getContext(): SecurityContext
  └── 提供便捷的静态方法访问安全上下文
```

### 注解与权限检查的关系

```
@RequiresPermission / @RequiresRole (注解)
  └── 权限检查框架（后续实现）
      └── 根据注解要求验证用户权限
```

## 验证规则

### LoginUser 验证

- getUserId() 不能返回 null
- getUsername() 不能为 null 或空字符串
- getRoles() 和 getPermissions() 不能为 null
- hasRole() 和 hasPermission() 应该正确实现逻辑

### SecurityContext 验证

- getLoginUser() 未登录时应返回 null
- isAuthenticated() 应该与 getLoginUser() != null 保持一致
- clear() 调用后，getLoginUser() 应该返回 null

### 注解验证

- @RequiresPermission 和 @RequiresRole 的 value 数组不应为空（除非特殊需求）
- logical 属性应该使用 Logical 枚举值

## 扩展性设计

### LoginUser 扩展

业务模块可以通过以下方式扩展 LoginUser：

1. **实现接口并添加字段**:
```java
public class BusinessLoginUser implements LoginUser {
    // 实现 LoginUser 接口的所有方法
    // 添加业务字段
    private String department;
    private String email;
    // ...
}
```

2. **使用组合模式**:
```java
public class BusinessLoginUser implements LoginUser {
    private LoginUser delegate;
    private String department;
    
    // 委托给 delegate，添加业务逻辑
}
```

### SecurityContext 扩展

实现类可以添加额外的方法：

```java
public class ExtendedSecurityContext implements SecurityContext {
    // 实现 SecurityContext 接口
    // 添加额外方法
    public void setLoginUser(LoginUser user) {
        // 实现设置逻辑
    }
}
```

## 性能考虑

1. **SecurityContext 获取**: 使用 ThreadLocal 或 Request Scope，避免频繁查找
2. **权限判断**: LoginUser 实现类可以缓存权限列表，避免重复计算
3. **注解扫描**: 权限检查框架应该缓存注解信息，避免重复扫描
4. **上下文清理**: 及时清理安全上下文，避免内存泄漏

