# atlas-common-feature-security

## 模块简介

`atlas-common-feature-security` 是 Atlas 项目的安全功能特性模块，提供登录用户信息模型（LoginUser）、权限注解、安全上下文获取接口等抽象定义。该模块采用抽象设计，定义接口和注解，不绑定具体实现，为业务模块提供统一的安全功能抽象，支持多种安全实现方案的灵活切换。

## 主要功能

### 1. LoginUser 接口

提供登录用户信息模型接口，封装当前登录用户的基本信息和权限信息：
- 定义用户基本信息获取方法（getUserId、getUsername）
- 定义权限和角色获取方法（getRoles、getPermissions）
- 定义权限判断方法（hasRole、hasPermission）
- 支持业务模块扩展自定义字段

### 2. 权限注解

提供权限相关的注解，用于在方法或类上声明权限要求：
- `@RequiresPermission`: 权限检查注解，支持单个或多个权限的声明
- `@RequiresRole`: 角色检查注解，支持单个或多个角色的声明
- `Logical`: 逻辑关系枚举（AND/OR），支持权限和角色的灵活组合
- 支持类级别和方法级别

### 3. 安全上下文接口

提供安全上下文获取的抽象接口：
- `SecurityContext`: 安全上下文接口，提供获取当前登录用户的方法
- `SecurityContextHolder`: 安全上下文持有者，提供静态方法获取安全上下文
- 支持多种实现方案（ThreadLocal、Request Scope 等）

### 4. 安全异常接口（可选）

提供安全相关的异常接口定义：
- `AuthenticationException`: 认证异常接口，表示用户未认证或认证失败
- `AuthorizationException`: 授权异常接口，表示用户权限不足

## 快速开始

### 添加依赖

在子模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.atlas</groupId>
    <artifactId>atlas-common-feature-security</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

#### 1. 实现 LoginUser 接口

```java
import com.atlas.common.feature.security.user.LoginUser;
import java.util.List;

public class DefaultLoginUser implements LoginUser {
    private Long userId;
    private String username;
    private List<String> roles;
    private List<String> permissions;
    
    // 实现所有必需的方法
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

#### 2. 使用权限注解

```java
import com.atlas.common.feature.security.annotation.RequiresPermission;
import com.atlas.common.feature.security.annotation.RequiresRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiresRole("USER") // 类级别：需要 USER 角色
public class UserController {
    
    @GetMapping("/users")
    @RequiresPermission("user:read") // 方法级别：需要 user:read 权限
    public Result<List<UserVO>> getUsers() {
        // ...
    }
    
    @GetMapping("/users/{id}")
    @RequiresPermission(value = {"user:read", "user:write"}, logical = Logical.OR)
    public Result<UserVO> getUser(@PathVariable Long id) {
        // ...
    }
}
```

#### 3. 使用 SecurityContextHolder 获取当前用户

```java
import com.atlas.common.feature.security.context.SecurityContextHolder;
import com.atlas.common.feature.security.user.LoginUser;

@RestController
public class UserController {
    
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        LoginUser user = SecurityContextHolder.getLoginUser();
        if (user == null) {
            return Result.error("未登录");
        }
        // 使用用户信息
        return Result.success(user);
    }
    
    @GetMapping("/check")
    public Result<String> checkAuth() {
        if (SecurityContextHolder.isAuthenticated()) {
            return Result.success("已认证");
        }
        return Result.error("未认证");
    }
}
```

#### 4. 扩展 LoginUser 添加业务字段

```java
import com.atlas.common.feature.security.user.LoginUser;

public class BusinessLoginUser implements LoginUser {
    // 实现 LoginUser 接口的所有方法
    // ...
    
    // 添加业务扩展字段
    private String department;
    private String email;
    
    public String getDepartment() {
        return department;
    }
    
    public String getEmail() {
        return email;
    }
}
```

## 重要说明

**本模块只提供抽象接口和注解定义，不包含具体实现。**

- 使用本模块前，需要先实现 `SecurityContext` 和 `SecurityContextHolder`
- 权限注解只作为元数据标记，权限检查逻辑需要在权限检查框架中实现
- 具体的安全实现（如认证、授权）需要在其他模块中完成

## 相关文档

- [快速开始指南](../../../specs/3-security-feature/quickstart.md) - 详细的使用指南和示例代码
- [功能规格说明](../../../specs/3-security-feature/spec.md) - 完整的功能需求说明
- [实施计划](../../../specs/3-security-feature/plan.md) - 技术实现方案
- [数据模型](../../../specs/3-security-feature/data-model.md) - 数据模型定义
- [技术调研](../../../specs/3-security-feature/research.md) - 技术决策和设计依据

## 技术栈

- **Java**: JDK 21
- **Spring Boot**: 4.0.1（仅用于测试）
- **无强制外部依赖**: 保持抽象层的纯净性

## 版本信息

- **当前版本**: 1.0.0
- **最后更新**: 2026-01-27

