# atlas-common-infra-web

## 模块简介

`atlas-common-infra-web` 是 Atlas 项目的 Web 基础设施模块，提供全局异常处理、参数校验返回、Jackson 配置、TraceId Filter 等功能。该模块为所有业务模块提供统一的异常处理、参数校验、JSON 序列化和 TraceId 管理能力，确保 Web 层的规范性、一致性和可维护性。

## 主要功能

### 1. 全局异常处理

提供全局异常处理器，统一处理所有异常并返回标准的 `Result` 格式响应：
- 处理业务异常（`BusinessException`、`ParameterException`、`PermissionException`、`DataException`）
- 处理参数校验异常（`MethodArgumentNotValidException`、`ConstraintViolationException`）
- 处理 Spring MVC 异常（`HttpRequestMethodNotSupportedException`、`HttpMediaTypeNotSupportedException` 等）
- 处理系统异常（`Exception`、`RuntimeException`）
- 所有异常响应使用统一的 `Result` 格式
- 异常响应包含错误码、错误消息、TraceId
- 支持异常日志记录（记录异常堆栈信息）

### 2. 参数校验返回

提供参数校验异常的统一处理，将 Spring Validation 的校验错误转换为标准的 `Result` 格式响应：
- 处理 `@Valid` 和 `@Validated` 注解触发的参数校验异常
- 提取校验错误信息（字段名、错误消息）
- 将校验错误信息格式化为统一的响应格式
- 校验错误响应包含错误码、错误消息、字段错误列表
- 校验错误响应包含 TraceId

### 3. Jackson 配置

提供统一的 Jackson JSON 序列化配置，确保所有模块使用一致的 JSON 格式：
- 配置日期时间格式（统一使用 ISO-8601 格式）
- 配置空值处理（null 值处理策略）
- 配置序列化特性（忽略空值、格式化输出等）
- 配置反序列化特性（忽略未知属性、大小写不敏感等）
- 配置时区处理
- 配置自定义序列化器（如 Long 类型转 String，避免精度丢失）

### 4. TraceId Filter

提供 HTTP Filter 来处理 TraceId，在请求的最早阶段设置 TraceId，确保所有请求都有 TraceId：
- 从 HTTP 请求头 `X-Trace-Id` 获取 TraceId
- 如果请求头中没有 TraceId，则自动生成
- 将 TraceId 设置到 `TraceIdUtil`（复用 `atlas-common-infra-logging` 模块的工具类）
- 在响应头中添加 TraceId（可选）
- Filter 执行顺序要早于其他 Filter
- 支持配置 Filter 的 URL 匹配模式（排除静态资源等）

## 快速开始

### 添加依赖

在子模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.atlas</groupId>
    <artifactId>atlas-common-infra-web</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

#### 1. 全局异常处理

全局异常处理器会自动处理所有 Controller 层抛出的异常，无需手动配置。

**在 Service 层抛出异常**:

```java
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
import com.atlas.common.feature.core.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    public Result<UserVO> getUser(@PathVariable Long id) {
        // 无需 try-catch，全局异常处理器会自动处理
        UserVO user = userService.getUserById(id);
        return Result.success(user);
    }
}
```

#### 2. 参数校验

使用 Spring Validation 注解进行参数校验，全局异常处理器会自动处理校验错误。

```java
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

#### 3. Jackson 配置

Jackson 配置会自动应用，无需手动配置。所有 JSON 序列化都会使用统一的格式。

**日期时间序列化示例**:

```java
import java.time.LocalDateTime;

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
  "createTime": "2026-01-27T10:00:00.000"
}
```

**注意**: 
- Long 类型会自动序列化为 String，避免前端 JavaScript 精度丢失
- null 值会被忽略，不会出现在 JSON 中
- 日期时间使用 ISO-8601 格式

#### 4. TraceId Filter

TraceId Filter 会自动处理所有 HTTP 请求，无需手动配置。

**自动功能**:
- 从请求头 `X-Trace-Id` 获取 TraceId
- 如果请求头中没有 TraceId，则自动生成
- TraceId 自动设置到 `TraceIdUtil`，供业务代码和日志使用
- 请求结束后自动清理 TraceId

**在业务代码中使用 TraceId**:

```java
import com.atlas.common.infra.logging.trace.TraceIdUtil;

@Service
public class UserService {
    public void createUser(User user) {
        // 获取当前请求的 TraceId
        String traceId = TraceIdUtil.getTraceId();
        log.info("创建用户: username={}, traceId={}", user.getUsername(), traceId);
        // 业务逻辑...
    }
}
```

**在响应头中添加 TraceId（可选）**:

如果需要将 TraceId 添加到响应头，可以在配置中启用：

```java
@Configuration
public class CustomWebConfig {
    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilter() {
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>();
        TraceIdFilter filter = new TraceIdFilter();
        filter.setAddResponseHeader(true);  // 启用响应头
        registration.setFilter(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
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
public class CustomWebConfig {
    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilter() {
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceIdFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        // 排除静态资源（可选）
        // registration.addInitParameter("excludePatterns", "/static/**,/public/**");
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

- [快速开始指南](../../../specs/005-infra-web/quickstart.md) - 详细的使用指南和示例代码
- [功能规格说明](../../../specs/005-infra-web/spec.md) - 完整的功能需求说明
- [技术规划文档](../../../specs/005-infra-web/plan.md) - 技术实现方案
- [数据模型定义](../../../specs/005-infra-web/data-model.md) - 数据模型定义
- [技术调研文档](../../../specs/005-infra-web/research.md) - 技术决策和选型说明
- [API 合约定义](../../../specs/005-infra-web/contracts/README.md) - API 接口规范

## 技术栈

- **Java**: JDK 21
- **Spring Boot**: 4.0.1
- **Spring Validation**: 参数校验（Spring Boot 内置）
- **Jackson**: JSON 序列化（Spring Boot 内置）

## 版本信息

- **当前版本**: 1.0.0
- **最后更新**: 2026-01-27

