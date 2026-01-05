# 快速开始指南

## 概述

本指南帮助开发人员快速了解和使用 `atlas-common-feature-core` 模块提供的核心功能特性。

## 前置要求

- JDK 21 已安装并配置
- Maven 3.8+ 已安装并配置
- 项目已继承父 POM（atlas）

## 模块依赖

在子模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.atlas</groupId>
    <artifactId>atlas-common-feature-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 快速开始

### 1. 使用 Result 包装 API 响应

**在 Controller 中使用**:

```java
package com.atlas.system.controller;

import com.atlas.common.feature.core.result.Result;
import com.atlas.system.model.vo.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    public Result<UserVO> getUser(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        return Result.success(user);
    }
}
```

**成功响应示例**:

```json
{
  "code": "000000",
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin"
  },
  "timestamp": 1704067200000,
  "traceId": "abc123def456"
}
```

**注意**: `traceId` 会自动从 MDC（Mapped Diagnostic Context）中获取。如果 MDC 中没有设置 traceId，则该字段为 null（使用 `@JsonInclude(NON_NULL)` 时不会序列化）。

**失败响应示例**:

```json
{
  "code": "052001",
  "message": "用户不存在",
  "data": null,
  "timestamp": 1704067200000
}
```

### 2. 使用异常体系

**在 Service 中抛出异常**:

```java
package com.atlas.system.service.impl;

import com.atlas.common.feature.core.constant.CommonErrorCode;
import com.atlas.common.feature.core.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + id);
        }
        return user;
    }
}
```

**全局异常处理器**:

```java
package com.atlas.system.exception;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.exception.ParameterException;
import com.atlas.common.feature.core.exception.PermissionException;
import com.atlas.common.feature.core.exception.DataException;
import com.atlas.common.feature.core.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getErrorCode(), e.getMessage());
    }
    
    @ExceptionHandler(ParameterException.class)
    public Result<Void> handleParameterException(ParameterException e) {
        return Result.error(e.getErrorCode(), e.getMessage());
    }
    
    @ExceptionHandler(PermissionException.class)
    public Result<Void> handlePermissionException(PermissionException e) {
        return Result.error(e.getErrorCode(), e.getMessage());
    }
    
    @ExceptionHandler(DataException.class)
    public Result<Void> handleDataException(DataException e) {
        return Result.error(e.getErrorCode(), e.getMessage());
    }
}
```

### 3. 使用分页对象

**在 Controller 中使用**:

```java
package com.atlas.system.controller;

import com.atlas.common.feature.core.page.PageResult;
import com.atlas.system.model.vo.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    
    @GetMapping("/users")
    public Result<PageResult<UserVO>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<UserVO> userList = userService.getUsers(page, size);
        Long total = userService.countUsers();
        PageResult<UserVO> pageResult = PageResult.of(userList, total, page, size);
        return Result.success(pageResult);
    }
}
```

**分页响应示例**:

```json
{
  "code": "000000",
  "message": "操作成功",
  "data": {
    "list": [
      { "id": 1, "username": "admin" },
      { "id": 2, "username": "user" }
    ],
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10,
    "traceId": "abc123def456"
  },
  "timestamp": 1704067200000,
  "traceId": "abc123def456"
}
```

**注意**: `PageResult` 和 `Result` 都会自动从 MDC 中获取 traceId，用于分布式追踪。

### 4. 使用错误码常量

**在代码中使用错误码**:

```java
import com.atlas.common.feature.core.constant.CommonErrorCode;
import com.atlas.common.feature.core.exception.BusinessException;

// 使用错误码
throw new BusinessException(CommonErrorCode.DATA_NOT_FOUND, "用户不存在");
```

### 5. 使用基础常量

**使用 HTTP 状态码常量**:

```java
import com.atlas.common.feature.core.constant.HttpStatus;

int statusCode = HttpStatus.OK;
```

**使用通用常量**:

```java
import com.atlas.common.feature.core.constant.CommonConstants;

int defaultPage = CommonConstants.DEFAULT_PAGE;
int defaultSize = CommonConstants.DEFAULT_SIZE;
```

## 常见问题

### Q1: 如何自定义成功消息？

**A**: 使用 `Result.success(String message, T data)` 方法：

```java
return Result.success("查询成功", user);
```

### Q2: 如何创建带数据的失败响应？

**A**: 使用 `Result.error(String code, String message, T data)` 方法：

```java
return Result.error(CommonErrorCode.BUSINESS_ERROR, "操作失败", errorData);
```

### Q3: 如何处理分页边界情况？

**A**: PageResult 会自动处理边界情况：

```java
// total = 0 时，pages = 0, list = []
PageResult<User> pageResult = PageResult.of(Collections.emptyList(), 0L, 1, 10);

// page > pages 时，返回空列表（需要在 Service 层处理）
if (page > pages) {
    return PageResult.of(Collections.emptyList(), total, page, size);
}
```

### Q4: 如何判断 Result 是否成功？

**A**: 使用 `isSuccess()` 方法：

```java
Result<User> result = Result.success(user);
if (result.isSuccess()) {
    User user = result.getData();
}
```

### Q5: 如何获取分页信息？

**A**: 使用 PageResult 的便捷方法：

```java
PageResult<User> pageResult = PageResult.of(userList, total, page, size);
boolean hasNext = pageResult.hasNext();
boolean hasPrevious = pageResult.hasPrevious();
boolean isFirst = pageResult.isFirst();
boolean isLast = pageResult.isLast();
```

### Q6: traceId 是如何工作的？

**A**: `Result` 和 `PageResult` 会自动从 MDC（Mapped Diagnostic Context）中获取 traceId：

```java
import org.slf4j.MDC;

// 在拦截器或过滤器中设置 traceId
MDC.put("traceId", "abc123def456");

// Result 和 PageResult 会自动包含 traceId
Result<User> result = Result.success(user);
// result.getTraceId() 返回 "abc123def456"

// 清理 MDC（通常在请求结束时）
MDC.clear();
```

**最佳实践**:
- 在网关或拦截器中统一设置 traceId
- 使用 UUID 或雪花算法生成 traceId
- 确保 traceId 在整个请求生命周期中传递

## 下一步

1. 阅读完整的 API 文档（contracts/README.md）
2. 查看数据模型定义（data-model.md）
3. 参考使用示例实现自己的业务逻辑
4. 参与代码审查，确保规范执行

## 相关文档

- [功能规格说明](spec.md)
- [实施计划](plan.md)
- [技术调研](research.md)
- [数据模型](data-model.md)
- [API 合约](contracts/README.md)

