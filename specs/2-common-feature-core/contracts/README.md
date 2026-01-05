# API 合约文档

## 概述

本文档定义了 `atlas-common-feature-core` 模块提供的公共 API 合约。该模块不提供 HTTP 接口，而是提供可复用的 Java 类和工具。

## 核心 API

### Result<T> 类

**包名**: `com.atlas.common.feature.core.result.Result`

**描述**: 统一 API 响应包装类

**公共方法**:

| 方法签名 | 返回类型 | 说明 |
|----------|----------|------|
| `static <T> Result<T> success(T data)` | Result<T> | 创建成功响应 |
| `static <T> Result<T> success(String message, T data)` | Result<T> | 创建成功响应（自定义消息） |
| `static <T> Result<T> error(String code, String message)` | Result<T> | 创建失败响应 |
| `static <T> Result<T> error(String code, String message, T data)` | Result<T> | 创建失败响应（带数据） |
| `boolean isSuccess()` | boolean | 判断是否成功 |
| `T getData()` | T | 获取响应数据 |
| `String getCode()` | String | 获取状态码 |
| `String getMessage()` | String | 获取消息 |
| `Long getTimestamp()` | Long | 获取时间戳 |

**使用示例**:

```java
// 成功响应
Result<User> result = Result.success(user);
// 或
Result<User> result = Result.success("查询成功", user);

// 失败响应
Result<Void> result = Result.error(CommonErrorCode.USER_NOT_FOUND, "用户不存在");
```

### PageResult<T> 类

**包名**: `com.atlas.common.feature.core.page.PageResult`

**描述**: 统一分页响应对象

**公共方法**:

| 方法签名 | 返回类型 | 说明 |
|----------|----------|------|
| `static <T> PageResult<T> of(List<T> list, Long total, Integer page, Integer size)` | PageResult<T> | 创建分页对象 |
| `static <T> PageResult<T> of(List<T> list, Long total)` | PageResult<T> | 创建分页对象（默认 page=1, size=list.size()） |
| `List<T> getList()` | List<T> | 获取数据列表 |
| `Long getTotal()` | Long | 获取总记录数 |
| `Integer getPage()` | Integer | 获取当前页码 |
| `Integer getSize()` | Integer | 获取每页大小 |
| `Integer getPages()` | Integer | 获取总页数 |
| `boolean hasPrevious()` | boolean | 是否有上一页 |
| `boolean hasNext()` | boolean | 是否有下一页 |
| `boolean isFirst()` | boolean | 是否是第一页 |
| `boolean isLast()` | boolean | 是否是最后一页 |

**使用示例**:

```java
// 创建分页对象
PageResult<User> pageResult = PageResult.of(userList, totalCount, page, size);
// 或
PageResult<User> pageResult = PageResult.of(userList, totalCount);
```

### BusinessException 类

**包名**: `com.atlas.common.feature.core.exception.BusinessException`

**描述**: 基础业务异常类

**构造函数**:

| 构造函数 | 说明 |
|----------|------|
| `BusinessException(String errorCode, String message)` | 创建异常（错误码和消息） |
| `BusinessException(String errorCode, String message, Throwable cause)` | 创建异常（错误码、消息和原因） |

**公共方法**:

| 方法签名 | 返回类型 | 说明 |
|----------|----------|------|
| `String getErrorCode()` | String | 获取错误码 |
| `String getMessage()` | String | 获取错误消息 |
| `Throwable getCause()` | Throwable | 获取异常原因 |

**使用示例**:

```java
// 抛出业务异常
throw new BusinessException(CommonErrorCode.USER_NOT_FOUND, "用户不存在");
// 或带原因
throw new BusinessException(CommonErrorCode.USER_NOT_FOUND, "用户不存在", cause);
```

### ParameterException 类

**包名**: `com.atlas.common.feature.core.exception.ParameterException`

**描述**: 参数异常类

**构造函数**: 同 BusinessException

**使用示例**:

```java
throw new ParameterException(CommonErrorCode.PARAM_REQUIRED, "用户名不能为空");
```

### PermissionException 类

**包名**: `com.atlas.common.feature.core.exception.PermissionException`

**描述**: 权限异常类

**构造函数**: 同 BusinessException

**使用示例**:

```java
throw new PermissionException(CommonErrorCode.PERMISSION_DENIED, "权限不足");
```

### DataException 类

**包名**: `com.atlas.common.feature.core.exception.DataException`

**描述**: 数据异常类

**构造函数**: 同 BusinessException

**使用示例**:

```java
throw new DataException(CommonErrorCode.DATA_CONFLICT, "用户名已存在");
```

### CommonErrorCode 类

**包名**: `com.atlas.common.feature.core.constant.CommonErrorCode`

**描述**: 通用错误码常量类

**常量定义**: 见 data-model.md

**使用示例**:

```java
String errorCode = CommonErrorCode.USER_NOT_FOUND;
```

### HttpStatus 类

**包名**: `com.atlas.common.feature.core.constant.HttpStatus`

**描述**: HTTP 状态码常量类

**常量定义**: 见 data-model.md

**使用示例**:

```java
int statusCode = HttpStatus.OK;
```

### CommonConstants 类

**包名**: `com.atlas.common.feature.core.constant.CommonConstants`

**描述**: 通用常量类

**常量定义**: 见 data-model.md

**使用示例**:

```java
int defaultPage = CommonConstants.DEFAULT_PAGE;
```

## 序列化规范

### Result 序列化

```json
{
  "code": "000000",
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1704067200000
}
```

### PageResult 序列化

```json
{
  "list": [ ... ],
  "total": 100,
  "page": 1,
  "size": 10,
  "pages": 10
}
```

### BusinessException 序列化

```json
{
  "errorCode": "052001",
  "message": "用户不存在",
  "cause": null
}
```

## 版本兼容性

- 所有 API 保持向后兼容
- 新增字段使用 `@JsonIgnoreProperties(ignoreUnknown = true)` 支持向后兼容
- 废弃方法使用 `@Deprecated` 注解标记

## 使用限制

- Result 类不能直接实例化，必须使用静态工厂方法
- PageResult 类不能直接实例化，必须使用静态工厂方法
- 常量类不能实例化，使用私有构造函数防止实例化

