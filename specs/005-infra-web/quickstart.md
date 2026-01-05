# 快速开始指南

## 概述

本指南介绍如何使用 `atlas-common-infra-web` 模块提供的 Web 基础设施功能，包括全局异常处理、参数校验返回、Jackson 配置和 TraceId Filter。

## 前置条件

- JDK 21
- Spring Boot 4.0.1
- Maven 3.8+
- 已安装 `atlas-common-feature-core` 模块
- 可选：已安装 `atlas-common-infra-logging` 模块（如果使用 TraceId Filter）

## 添加依赖

在子模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.atlas</groupId>
    <artifactId>atlas-common-infra-web</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 使用示例

### 1. 全局异常处理

全局异常处理器会自动处理所有 Controller 层抛出的异常，无需手动配置。

**在 Service 层抛出异常**:

```java
package com.atlas.system.service;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.constant.CommonErrorCode;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.DATA_NOT_FOUND, "用户不存在: id=" + id);
        }
        return user;
    }
}
```

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
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/users/{id}")
    public Result<UserVO> getUser(@PathVariable Long id) {
        // 无需 try-catch，全局异常处理器会自动处理
        UserVO user = userService.getUserById(id);
        return Result.success(user);
    }
}
```

**异常响应示例**:

```json
{
  "code": "054001",
  "message": "用户不存在: id=123",
  "data": null,
  "timestamp": "2026-01-27T10:00:00",
  "traceId": "abc123def456"
}
```

### 2. 参数校验

使用 Spring Validation 注解进行参数校验，全局异常处理器会自动处理校验错误。

**定义 DTO 类**:

```java
package com.atlas.system.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserDTO {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在 3-20 之间")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在 6-20 之间")
    private String password;
}
```

**在 Controller 中使用**:

```java
package com.atlas.system.controller;

import com.atlas.common.feature.core.result.Result;
import com.atlas.system.model.dto.CreateUserDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    
    @PostMapping("/users")
    public Result<Void> createUser(@Valid @RequestBody CreateUserDTO dto) {
        // 参数校验失败会自动抛出异常，全局异常处理器会自动处理
        userService.createUser(dto);
        return Result.success();
    }
}
```

**参数校验错误响应示例**:

```json
{
  "code": "051001",
  "message": "参数校验失败",
  "data": {
    "errors": [
      {
        "field": "username",
        "message": "用户名不能为空"
      },
      {
        "field": "email",
        "message": "邮箱格式不正确"
      }
    ]
  },
  "timestamp": "2026-01-27T10:00:00",
  "traceId": "abc123def456"
}
```

### 3. Jackson 配置

Jackson 配置会自动应用，无需手动配置。所有 JSON 序列化都会使用统一的格式。

**日期时间序列化**:

```java
package com.atlas.system.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private LocalDateTime createTime;  // 自动序列化为 ISO-8601 格式
}
```

**序列化结果**:

```json
{
  "id": "1234567890123456789",
  "username": "test",
  "createTime": "2026-01-27T10:00:00"
}
```

**注意**: 
- Long 类型会自动序列化为 String，避免前端 JavaScript 精度丢失
- null 值会被忽略，不会出现在 JSON 中
- 日期时间使用 ISO-8601 格式

### 4. TraceId Filter

TraceId Filter 会自动处理所有 HTTP 请求，无需手动配置。

**自动功能**:
- 从请求头 `X-Trace-Id` 获取 TraceId
- 如果请求头中没有 TraceId，则自动生成
- TraceId 自动设置到 `TraceIdUtil`，供业务代码和日志使用
- 请求结束后自动清理 TraceId

**在业务代码中使用 TraceId**:

```java
package com.atlas.system.service;

import com.atlas.common.infra.logging.trace.TraceIdUtil;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    public void createUser(User user) {
        // 获取当前请求的 TraceId
        String traceId = TraceIdUtil.getTraceId();
        log.info("创建用户: username={}, traceId={}", user.getUsername(), traceId);
        
        // 业务逻辑
        userMapper.insert(user);
    }
}
```

**在日志中自动输出 TraceId**:

日志格式中已经包含 TraceId，无需手动添加：

```
2026-01-27 10:00:00.123 [http-nio-8080-exec-1] INFO  com.atlas.system.service.UserService [TraceId: abc123def456] - 创建用户: username=test
```

**在响应头中添加 TraceId（可选）**:

如果需要将 TraceId 添加到响应头，可以在配置中启用：

```java
@Configuration
public class WebConfig {
    
    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilter() {
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceIdFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        // 启用响应头添加 TraceId
        registration.getFilter().setAddResponseHeader(true);
        return registration;
    }
}
```

## 配置说明

### 全局异常处理器配置

全局异常处理器无需配置，Spring Boot 会自动扫描和注册。

如果需要自定义异常处理行为，可以创建自己的 `GlobalExceptionHandler`：

```java
@RestControllerAdvice
public class CustomGlobalExceptionHandler extends GlobalExceptionHandler {
    
    // 可以覆盖或扩展异常处理方法
}
```

### Jackson 配置

Jackson 配置会自动应用，无需手动配置。

如果需要自定义配置，可以创建自己的 `JacksonConfig`：

```java
@Configuration
public class CustomJacksonConfig {
    
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 自定义配置
        return mapper;
    }
}
```

### TraceId Filter 配置

TraceId Filter 会自动注册，无需手动配置。

如果需要自定义配置，可以创建 `FilterRegistrationBean`：

```java
@Configuration
public class WebConfig {
    
    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilter() {
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceIdFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        // 排除静态资源
        registration.addInitParameter("excludePatterns", "/static/**,/public/**");
        return registration;
    }
}
```

## 最佳实践

### 1. 异常处理

- **业务异常**: 在 Service 层抛出 `BusinessException`，全局异常处理器会自动处理
- **参数校验**: 使用 `@Valid` 或 `@Validated` 注解，全局异常处理器会自动处理校验错误
- **系统异常**: 系统异常会被全局异常处理器捕获，返回通用错误响应

### 2. 参数校验

- **DTO 类**: 使用 `@Valid` 注解进行方法参数校验
- **方法参数**: 使用 `@Validated` 注解进行方法参数校验
- **分组校验**: 使用 `@Validated` 注解的 `groups` 属性进行分组校验

### 3. JSON 序列化

- **日期时间**: 使用 `LocalDateTime`、`Date` 等类型，会自动序列化为 ISO-8601 格式
- **Long 类型**: Long 类型会自动序列化为 String，避免前端精度丢失
- **null 值**: null 值会被忽略，不会出现在 JSON 中

### 4. TraceId 使用

- **自动处理**: TraceId Filter 会自动处理所有 HTTP 请求，无需手动设置
- **业务代码**: 使用 `TraceIdUtil.getTraceId()` 获取 TraceId
- **日志输出**: TraceId 会自动输出到日志中，无需手动添加

## 常见问题

### Q1: 如何自定义异常处理行为？

A: 可以创建自己的 `GlobalExceptionHandler`，继承或覆盖默认的异常处理方法。

### Q2: 如何自定义 Jackson 配置？

A: 可以创建自己的 `JacksonConfig`，使用 `@Primary` 注解覆盖默认配置。

### Q3: TraceId Filter 和 TraceIdInterceptor 有什么区别？

A: 
- Filter 执行顺序早于 Interceptor，在请求的最早阶段执行
- Filter 可以处理所有 HTTP 请求，包括静态资源请求
- Interceptor 只能处理 Controller 层的请求
- 建议优先使用 Filter，Interceptor 作为备选方案

### Q4: 如何排除某些 URL 的 TraceId Filter？

A: 可以在 `FilterRegistrationBean` 中配置 `excludePatterns` 参数。

### Q5: 如何自定义参数校验错误格式？

A: 可以创建自己的 `GlobalExceptionHandler`，覆盖 `handleMethodArgumentNotValidException` 方法。

## 相关文档

- [功能规格说明](spec.md)
- [技术规划文档](plan.md)
- [数据模型定义](data-model.md)
- [技术调研文档](research.md)
- [API 合约定义](contracts/README.md)

