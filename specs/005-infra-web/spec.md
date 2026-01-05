# 功能规格说明

## 宪法检查

本规格说明必须符合项目宪法要求：

- ✅ **模块化**: 功能归属 `atlas-common-infra-web` 模块
- ✅ **代码复用**: 提供可复用的 Web 基础设施组件
- ✅ **中文注释**: 所有代码使用中文注释
- ✅ **RESTful API**: 所有接口遵循 RESTful 设计规范
- ✅ **统一响应格式**: 使用 `atlas-common-feature-core` 模块的 `Result` 类

## 功能描述

实现 `atlas-common-infra-web` 模块，提供 Web 基础设施功能，包括全局异常处理、参数校验返回、Jackson 配置、TraceId Filter 等。该模块为所有业务模块提供统一的异常处理、参数校验、JSON 序列化和 TraceId 管理能力，确保 Web 层的规范性、一致性和可维护性。

## 功能需求

### FR1: 全局异常处理

**需求描述**: 提供全局异常处理器，统一处理所有异常并返回标准的 `Result` 格式响应。

**功能要求**:
- 提供 `@RestControllerAdvice` 注解的全局异常处理器
- 处理业务异常（`BusinessException`、`ParameterException`、`PermissionException`、`DataException`）
- 处理参数校验异常（`MethodArgumentNotValidException`、`ConstraintViolationException`）
- 处理 Spring MVC 异常（`HttpRequestMethodNotSupportedException`、`HttpMediaTypeNotSupportedException` 等）
- 处理系统异常（`Exception`、`RuntimeException`）
- 所有异常响应使用统一的 `Result` 格式
- 异常响应包含错误码、错误消息、TraceId
- 异常处理器包含完整的中文注释
- 支持异常日志记录（记录异常堆栈信息）

**验收标准**:
- 所有业务异常可以统一处理并返回标准格式
- 参数校验异常可以统一处理并返回标准格式
- Spring MVC 异常可以统一处理并返回标准格式
- 系统异常可以统一处理并返回标准格式
- 异常响应格式符合 `Result` 规范
- 异常响应包含 TraceId（从 MDC 获取）
- 异常处理器包含完整的中文注释
- 异常日志可以正确记录

### FR2: 参数校验返回

**需求描述**: 提供参数校验异常的统一处理，将 Spring Validation 的校验错误转换为标准的 `Result` 格式响应。

**功能要求**:
- 处理 `@Valid` 和 `@Validated` 注解触发的参数校验异常
- 处理 `MethodArgumentNotValidException`（方法参数校验失败）
- 处理 `ConstraintViolationException`（约束校验失败）
- 提取校验错误信息（字段名、错误消息）
- 将校验错误信息格式化为统一的响应格式
- 校验错误响应包含错误码、错误消息、字段错误列表
- 校验错误响应包含 TraceId
- 参数校验处理包含完整的中文注释

**验收标准**:
- `@Valid` 注解的校验错误可以统一处理
- `@Validated` 注解的校验错误可以统一处理
- 校验错误信息可以正确提取和格式化
- 校验错误响应格式符合 `Result` 规范
- 校验错误响应包含字段错误列表
- 校验错误响应包含 TraceId
- 参数校验处理包含完整的中文注释

### FR3: Jackson 配置

**需求描述**: 提供统一的 Jackson JSON 序列化配置，确保所有模块使用一致的 JSON 格式。

**功能要求**:
- 提供 Jackson `ObjectMapper` 的配置类
- 配置日期时间格式（统一使用 ISO-8601 格式或指定格式）
- 配置空值处理（null 值处理策略）
- 配置序列化特性（忽略空值、格式化输出等）
- 配置反序列化特性（忽略未知属性、大小写不敏感等）
- 配置时区处理
- 配置自定义序列化器（如 Long 类型转 String，避免精度丢失）
- Jackson 配置包含完整的中文注释

**验收标准**:
- 日期时间可以统一格式化
- 空值可以统一处理
- JSON 序列化特性符合项目规范
- JSON 反序列化特性符合项目规范
- 时区可以正确处理
- 自定义序列化器可以正常工作
- Jackson 配置包含完整的中文注释

### FR4: TraceId Filter

**需求描述**: 提供 HTTP Filter 来处理 TraceId，在请求的最早阶段设置 TraceId，确保所有请求都有 TraceId。

**功能要求**:
- 提供 `Filter` 接口的实现类
- 从 HTTP 请求头 `X-Trace-Id` 获取 TraceId
- 如果请求头中没有 TraceId，则自动生成
- 将 TraceId 设置到 `TraceIdUtil`（复用 `atlas-common-infra-logging` 模块的工具类）
- 在响应头中添加 TraceId（可选）
- Filter 执行顺序要早于其他 Filter
- TraceId Filter 包含完整的中文注释
- 支持配置 Filter 的 URL 匹配模式（排除静态资源等）

**验收标准**:
- HTTP 请求可以自动获取或生成 TraceId
- TraceId 可以正确设置到 `TraceIdUtil`
- TraceId 可以正确输出到日志中（通过 MDC）
- TraceId 可以在响应头中返回（可选）
- Filter 执行顺序正确
- TraceId Filter 包含完整的中文注释
- Filter 可以配置 URL 匹配模式

## 用户场景

### 场景 1: 开发人员使用全局异常处理

**角色**: 后端开发人员

**前置条件**: 开发人员正在开发业务接口

**操作流程**:
1. 开发人员在 Service 层抛出业务异常（如 `BusinessException`）
2. Controller 层不需要捕获异常
3. 全局异常处理器自动捕获异常
4. 全局异常处理器将异常转换为标准的 `Result` 格式响应
5. 客户端收到统一的错误响应格式

**预期结果**: 所有异常都可以统一处理，返回标准的 `Result` 格式，包含错误码、错误消息和 TraceId

### 场景 2: 系统自动处理参数校验错误

**角色**: 前端开发人员

**前置条件**: 前端调用后端接口，传递了不符合校验规则的数据

**操作流程**:
1. 前端发送请求，参数不符合 `@Valid` 或 `@Validated` 注解的校验规则
2. Spring Validation 触发参数校验异常
3. 全局异常处理器捕获参数校验异常
4. 全局异常处理器提取校验错误信息（字段名、错误消息）
5. 全局异常处理器将校验错误转换为标准的 `Result` 格式响应
6. 客户端收到包含字段错误列表的响应

**预期结果**: 参数校验错误可以统一处理，返回标准的 `Result` 格式，包含字段错误列表和 TraceId

### 场景 3: 系统使用统一的 Jackson 配置

**角色**: 后端开发人员

**前置条件**: 开发人员正在开发返回 JSON 数据的接口

**操作流程**:
1. 开发人员在 Controller 中返回对象
2. Spring MVC 使用 Jackson 将对象序列化为 JSON
3. Jackson 配置类自动应用统一的序列化规则
4. 客户端收到格式统一的 JSON 响应

**预期结果**: 所有 JSON 响应都使用统一的格式（日期时间格式、空值处理、自定义序列化等）

### 场景 4: 系统自动处理 TraceId

**角色**: 系统运维人员

**前置条件**: 用户发起 HTTP 请求

**操作流程**:
1. HTTP 请求到达服务器
2. TraceId Filter 拦截请求
3. TraceId Filter 从请求头获取 TraceId，如果不存在则自动生成
4. TraceId Filter 将 TraceId 设置到 `TraceIdUtil`
5. TraceId 自动注入到 MDC，供日志使用
6. 业务代码和日志都可以访问 TraceId
7. 请求结束后，TraceId 自动清理

**预期结果**: 所有请求都有 TraceId，TraceId 可以正确传递到日志和下游服务，便于问题排查和链路追踪

## 数据模型

### 异常响应格式

使用 `atlas-common-feature-core` 模块的 `Result` 类：

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

## 业务逻辑

### 全局异常处理流程

1. **异常捕获**: `@RestControllerAdvice` 注解的全局异常处理器捕获所有异常
2. **异常分类**: 根据异常类型选择对应的处理方法
3. **错误码映射**: 将异常类型映射到对应的错误码
4. **响应构建**: 使用 `Result.error()` 方法构建错误响应
5. **TraceId 注入**: 从 MDC 获取 TraceId 并注入到响应中
6. **日志记录**: 记录异常信息（包括堆栈信息）

### 参数校验处理流程

1. **校验触发**: Spring Validation 在方法参数校验时触发异常
2. **异常捕获**: 全局异常处理器捕获 `MethodArgumentNotValidException` 或 `ConstraintViolationException`
3. **错误提取**: 从异常中提取字段错误信息（字段名、错误消息）
4. **错误格式化**: 将错误信息格式化为统一的响应格式
5. **响应构建**: 使用 `Result.error()` 方法构建校验错误响应
6. **TraceId 注入**: 从 MDC 获取 TraceId 并注入到响应中

### TraceId Filter 处理流程

1. **请求拦截**: Filter 在请求的最早阶段拦截请求
2. **TraceId 获取**: 从 HTTP 请求头 `X-Trace-Id` 获取 TraceId
3. **TraceId 生成**: 如果请求头中没有 TraceId，则调用 `TraceIdUtil.generate()` 生成
4. **TraceId 设置**: 调用 `TraceIdUtil.setTraceId()` 设置 TraceId（同时设置 ThreadLocal 和 MDC）
5. **响应头设置**: 可选地将 TraceId 添加到响应头
6. **请求继续**: 继续处理请求

## 异常处理

### 异常类型

1. **业务异常**: `BusinessException`、`ParameterException`、`PermissionException`、`DataException`
2. **参数校验异常**: `MethodArgumentNotValidException`、`ConstraintViolationException`
3. **Spring MVC 异常**: `HttpRequestMethodNotSupportedException`、`HttpMediaTypeNotSupportedException`、`MissingServletRequestParameterException` 等
4. **系统异常**: `Exception`、`RuntimeException`、`NullPointerException` 等

### 异常处理策略

- **业务异常**: 返回业务错误码和错误消息
- **参数校验异常**: 返回参数校验错误码和字段错误列表
- **Spring MVC 异常**: 返回 HTTP 方法错误码和错误消息
- **系统异常**: 返回系统错误码和通用错误消息（生产环境不暴露堆栈信息）

## 依赖关系

### 模块依赖

- **atlas-common-feature-core**: 依赖 `Result` 类和异常类
- **atlas-common-infra-logging**: 依赖 `TraceIdUtil` 工具类（可选，如果使用 TraceId Filter）

### Spring 依赖

- **spring-boot-starter-web**: Web 相关功能
- **spring-boot-starter-validation**: 参数校验功能
- **jackson-databind**: Jackson JSON 序列化

## 测试要求

- 单元测试覆盖率 ≥ 80%
- 测试全局异常处理器的所有异常处理方法
- 测试参数校验异常处理
- 测试 Jackson 配置的正确性
- 测试 TraceId Filter 的功能
- 集成测试覆盖主要异常场景

## 成功标准

1. **统一性**: 所有异常响应使用统一的 `Result` 格式，响应格式一致性达到 100%
2. **完整性**: 所有异常类型都可以正确处理，异常处理覆盖率 ≥ 95%
3. **易用性**: 开发人员可以在 5 分钟内理解并使用全局异常处理，无需在每个 Controller 中编写异常处理代码
4. **可维护性**: 异常处理逻辑集中管理，新增异常类型处理时间不超过 10 分钟
5. **可追溯性**: 所有异常响应包含 TraceId，便于问题排查和链路追踪

## 实现注意事项

- [ ] 检查是否有可复用的公共方法（如 `TraceIdUtil`）
- [ ] 确保所有类和方法添加中文注释
- [ ] 遵循 RESTful 设计规范
- [ ] 使用统一的异常处理机制
- [ ] 确保 Jackson 配置符合项目规范
- [ ] 确保 TraceId Filter 与 `atlas-common-infra-logging` 模块的 TraceId 机制兼容
- [ ] 考虑 Filter 的执行顺序，确保 TraceId Filter 在其他 Filter 之前执行
