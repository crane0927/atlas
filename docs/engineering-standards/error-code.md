# 错误码规范

## 概述

本文档定义了 Atlas 项目的错误码规范和段位分配规则，确保所有模块的错误码统一、可追溯、不冲突。

## 错误码格式

### 格式定义

Atlas 项目采用 **6 位数字错误码格式**：`MMTTSS`

| 位置 | 长度 | 名称 | 范围 | 说明 |
|------|------|------|------|------|
| MM | 2 位 | 模块码 | 01-99 | 标识错误所属的模块 |
| TT | 2 位 | 类型码 | 00-99 | 标识错误的类型 |
| SS | 2 位 | 序号 | 00-99 | 同一类型下的错误序号 |

### 格式示例

- `032001`: 系统管理模块（03）的业务错误（20）第 1 个错误（01）
- `023001`: 认证模块（02）的权限错误（30）第 1 个错误（01）
- `011001`: 网关模块（01）的系统错误（10）第 1 个错误（01）

## 模块码分配

### 模块码分配表

| 模块码 | 模块名称 | 错误码范围 | 说明 |
|--------|----------|------------|------|
| 01 | atlas-gateway | 010000-019999 | API 网关模块 |
| 02 | atlas-auth | 020000-029999 | 认证授权模块 |
| 03 | atlas-system | 030000-039999 | 系统管理模块 |
| 04 | atlas-common-infra | 040000-049999 | 基础设施模块 |
| 05 | atlas-common-feature | 050000-059999 | 功能特性模块 |
| 06-99 | 业务模块 | 060000-999999 | 预留业务模块 |

**注意**: 每个模块分配 10000 个错误码空间（0000-9999），足够支持模块的所有错误场景。

## 错误类型码分配

### 类型码分配表

| 类型码 | 错误类型 | 说明 | 使用场景 |
|--------|----------|------|----------|
| 00-09 | 系统错误 | 系统级错误 | 服务不可用、超时、系统异常等 |
| 10-19 | 参数错误 | 参数校验错误 | 必填项缺失、格式错误、范围错误等 |
| 20-29 | 业务错误 | 业务逻辑错误 | 数据不存在、状态不正确、业务规则违反等 |
| 30-39 | 权限错误 | 权限相关错误 | 无权限访问、Token 失效、角色不足等 |
| 40-49 | 数据错误 | 数据相关错误 | 数据冲突、数据格式错误、数据完整性错误等 |
| 50-99 | 预留扩展 | 预留扩展类型 | 未来可能的新错误类型 |

### 类型码详细说明

#### 00-09: 系统错误

| 类型码 | 说明 | 示例错误码 |
|--------|------|------------|
| 00 | 通用系统错误 | `030000` - 系统内部错误 |
| 01 | 服务不可用 | `030001` - 服务暂时不可用 |
| 02 | 超时错误 | `030002` - 请求处理超时 |
| 03 | 资源不足 | `030003` - 系统资源不足 |
| 04-09 | 预留 | 预留扩展 |

#### 10-19: 参数错误

| 类型码 | 说明 | 示例错误码 |
|--------|------|------------|
| 10 | 通用参数错误 | `031000` - 参数错误 |
| 11 | 必填项缺失 | `031001` - 用户名不能为空 |
| 12 | 格式错误 | `031002` - 邮箱格式不正确 |
| 13 | 范围错误 | `031003` - 年龄必须在 0-150 之间 |
| 14-19 | 预留 | 预留扩展 |

#### 20-29: 业务错误

| 类型码 | 说明 | 示例错误码 |
|--------|------|------------|
| 20 | 通用业务错误 | `032000` - 业务处理失败 |
| 21 | 数据不存在 | `032001` - 用户不存在 |
| 22 | 状态不正确 | `032002` - 订单状态不允许此操作 |
| 23 | 业务规则违反 | `032003` - 余额不足 |
| 24-29 | 预留 | 预留扩展 |

#### 30-39: 权限错误

| 类型码 | 说明 | 示例错误码 |
|--------|------|------------|
| 30 | 通用权限错误 | `023000` - 权限不足 |
| 31 | Token 无效 | `023001` - Token 已失效 |
| 32 | 无权限访问 | `023002` - 无权限访问此资源 |
| 33 | 角色不足 | `023003` - 需要管理员角色 |
| 34-39 | 预留 | 预留扩展 |

#### 40-49: 数据错误

| 类型码 | 说明 | 示例错误码 |
|--------|------|------------|
| 40 | 通用数据错误 | `034000` - 数据错误 |
| 41 | 数据冲突 | `034001` - 用户名已存在 |
| 42 | 数据格式错误 | `034002` - 日期格式不正确 |
| 43 | 数据完整性错误 | `034003` - 外键约束违反 |
| 44-49 | 预留 | 预留扩展 |

## 错误码常量类结构

### 常量类命名规范

错误码常量类应命名为 `{Module}ErrorCode`，例如：
- `SystemErrorCode` - 系统管理模块错误码
- `AuthErrorCode` - 认证授权模块错误码
- `GatewayErrorCode` - 网关模块错误码

### 常量类结构示例

```java
package com.atlas.system.constant;

/**
 * 系统管理模块错误码常量
 * 
 * @author Atlas Team
 * @date 2025-01-27
 */
public class SystemErrorCode {
    
    // ========== 系统错误 (00-09) ==========
    /** 系统内部错误 */
    public static final String SYSTEM_ERROR = "030000";
    
    /** 服务暂时不可用 */
    public static final String SERVICE_UNAVAILABLE = "030001";
    
    /** 请求处理超时 */
    public static final String REQUEST_TIMEOUT = "030002";
    
    // ========== 参数错误 (10-19) ==========
    /** 参数错误 */
    public static final String PARAM_ERROR = "031000";
    
    /** 用户名不能为空 */
    public static final String USERNAME_REQUIRED = "031001";
    
    /** 邮箱格式不正确 */
    public static final String EMAIL_FORMAT_ERROR = "031002";
    
    // ========== 业务错误 (20-29) ==========
    /** 业务处理失败 */
    public static final String BUSINESS_ERROR = "032000";
    
    /** 用户不存在 */
    public static final String USER_NOT_FOUND = "032001";
    
    /** 订单状态不允许此操作 */
    public static final String ORDER_STATUS_INVALID = "032002";
    
    /** 余额不足 */
    public static final String INSUFFICIENT_BALANCE = "032003";
    
    // ========== 权限错误 (30-39) ==========
    /** 权限不足 */
    public static final String PERMISSION_DENIED = "033000";
    
    // ========== 数据错误 (40-49) ==========
    /** 数据错误 */
    public static final String DATA_ERROR = "034000";
    
    /** 用户名已存在 */
    public static final String USERNAME_EXISTS = "034001";
    
    /** 日期格式不正确 */
    public static final String DATE_FORMAT_ERROR = "034002";
    
    // 私有构造函数，防止实例化
    private SystemErrorCode() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
```

### 错误码与消息映射

建议使用统一的错误消息映射机制：

```java
package com.atlas.system.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统管理模块错误消息映射
 */
public class SystemErrorMessages {
    
    private static final Map<String, String> ERROR_MESSAGES = new HashMap<>();
    
    static {
        ERROR_MESSAGES.put(SystemErrorCode.USER_NOT_FOUND, "用户不存在");
        ERROR_MESSAGES.put(SystemErrorCode.USERNAME_EXISTS, "用户名已存在");
        ERROR_MESSAGES.put(SystemErrorCode.EMAIL_FORMAT_ERROR, "邮箱格式不正确");
        // ... 更多错误消息
    }
    
    /**
     * 获取错误消息
     * 
     * @param errorCode 错误码
     * @return 错误消息
     */
    public static String getMessage(String errorCode) {
        return ERROR_MESSAGES.getOrDefault(errorCode, "未知错误");
    }
}
```

## 错误码使用示例

### 在 Service 中使用

```java
package com.atlas.system.service;

import com.atlas.system.constant.SystemErrorCode;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(SystemErrorCode.USER_NOT_FOUND, "用户不存在: id=" + id);
        }
        return user;
    }
    
    public void createUser(User user) {
        // 检查用户名是否已存在
        if (userMapper.existsByUsername(user.getUsername())) {
            throw new BusinessException(SystemErrorCode.USERNAME_EXISTS, "用户名已存在: " + user.getUsername());
        }
        
        // 创建用户
        userMapper.insert(user);
    }
}
```

### 在 Controller 中使用

```java
package com.atlas.system.controller;

import com.atlas.system.constant.SystemErrorCode;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return Result.success(user);
        } catch (BusinessException e) {
            return Result.error(e.getErrorCode(), e.getMessage());
        }
    }
}
```

### 统一异常处理

```java
package com.atlas.system.exception;

import com.atlas.common.feature.core.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getErrorCode(), e.getMessage());
    }
}
```

## 错误码分配登记

### 登记表格式

建议维护错误码分配登记表，记录已使用的错误码：

| 错误码 | 模块 | 类型 | 说明 | 创建日期 | 创建人 |
|--------|------|------|------|----------|--------|
| 032001 | atlas-system | 业务错误 | 用户不存在 | 2025-01-27 | 系统 |
| 032002 | atlas-system | 业务错误 | 订单状态不允许此操作 | 2025-01-27 | 系统 |
| 023001 | atlas-auth | 权限错误 | Token 已失效 | 2025-01-27 | 系统 |

### 分配流程

1. **确定模块**: 确认错误所属模块
2. **确定类型**: 确认错误类型（系统/参数/业务/权限/数据）
3. **查找可用序号**: 在对应模块和类型下查找未使用的序号
4. **登记错误码**: 在错误码分配登记表中记录
5. **创建常量**: 在错误码常量类中添加常量定义

## 验证规则

1. **格式验证**: 错误码必须符合 6 位数字格式（MMTTSS）
2. **段位验证**: 错误码必须在对应模块的段位范围内
3. **唯一性验证**: 错误码在项目范围内必须唯一
4. **常量定义**: 所有错误码必须在常量类中定义，禁止硬编码

## 工具支持

### 代码检查

可以使用以下工具检查错误码规范：

- **自定义 Checkstyle 规则**: 检查错误码格式
- **Maven Enforcer**: 强制错误码规范
- **代码审查**: 人工检查错误码使用

### IDE 支持

- **IntelliJ IDEA**: 使用常量检查，避免硬编码错误码
- **Eclipse**: 使用常量重构功能

## 常见问题

### Q1: 如何分配新的错误码？

**A**: 
1. 确定模块和错误类型
2. 在对应段位下查找未使用的序号
3. 在错误码常量类中添加常量
4. 在错误码分配登记表中记录

### Q2: 错误码不够用怎么办？

**A**: 每个模块有 10000 个错误码空间，每个类型有 100 个错误码。如果不够用，可以：
- 检查是否有重复或可以合并的错误
- 使用更细粒度的类型码（如 20-29 可以细分）
- 申请新的模块码（06-99 预留）

### Q3: 错误码可以跨模块使用吗？

**A**: 不建议。每个模块应使用自己的错误码段位，保持模块独立性。

## 参考

- [项目宪法 - 模块职责边界](.specify/memory/constitution.md)
- [错误码段位分配表](#错误码段位分配表)

