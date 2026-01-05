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

#### 4. TraceId Filter

TraceId Filter 会自动处理所有 HTTP 请求，无需手动配置。

## 重要说明

- **全局异常处理**: 所有异常都会自动处理，无需在每个 Controller 中编写异常处理代码
- **参数校验**: 使用 `@Valid` 或 `@Validated` 注解，校验错误会自动转换为标准格式
- **Jackson 配置**: 统一的 JSON 序列化格式，Long 类型自动转换为 String，避免前端精度丢失
- **TraceId 管理**: TraceId Filter 在请求的最早阶段设置 TraceId，确保所有请求都有 TraceId

## 相关文档

- [快速开始指南](../../../specs/005-infra-web/quickstart.md) - 详细的使用指南和示例代码
- [功能规格说明](../../../specs/005-infra-web/spec.md) - 完整的功能需求说明
- [实施计划](../../../specs/005-infra-web/plan.md) - 技术实现方案
- [数据模型](../../../specs/005-infra-web/data-model.md) - 数据模型定义

## 技术栈

- **Java**: JDK 21
- **Spring Boot**: 4.0.1
- **Spring Validation**: 参数校验（Spring Boot 内置）
- **Jackson**: JSON 序列化（Spring Boot 内置）

## 版本信息

- **当前版本**: 1.0.0
- **最后更新**: 2026-01-27

