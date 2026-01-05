# Gateway 验收测试文档

## 概述

本文档定义了 `atlas-gateway` 模块的验收测试场景，用于验证 Gateway 的核心功能是否正常工作。

## 测试环境

- **Gateway 端口**: 8080
- **后端服务端口**: 8080（临时 Mock 服务）
- **Nacos Server**: localhost:8848
- **测试工具**: curl、Postman 或类似工具

## 验收测试场景

### 场景 1: 路由转发功能测试 [US1]

**测试目标**: 验证 Gateway 能够正确转发请求到后端服务

**前置条件**:
- Gateway 已启动
- 后端服务（Mock 服务）已启动
- 路由规则已配置

**测试步骤**:

1. **测试健康检查接口转发**
   ```bash
   curl http://localhost:8080/health
   ```
   **预期结果**: 
   - HTTP 状态码: 200
   - 响应体包含健康检查信息

2. **测试 Mock 接口转发**
   ```bash
   curl http://localhost:8080/mock/test
   ```
   **预期结果**: 
   - HTTP 状态码: 200
   - 响应体包含 Mock 数据

3. **测试路径重写（StripPrefix）**
   ```bash
   curl http://localhost:8080/gateway/health
   ```
   **预期结果**: 
   - 请求被转发到 `/health`（路径前缀 `/gateway` 被移除）
   - HTTP 状态码: 200

**验收标准**:
- ✅ 所有路由转发请求都能成功
- ✅ 路径重写功能正常工作
- ✅ 响应格式正确

---

### 场景 2: TraceId 链路追踪测试 [US3]

**测试目标**: 验证 Gateway 能够正确处理 TraceId

**前置条件**:
- Gateway 已启动
- TraceId 过滤器已配置

**测试步骤**:

1. **测试自动生成 TraceId**
   ```bash
   curl -v http://localhost:8080/health
   ```
   **预期结果**: 
   - 响应头包含 `X-Trace-Id`
   - TraceId 格式正确（32 位字符串）

2. **测试使用已有 TraceId**
   ```bash
   curl -v -H "X-Trace-Id: test-trace-id-12345" http://localhost:8080/health
   ```
   **预期结果**: 
   - 响应头中的 `X-Trace-Id` 为 `test-trace-id-12345`
   - TraceId 传递到后端服务

3. **测试 TraceId 在日志中输出**
   - 查看 Gateway 日志
   - 查看后端服务日志
   **预期结果**: 
   - 日志中包含 TraceId
   - TraceId 在请求链路中保持一致

**验收标准**:
- ✅ TraceId 自动生成功能正常
- ✅ TraceId 传递功能正常
- ✅ TraceId 在日志中正确输出

---

### 场景 3: 统一错误返回测试 [US4]

**测试目标**: 验证 Gateway 的错误响应使用统一格式

**前置条件**:
- Gateway 已启动
- 统一异常处理器已配置

**测试步骤**:

1. **测试路由不存在错误（404）**
   ```bash
   curl http://localhost:8080/not-exist
   ```
   **预期结果**: 
   - HTTP 状态码: 200（Gateway 统一错误响应）
   - 响应体格式:
     ```json
     {
       "code": "010404",
       "message": "路由不存在",
       "data": null,
       "timestamp": 1706342400000,
       "traceId": "abc123def456"
     }
     ```

2. **测试服务不可用错误（503）**
   - 停止后端服务
   - 发送请求
   ```bash
   curl http://localhost:8080/health
   ```
   **预期结果**: 
   - HTTP 状态码: 200
   - 响应体包含错误码 `010503`
   - 响应体包含 TraceId

3. **测试请求超时错误**
   - 配置超时时间
   - 发送请求（模拟超时）
   **预期结果**: 
   - HTTP 状态码: 200
   - 响应体包含错误码 `010002`
   - 响应体包含 TraceId

**验收标准**:
- ✅ 所有错误响应都使用统一的 `Result` 格式
- ✅ 错误码符合项目规范（6位数字，01 开头）
- ✅ 错误响应包含 TraceId

---

### 场景 4: CORS 跨域支持测试 [US2]

**测试目标**: 验证 Gateway 能够正确处理 CORS 跨域请求

**前置条件**:
- Gateway 已启动
- CORS 配置已配置

**测试步骤**:

1. **测试预检请求（OPTIONS）**
   ```bash
   curl -X OPTIONS http://localhost:8080/health \
     -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: GET" \
     -v
   ```
   **预期结果**: 
   - HTTP 状态码: 200
   - 响应头包含:
     - `Access-Control-Allow-Origin: *`
     - `Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS`
     - `Access-Control-Allow-Headers: *`
     - `Access-Control-Max-Age: 3600`

2. **测试实际请求（GET）**
   ```bash
   curl http://localhost:8080/health \
     -H "Origin: http://localhost:3000" \
     -v
   ```
   **预期结果**: 
   - HTTP 状态码: 200
   - 响应头包含 CORS 相关头信息

3. **测试自定义源（非通配符）**
   - 配置 CORS 允许特定源
   - 发送请求
   **预期结果**: 
   - 允许的源可以正常访问
   - 不允许的源被拒绝

**验收标准**:
- ✅ 预检请求（OPTIONS）正确处理
- ✅ CORS 响应头正确设置
- ✅ 跨域请求能够正常访问

---

### 场景 5: 鉴权控制测试 [US5]

**测试目标**: 验证 Gateway 的鉴权控制功能

**前置条件**:
- Gateway 已启动
- 白名单配置已配置
- Token 校验器已配置（占位实现）

**测试步骤**:

1. **测试白名单路径（无需 Token）**
   ```bash
   curl http://localhost:8080/health
   ```
   **预期结果**: 
   - HTTP 状态码: 200
   - 请求能够正常通过

2. **测试非白名单路径（需要 Token 校验）**
   ```bash
   curl http://localhost:8080/api/user/info
   ```
   **预期结果**: 
   - HTTP 状态码: 200（占位实现默认放行）
   - 请求能够正常通过

3. **测试 Token 校验失败（模拟）**
   - 实现自定义 Token 校验器（返回 false）
   - 发送请求
   **预期结果**: 
   - HTTP 状态码: 200
   - 响应体包含错误码 `013001`
   - 响应体包含错误消息 "Token 校验失败"

**验收标准**:
- ✅ 白名单路径能够正常通过
- ✅ 非白名单路径会触发 Token 校验
- ✅ Token 校验失败返回统一错误格式

---

### 场景 6: Nacos Config 配置动态更新测试 [US6]

**测试目标**: 验证 Gateway 配置能够通过 Nacos Config 动态更新

**前置条件**:
- Gateway 已启动
- Nacos Server 已启动
- Nacos Config 配置已创建

**测试步骤**:

1. **测试路由规则动态更新**
   - 在 Nacos 控制台修改路由规则
   - 等待配置生效（< 5 秒）
   - 测试新路由规则
   ```bash
   curl http://localhost:8080/new-route
   ```
   **预期结果**: 
   - 新路由规则生效
   - 请求能够正常转发

2. **测试白名单配置动态更新**
   - 在 Nacos 控制台修改白名单配置
   - 等待配置生效（< 5 秒）
   - 测试白名单路径
   **预期结果**: 
   - 白名单配置生效
   - 白名单路径能够正常通过

3. **测试 CORS 配置动态更新**
   - 在 Nacos 控制台修改 CORS 配置
   - 等待配置生效（< 5 秒）
   - 测试 CORS 请求
   **预期结果**: 
   - CORS 配置生效
   - CORS 响应头正确设置

**验收标准**:
- ✅ 路由规则能够动态更新
- ✅ 白名单配置能够动态更新
- ✅ CORS 配置能够动态更新
- ✅ 配置更新无需重启服务

---

## 测试检查清单

### 功能检查清单

- [ ] 路由转发功能正常
- [ ] TraceId 链路追踪功能正常
- [ ] 统一错误返回功能正常
- [ ] CORS 跨域支持功能正常
- [ ] 鉴权控制功能正常
- [ ] Nacos Config 配置动态更新功能正常

### 性能检查清单

- [ ] 路由转发响应时间 < 100ms
- [ ] TraceId 处理开销 < 10ms
- [ ] 错误处理响应时间 < 50ms
- [ ] CORS 预检请求响应时间 < 50ms

### 稳定性检查清单

- [ ] Gateway 能够正常启动
- [ ] Gateway 能够正常关闭
- [ ] 配置更新不会导致服务中断
- [ ] 错误处理不会导致服务崩溃

## 测试报告模板

### 测试环境信息

- **Gateway 版本**: 1.0.0
- **测试日期**: YYYY-MM-DD
- **测试人员**: XXX
- **测试环境**: dev/test/prod

### 测试结果汇总

| 测试场景 | 测试结果 | 备注 |
|---------|---------|------|
| 场景 1: 路由转发功能测试 | ✅/❌ | |
| 场景 2: TraceId 链路追踪测试 | ✅/❌ | |
| 场景 3: 统一错误返回测试 | ✅/❌ | |
| 场景 4: CORS 跨域支持测试 | ✅/❌ | |
| 场景 5: 鉴权控制测试 | ✅/❌ | |
| 场景 6: Nacos Config 配置动态更新测试 | ✅/❌ | |

### 问题记录

| 问题编号 | 问题描述 | 严重程度 | 状态 |
|---------|---------|---------|------|
| | | | |

## 参考资源

- [Gateway 快速开始指南](./quickstart.md)
- [Gateway README](../../atlas-gateway/README.md)
- [项目错误码规范](../../../docs/engineering-standards/error-code.md)

