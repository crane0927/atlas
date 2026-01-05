# API 合约文档

## 概述

`atlas-gateway` 模块作为 API 网关，不直接提供外部 API 接口，而是作为请求转发的中介层。Gateway 的主要职责是：

1. **路由转发**: 将客户端请求转发到对应的后端服务
2. **跨域处理**: 处理 CORS 跨域请求
3. **链路追踪**: 处理 TraceId，确保链路追踪
4. **错误处理**: 统一处理 Gateway 异常，返回统一格式的错误响应
5. **鉴权控制**: 实现白名单和 Token 校验（占位实现）

## 内部接口规范

### TraceId 请求头

**请求头名称**: `X-Trace-Id`

**说明**: Gateway 从请求头获取 TraceId，如果不存在则自动生成，并在响应头中返回。

**示例**:
```
请求头: X-Trace-Id: abc123def456
响应头: X-Trace-Id: abc123def456
```

### 错误响应格式

Gateway 的所有错误响应使用统一的 `Result` 格式：

```json
{
  "code": "010404",
  "message": "路由不存在",
  "data": null,
  "timestamp": 1706342400000,
  "traceId": "abc123def456"
}
```

**错误码规范**:
- Gateway 模块使用错误码段 01（atlas-gateway）
- 错误码格式：01TTSS（6位数字）
- 常见错误码：
  - `010404`: 路由不存在
  - `010503`: 服务不可用
  - `010002`: 请求超时
  - `013001`: Token 无效（未来扩展）
  - `010000`: Gateway 其他异常

## 配置接口

Gateway 的配置通过 Nacos Config 管理，不涉及 HTTP API 接口。配置格式遵循项目的配置命名规范（`atlas.gateway.*`）。

## 扩展接口

### Token 校验扩展点

Gateway 提供 Token 校验扩展点，允许后续扩展实现具体的 Token 校验逻辑。

**接口定义**:
```java
public interface TokenValidator {
    /**
     * 校验 Token
     *
     * @param request HTTP 请求
     * @return true 表示校验通过，false 表示校验失败
     */
    boolean validate(ServerHttpRequest request);
}
```

**默认实现**: 占位实现，默认返回 `true`（放行所有请求）

**扩展方式**: 实现 `TokenValidator` 接口，注册为 Spring Bean，Gateway 会自动使用该实现。

## 注意事项

1. **Gateway 不提供业务 API**: Gateway 只负责请求转发，不提供具体的业务接口
2. **错误响应格式统一**: 所有 Gateway 错误响应使用统一的 `Result` 格式
3. **TraceId 自动处理**: Gateway 自动处理 TraceId，无需客户端手动设置（可选）
4. **配置管理**: Gateway 配置通过 Nacos Config 管理，支持动态更新

