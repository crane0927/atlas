# 数据模型

## 概述

本文档定义了 `atlas-common-infra-web` 模块涉及的所有数据实体、配置参数和工具类。

## 核心实体

### GlobalExceptionHandler（异常处理器类）

**描述**: 全局异常处理器，统一处理所有异常并返回标准的 `Result` 格式响应。

**包名**: `com.atlas.common.infra.web.exception`

**方法定义**:

| 方法名 | 参数 | 返回类型 | 说明 | 必填 |
|--------|------|----------|------|------|
| handleBusinessException | BusinessException | Result<Void> | 处理业务异常 | 是 |
| handleParameterException | ParameterException | Result<Void> | 处理参数异常 | 是 |
| handlePermissionException | PermissionException | Result<Void> | 处理权限异常 | 是 |
| handleDataException | DataException | Result<Void> | 处理数据异常 | 是 |
| handleMethodArgumentNotValidException | MethodArgumentNotValidException | Result<ValidationError> | 处理方法参数校验异常 | 是 |
| handleConstraintViolationException | ConstraintViolationException | Result<ValidationError> | 处理约束校验异常 | 是 |
| handleHttpRequestMethodNotSupportedException | HttpRequestMethodNotSupportedException | Result<Void> | 处理 HTTP 方法不支持异常 | 是 |
| handleHttpMediaTypeNotSupportedException | HttpMediaTypeNotSupportedException | Result<Void> | 处理 HTTP 媒体类型不支持异常 | 是 |
| handleMissingServletRequestParameterException | MissingServletRequestParameterException | Result<Void> | 处理缺少请求参数异常 | 是 |
| handleException | Exception | Result<Void> | 处理系统异常 | 是 |

**约束规则**:
- 所有异常处理方法都返回 `Result` 格式响应
- 异常响应包含错误码、错误消息、TraceId
- 业务异常使用 INFO 级别日志记录
- 系统异常使用 ERROR 级别日志记录，包含堆栈信息

### ValidationError（校验错误类）

**描述**: 参数校验错误信息封装类，包含字段错误列表。

**包名**: `com.atlas.common.infra.web.exception`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| errors | List<FieldError> | 字段错误列表 | 是 | - |

**约束规则**:
- errors 不能为 null
- errors 可以为空列表（表示无错误）

### FieldError（字段错误类）

**描述**: 单个字段的错误信息。

**包名**: `com.atlas.common.infra.web.exception`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| field | String | 字段名 | 是 | - |
| message | String | 错误消息 | 是 | - |

**约束规则**:
- field 不能为 null 或空字符串
- message 不能为 null 或空字符串

### JacksonConfig（Jackson 配置类）

**描述**: Jackson JSON 序列化配置类，提供统一的 `ObjectMapper` Bean。

**包名**: `com.atlas.common.infra.web.config`

**配置项**:

| 配置项 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| dateFormat | SimpleDateFormat | 日期时间格式 | ISO-8601 |
| serializationInclusion | JsonInclude.Include | 序列化包含策略 | NON_NULL |
| writeDatesAsTimestamps | Boolean | 日期是否序列化为时间戳 | false |
| timeZone | TimeZone | 时区 | 系统默认时区 |
| failOnUnknownProperties | Boolean | 反序列化时是否忽略未知属性 | false |
| caseInsensitiveProperties | Boolean | 属性名是否大小写不敏感 | false |

**约束规则**:
- dateFormat 使用 ISO-8601 格式：`yyyy-MM-dd'T'HH:mm:ss.SSSZ`
- serializationInclusion 设置为 `NON_NULL`，忽略 null 值
- writeDatesAsTimestamps 设置为 false，使用字符串格式
- failOnUnknownProperties 设置为 false，忽略未知属性

### LongToStringSerializer（自定义序列化器）

**描述**: Long 类型自定义序列化器，将 Long 类型序列化为 String，避免前端 JavaScript 精度丢失。

**包名**: `com.atlas.common.infra.web.serializer`

**方法定义**:

| 方法名 | 参数 | 返回类型 | 说明 | 必填 |
|--------|------|----------|------|------|
| serialize | Long, JsonGenerator, SerializerProvider | void | 序列化 Long 为 String | 是 |

**约束规则**:
- 将 Long 值转换为 String 类型
- null 值不处理，由 Jackson 的 null 处理策略决定

### TraceIdFilter（TraceId Filter 类）

**描述**: TraceId HTTP Filter，在请求的最早阶段设置 TraceId。

**包名**: `com.atlas.common.infra.web.filter`

**方法定义**:

| 方法名 | 参数 | 返回类型 | 说明 | 必填 |
|--------|------|----------|------|------|
| doFilter | ServletRequest, ServletResponse, FilterChain | void | 过滤请求，设置 TraceId | 是 |

**配置项**:

| 配置项 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| traceIdHeader | String | TraceId 请求头名称 | X-Trace-Id |
| addResponseHeader | Boolean | 是否在响应头中添加 TraceId | false |
| order | Integer | Filter 执行顺序 | Ordered.HIGHEST_PRECEDENCE |

**约束规则**:
- 从请求头 `X-Trace-Id` 获取 TraceId
- 如果请求头中没有 TraceId，则自动生成
- TraceId 设置到 `TraceIdUtil`（复用 `atlas-common-infra-logging` 模块的工具类）
- 请求结束后清理 TraceId，避免内存泄漏

## 依赖关系

### 内部依赖

- **atlas-common-feature-core**: 
  - `Result` 类：统一响应格式
  - `BusinessException`、`ParameterException`、`PermissionException`、`DataException`：业务异常类
  - `CommonErrorCode`：通用错误码常量

- **atlas-common-infra-logging**（可选）:
  - `TraceIdUtil`：TraceId 管理工具类

### 外部依赖

- **spring-boot-starter-web**: Web 相关功能
- **spring-boot-starter-validation**: 参数校验功能
- **jackson-databind**: Jackson JSON 序列化

## 数据流

### 异常处理流程

```
Controller 抛出异常
    ↓
@RestControllerAdvice 捕获异常
    ↓
根据异常类型选择处理方法
    ↓
提取错误码和错误消息
    ↓
从 MDC 获取 TraceId
    ↓
构建 Result 响应
    ↓
记录异常日志
    ↓
返回响应
```

### 参数校验流程

```
Controller 方法参数校验
    ↓
@Valid/@Validated 触发校验
    ↓
校验失败抛出异常
    ↓
@RestControllerAdvice 捕获异常
    ↓
提取字段错误信息
    ↓
构建 ValidationError 对象
    ↓
构建 Result 响应
    ↓
返回响应
```

### TraceId Filter 流程

```
HTTP 请求到达
    ↓
TraceIdFilter.doFilter()
    ↓
从请求头获取 TraceId
    ↓
如果不存在，生成 TraceId
    ↓
设置 TraceId 到 TraceIdUtil
    ↓
继续处理请求
    ↓
请求处理完成
    ↓
清理 TraceId
```

## 序列化规范

### Result 序列化

```json
{
  "code": "错误码",
  "message": "错误消息",
  "data": null,
  "timestamp": "2026-01-27T10:00:00",
  "traceId": "abc123def456"
}
```

### ValidationError 序列化

```json
{
  "code": "参数校验错误码",
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

### Long 类型序列化

**序列化前**:
```json
{
  "id": 1234567890123456789
}
```

**序列化后**:
```json
{
  "id": "1234567890123456789"
}
```

## 版本兼容性

- 所有 API 保持向后兼容
- 新增字段使用 `@JsonIgnoreProperties(ignoreUnknown = true)` 支持向后兼容
- 废弃方法使用 `@Deprecated` 注解标记

## 使用限制

- `GlobalExceptionHandler` 只能处理 Controller 层抛出的异常
- `TraceIdFilter` 必须在其他 Filter 之前执行
- `LongToStringSerializer` 只序列化 Long 类型，不处理 Long 的包装类型

