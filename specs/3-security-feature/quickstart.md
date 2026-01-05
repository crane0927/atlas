# 快速开始指南

## 概述

本指南帮助您快速了解和使用 `atlas-common-feature-security` 模块提供的安全功能抽象接口和注解。

## 模块介绍

`atlas-common-feature-security` 是一个抽象安全功能模块，提供：

1. **LoginUser 接口**: 登录用户信息模型
2. **权限注解**: @RequiresPermission、@RequiresRole
3. **安全上下文接口**: SecurityContext、SecurityContextHolder

**重要**: 本模块只提供抽象接口和注解定义，不包含具体实现。具体实现需要在其他模块中完成。

## 核心概念

### LoginUser - 登录用户模型

LoginUser 是一个接口，定义了登录用户的基本信息和权限信息：

```java
public interface LoginUser {
    Object getUserId();
    String getUsername();
    List<String> getRoles();
    List<String> getPermissions();
    boolean hasRole(String role);
    boolean hasPermission(String permission);
}
```

### SecurityContext - 安全上下文

SecurityContext 是一个接口，提供获取当前登录用户的方法：

```java
public interface SecurityContext {
    LoginUser getLoginUser();
    boolean isAuthenticated();
    void clear();
}
```

### SecurityContextHolder - 上下文持有者

SecurityContextHolder 提供静态方法，方便获取安全上下文：

```java
public abstract class SecurityContextHolder {
    public static SecurityContext getContext();
    public static LoginUser getLoginUser();
    public static boolean isAuthenticated();
}
```

### 权限注解

提供两个权限注解用于声明权限要求：

- `@RequiresPermission`: 声明权限要求
- `@RequiresRole`: 声明角色要求

## 使用示例

### 示例 1: 实现 LoginUser 接口

```java
package com.example.security;

import com.atlas.common.feature.security.user.LoginUser;
import java.util.List;

/**
 * 默认登录用户实现
 */
public class DefaultLoginUser implements LoginUser {
    private Long userId;
    private String username;
    private List<String> roles;
    private List<String> permissions;
    
    public DefaultLoginUser(Long userId, String username, 
                           List<String> roles, List<String> permissions) {
        this.userId = userId;
        this.username = username;
        this.roles = roles != null ? roles : List.of();
        this.permissions = permissions != null ? permissions : List.of();
    }
    
    @Override
    public Object getUserId() {
        return userId;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public List<String> getRoles() {
        return roles;
    }
    
    @Override
    public List<String> getPermissions() {
        return permissions;
    }
    
    @Override
    public boolean hasRole(String role) {
        return roles.contains(role);
    }
    
    @Override
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
}
```

### 示例 2: 实现 SecurityContext 接口

```java
package com.example.security;

import com.atlas.common.feature.security.context.SecurityContext;
import com.atlas.common.feature.security.user.LoginUser;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 ThreadLocal 的安全上下文实现
 */
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
    
    /**
     * 设置当前登录用户（实现类提供的方法）
     */
    public void setLoginUser(LoginUser user) {
        if (user != null) {
            context.set(user);
        } else {
            context.remove();
        }
    }
}
```

### 示例 3: 扩展 SecurityContextHolder

```java
package com.example.security;

import com.atlas.common.feature.security.context.SecurityContext;
import com.atlas.common.feature.security.context.SecurityContextHolder;
import com.atlas.common.feature.security.user.LoginUser;

/**
 * 扩展 SecurityContextHolder，提供具体实现
 */
public class CustomSecurityContextHolder extends SecurityContextHolder {
    private static final ThreadLocalSecurityContext context = new ThreadLocalSecurityContext();
    
    @Override
    public static SecurityContext getContext() {
        return context;
    }
    
    /**
     * 设置当前登录用户
     */
    public static void setLoginUser(LoginUser user) {
        context.setLoginUser(user);
    }
}
```

### 示例 4: 在 Controller 中使用

```java
package com.example.controller;

import com.atlas.common.feature.security.annotation.RequiresPermission;
import com.atlas.common.feature.security.annotation.RequiresRole;
import com.atlas.common.feature.security.context.SecurityContextHolder;
import com.atlas.common.feature.security.user.LoginUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器示例
 */
@RestController
@RequestMapping("/api/users")
@RequiresRole("USER") // 类级别：需要 USER 角色
public class UserController {
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Object getCurrentUser() {
        LoginUser user = SecurityContextHolder.getLoginUser();
        if (user == null) {
            return "未登录";
        }
        return Map.of(
            "userId", user.getUserId(),
            "username", user.getUsername(),
            "roles", user.getRoles(),
            "permissions", user.getPermissions()
        );
    }
    
    /**
     * 需要 user:read 权限
     */
    @GetMapping("/list")
    @RequiresPermission("user:read")
    public Object listUsers() {
        // 获取用户列表
        return List.of();
    }
    
    /**
     * 需要 user:write 权限或 ADMIN 角色
     */
    @PostMapping("/create")
    @RequiresPermission(value = "user:write", logical = Logical.OR)
    @RequiresRole(value = "ADMIN", logical = Logical.OR)
    public Object createUser() {
        // 创建用户
        return "success";
    }
}
```

### 示例 5: 扩展 LoginUser 添加业务字段

```java
package com.example.security;

import com.atlas.common.feature.security.user.LoginUser;
import java.util.List;

/**
 * 业务登录用户，扩展了部门信息
 */
public class BusinessLoginUser implements LoginUser {
    private Long userId;
    private String username;
    private List<String> roles;
    private List<String> permissions;
    
    // 业务扩展字段
    private String department;
    private String email;
    
    // 构造函数
    public BusinessLoginUser(Long userId, String username, 
                           List<String> roles, List<String> permissions,
                           String department, String email) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
        this.department = department;
        this.email = email;
    }
    
    // 实现 LoginUser 接口的所有方法
    @Override
    public Object getUserId() {
        return userId;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public List<String> getRoles() {
        return roles;
    }
    
    @Override
    public List<String> getPermissions() {
        return permissions;
    }
    
    @Override
    public boolean hasRole(String role) {
        return roles.contains(role);
    }
    
    @Override
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    
    // 业务扩展方法
    public String getDepartment() {
        return department;
    }
    
    public String getEmail() {
        return email;
    }
}
```

## 权限注解使用说明

### @RequiresPermission

**功能**: 声明方法或类需要指定的权限

**属性**:
- `value`: 权限列表（String[]）
- `logical`: 逻辑关系（Logical.AND 或 Logical.OR），默认 AND

**示例**:
```java
// 需要单个权限
@RequiresPermission("user:read")

// 需要多个权限（AND 关系）
@RequiresPermission({"user:read", "user:write"})

// 需要多个权限（OR 关系）
@RequiresPermission(value = {"user:read", "user:write"}, logical = Logical.OR)
```

### @RequiresRole

**功能**: 声明方法或类需要指定的角色

**属性**:
- `value`: 角色列表（String[]）
- `logical`: 逻辑关系（Logical.AND 或 Logical.OR），默认 AND

**示例**:
```java
// 需要单个角色
@RequiresRole("ADMIN")

// 需要多个角色（OR 关系）
@RequiresRole(value = {"ADMIN", "MANAGER"}, logical = Logical.OR)
```

### 注解作用域

- **类级别**: 应用于整个类，所有方法都需要满足注解要求
- **方法级别**: 应用于单个方法，会覆盖类级别的注解
- **组合使用**: 可以同时使用 @RequiresPermission 和 @RequiresRole

## 注意事项

1. **抽象设计**: 本模块只提供抽象接口和注解，不包含具体实现
2. **实现要求**: 使用本模块前，需要先实现 SecurityContext 和 SecurityContextHolder
3. **权限检查**: 注解只作为元数据标记，权限检查逻辑需要在权限检查框架中实现
4. **线程安全**: 如果使用 ThreadLocal 实现，需要注意线程安全
5. **上下文清理**: 请求结束后应该清理安全上下文，避免内存泄漏

## 下一步

1. **实现安全上下文**: 根据项目需求实现 SecurityContext 和 SecurityContextHolder
2. **实现权限检查框架**: 基于注解实现权限检查逻辑
3. **集成认证框架**: 集成 Spring Security 或其他认证框架
4. **扩展用户模型**: 根据业务需求扩展 LoginUser 接口

## 相关文档

- [功能规格说明](./spec.md)
- [实现计划](./plan.md)
- [数据模型](./data-model.md)
- [技术调研](./research.md)

