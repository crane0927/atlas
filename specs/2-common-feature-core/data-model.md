# 数据模型

## 概述

本文档定义了 `atlas-common-feature-core` 模块涉及的所有数据实体和关系。

## 核心实体

### Result<T>

**描述**: 统一 API 响应包装类，用于封装所有 HTTP 接口的响应数据。

**包名**: `com.atlas.common.feature.core.result`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 | 示例 |
|--------|------|------|------|--------|------|
| code | String | 状态码（成功为 "000000"，失败为错误码） | 是 | - | "000000" |
| message | String | 消息（成功为 "操作成功"，失败为错误消息） | 是 | - | "操作成功" |
| data | T | 响应数据（泛型） | 否 | null | User 对象 |
| timestamp | Long | 时间戳（毫秒） | 是 | System.currentTimeMillis() | 1704067200000 |

**约束规则**:
- code 不能为 null 或空字符串
- message 不能为 null 或空字符串
- timestamp 必须大于 0
- 成功响应：code = "000000", message = "操作成功", data != null
- 失败响应：code != "000000", message = 错误消息, data = null

**状态转换**:
- 创建成功响应：`Result.success(data)` → code="000000", data=data
- 创建失败响应：`Result.error(code, message)` → code=code, data=null

**序列化规则**:
- 使用 `@JsonInclude(JsonInclude.Include.NON_NULL)` 忽略 null 值
- 字段名使用小写字母（code、message、data、timestamp）

### PageResult<T>

**描述**: 统一分页响应对象，用于封装分页查询结果。

**包名**: `com.atlas.common.feature.core.page`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 | 示例 |
|--------|------|------|------|--------|------|
| list | List<T> | 数据列表（泛型） | 是 | - | List<User> |
| total | Long | 总记录数 | 是 | - | 100 |
| page | Integer | 当前页码（从 1 开始） | 是 | - | 1 |
| size | Integer | 每页大小 | 是 | - | 10 |
| pages | Integer | 总页数（自动计算） | 是 | - | 10 |

**约束规则**:
- list 不能为 null（可以为空列表）
- total 必须 >= 0
- page 必须 >= 1
- size 必须 > 0 且 <= 1000
- pages 自动计算：`pages = (total + size - 1) / size`

**计算逻辑**:
- `pages = (total + size - 1) / size`: 向上取整计算总页数
- `hasPrevious = page > 1`: 是否有上一页
- `hasNext = page < pages`: 是否有下一页
- `isFirst = page == 1`: 是否是第一页
- `isLast = page == pages`: 是否是最后一页

**边界情况**:
- total = 0: pages = 0, list = []
- list.size() = 0: 正常情况，表示当前页无数据
- page > pages: 无效情况，应返回空列表

### BusinessException

**描述**: 基础业务异常类，所有业务异常的基类。

**包名**: `com.atlas.common.feature.core.exception`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 | 示例 |
|--------|------|------|------|--------|------|
| errorCode | String | 错误码（6位数字） | 是 | - | "052001" |
| message | String | 错误消息 | 是 | - | "用户不存在" |
| cause | Throwable | 异常原因（可选） | 否 | null | 原始异常 |

**约束规则**:
- errorCode 必须符合 6 位数字格式（MMTTSS）
- message 不能为 null 或空字符串
- errorCode 必须符合项目错误码规范

**异常类型**:

1. **ParameterException**: 参数异常
   - 错误码范围: 051000-051999
   - 使用场景: 参数校验失败、必填项缺失、格式错误等

2. **PermissionException**: 权限异常
   - 错误码范围: 053000-053999
   - 使用场景: 权限不足、Token 失效、角色不足等

3. **DataException**: 数据异常
   - 错误码范围: 054000-054999
   - 使用场景: 数据冲突、数据格式错误、数据完整性错误等

4. **BusinessException**: 通用业务异常
   - 错误码范围: 052000-052999
   - 使用场景: 业务逻辑错误、状态不正确、业务规则违反等

### CommonErrorCode

**描述**: 通用错误码常量类，定义项目通用的错误码。

**包名**: `com.atlas.common.feature.core.constant`

**错误码定义**:

| 错误码 | 常量名 | 说明 | 类型 |
|--------|--------|------|------|
| 050000 | SYSTEM_ERROR | 系统内部错误 | 系统错误 |
| 050001 | SERVICE_UNAVAILABLE | 服务暂时不可用 | 系统错误 |
| 050002 | REQUEST_TIMEOUT | 请求处理超时 | 系统错误 |
| 051000 | PARAM_ERROR | 参数错误 | 参数错误 |
| 051001 | PARAM_REQUIRED | 必填项缺失 | 参数错误 |
| 051002 | PARAM_FORMAT_ERROR | 参数格式错误 | 参数错误 |
| 052000 | BUSINESS_ERROR | 业务处理失败 | 业务错误 |
| 052001 | DATA_NOT_FOUND | 数据不存在 | 业务错误 |
| 052002 | STATUS_INVALID | 状态不正确 | 业务错误 |
| 053000 | PERMISSION_DENIED | 权限不足 | 权限错误 |
| 053001 | TOKEN_INVALID | Token 无效 | 权限错误 |
| 054000 | DATA_ERROR | 数据错误 | 数据错误 |
| 054001 | DATA_CONFLICT | 数据冲突 | 数据错误 |

**错误消息映射**:

| 错误码 | 错误消息 |
|--------|----------|
| 050000 | 系统内部错误 |
| 050001 | 服务暂时不可用 |
| 050002 | 请求处理超时 |
| 051000 | 参数错误 |
| 051001 | 必填项缺失 |
| 051002 | 参数格式错误 |
| 052000 | 业务处理失败 |
| 052001 | 数据不存在 |
| 052002 | 状态不正确 |
| 053000 | 权限不足 |
| 053001 | Token 无效 |
| 054000 | 数据错误 |
| 054001 | 数据冲突 |

### HttpStatus

**描述**: HTTP 状态码常量类。

**包名**: `com.atlas.common.feature.core.constant`

**常量定义**:

| 常量名 | 值 | 说明 |
|--------|-----|------|
| OK | 200 | 请求成功 |
| CREATED | 201 | 创建成功 |
| NO_CONTENT | 204 | 无内容 |
| BAD_REQUEST | 400 | 请求错误 |
| UNAUTHORIZED | 401 | 未授权 |
| FORBIDDEN | 403 | 禁止访问 |
| NOT_FOUND | 404 | 资源不存在 |
| INTERNAL_SERVER_ERROR | 500 | 服务器内部错误 |

### CommonConstants

**描述**: 通用常量类。

**包名**: `com.atlas.common.feature.core.constant`

**常量定义**:

| 常量名 | 类型 | 值 | 说明 |
|--------|------|-----|------|
| EMPTY_STRING | String | "" | 空字符串 |
| DEFAULT_PAGE | Integer | 1 | 默认页码 |
| DEFAULT_SIZE | Integer | 10 | 默认每页大小 |
| MAX_PAGE_SIZE | Integer | 1000 | 最大每页大小 |
| SUCCESS_CODE | String | "000000" | 成功状态码 |
| SUCCESS_MESSAGE | String | "操作成功" | 成功消息 |

## 实体关系

### Result 与数据的关系

```
Result<T>
  ├── data: T (泛型，可以是任意类型)
  └── 成功时包含数据，失败时 data = null
```

### PageResult 与数据的关系

```
PageResult<T>
  ├── list: List<T> (泛型列表)
  └── 封装分页查询结果
```

### BusinessException 与错误码的关系

```
BusinessException
  ├── errorCode: String (引用 CommonErrorCode 常量)
  └── 所有异常都包含错误码
```

### Result 与异常的关系

```
BusinessException (抛出)
  └── 全局异常处理器捕获
      └── Result.error(errorCode, message) (返回)
```

## 验证规则

### Result 验证

- code 必须符合格式：成功为 "000000"，失败为 6 位数字
- message 长度不超过 500 字符
- timestamp 必须在合理范围内（当前时间前后 1 小时内）

### PageResult 验证

- total >= 0
- page >= 1
- size > 0 且 <= 1000
- pages 必须等于 `(total + size - 1) / size`

### BusinessException 验证

- errorCode 必须符合 6 位数字格式
- message 长度不超过 500 字符
- errorCode 必须在 CommonErrorCode 中定义

## 数据迁移

本模块不涉及数据迁移，所有实体都是内存对象，不持久化。

## 性能考虑

1. **Result 序列化**: 使用 `@JsonInclude(NON_NULL)` 减少 JSON 大小
2. **PageResult 计算**: 总页数计算使用整数运算，性能开销小
3. **异常创建**: 避免在循环中频繁创建异常对象
4. **常量访问**: 使用 final static 常量，JVM 优化后性能好

