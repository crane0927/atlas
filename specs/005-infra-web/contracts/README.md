# Contracts

本目录用于存放该功能的接口契约定义。

## 说明

本功能主要提供 Web 基础设施组件（全局异常处理、参数校验返回、Jackson 配置、TraceId Filter），不涉及对外 API 接口，因此无需定义接口契约。

## 内部接口

### GlobalExceptionHandler

**描述**: 全局异常处理器，统一处理所有异常。

**包名**: `com.atlas.common.infra.web.exception.GlobalExceptionHandler`

**注解**: `@RestControllerAdvice`

**方法**: 见 data-model.md

### TraceIdFilter

**描述**: TraceId HTTP Filter，在请求的最早阶段设置 TraceId。

**包名**: `com.atlas.common.infra.web.filter.TraceIdFilter`

**接口**: `Filter`

**方法**: 见 data-model.md

### JacksonConfig

**描述**: Jackson JSON 序列化配置类。

**包名**: `com.atlas.common.infra.web.config.JacksonConfig`

**注解**: `@Configuration`

**Bean**: `ObjectMapper`

## 响应格式

### 异常响应格式

所有异常响应都使用统一的 `Result` 格式：

```json
{
  "code": "错误码",
  "message": "错误消息",
  "data": null,
  "timestamp": "2026-01-27T10:00:00",
  "traceId": "abc123def456"
}
```

### 参数校验错误响应格式

参数校验错误响应使用 `Result<ValidationError>` 格式：

```json
{
  "code": "参数校验错误码",
  "message": "参数校验失败",
  "data": {
    "errors": [
      {
        "field": "username",
        "message": "用户名不能为空"
      }
    ]
  },
  "timestamp": "2026-01-27T10:00:00",
  "traceId": "abc123def456"
}
```

## 错误码

错误码定义在 `atlas-common-feature-core` 模块的 `CommonErrorCode` 类中：

- `PARAMETER_ERROR`: 参数错误
- `SYSTEM_ERROR`: 系统错误
- `HTTP_METHOD_NOT_SUPPORTED`: HTTP 方法不支持
- `HTTP_MEDIA_TYPE_NOT_SUPPORTED`: HTTP 媒体类型不支持
- `MISSING_REQUEST_PARAMETER`: 缺少请求参数

