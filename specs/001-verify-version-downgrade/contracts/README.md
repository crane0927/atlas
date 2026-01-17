# 合约说明

## 概述

本功能主要涉及版本降级验证，不涉及新的 API 接口合约。验证过程使用现有的服务接口进行功能测试。

## 验证使用的现有接口

### Gateway 服务接口

- **路由转发**: 验证 Gateway 能够正常转发请求到后端服务
- **健康检查**: `GET /actuator/health` - 验证服务健康状态

### Auth 服务接口

- **用户登录**: `POST /api/v1/auth/login` - 验证登录功能是否正常
- **健康检查**: `GET /actuator/health` - 验证服务健康状态

### System 服务接口

- **用户查询**: `GET /api/v1/users/{userId}` - 验证用户查询功能是否正常
- **权限查询**: `GET /api/v1/users/{userId}/authorities` - 验证权限查询功能是否正常
- **健康检查**: `GET /actuator/health` - 验证服务健康状态

## 验证合约

### 构建验证合约

**验证方法**: Maven 构建命令

**合约**:
- 输入: 无（执行 `mvn clean install` 命令）
- 输出: 构建结果（成功/失败、构建日志、构建产物）

**成功标准**:
- 构建命令退出码为 0
- 所有模块编译成功
- 构建产物正常生成

### 编译验证合约

**验证方法**: Maven 编译命令

**合约**:
- 输入: 模块路径
- 输出: 编译结果（成功/失败、编译日志）

**成功标准**:
- 编译命令退出码为 0
- 无编译错误
- 无依赖解析失败

### 启动验证合约

**验证方法**: Spring Boot 应用启动

**合约**:
- 输入: 服务配置
- 输出: 启动结果（成功/失败、启动日志、服务状态）

**成功标准**:
- 服务成功启动
- 服务监听配置的端口
- 服务注册到 Nacos（如果配置）
- 健康检查通过

### 功能验证合约

**验证方法**: HTTP 请求测试

**合约**:
- 输入: HTTP 请求（方法、路径、请求体）
- 输出: HTTP 响应（状态码、响应体）

**成功标准**:
- HTTP 状态码为 200 或预期状态码
- 响应体格式正确
- 功能逻辑正确

### 兼容性验证合约

**验证方法**: 依赖分析和运行时检查

**合约**:
- 输入: 依赖配置
- 输出: 兼容性结果（兼容/不兼容、冲突列表、错误列表）

**成功标准**:
- 所有依赖版本兼容
- 无版本冲突
- 无运行时兼容性错误

## 验证结果格式

### 构建验证结果

```json
{
  "success": true,
  "exitCode": 0,
  "buildTime": 12345,
  "modules": [
    {
      "moduleName": "atlas-gateway",
      "success": true,
      "compileTime": 1234
    }
  ],
  "errors": [],
  "warnings": []
}
```

### 服务启动验证结果

```json
{
  "serviceName": "atlas-gateway",
  "success": true,
  "startupTime": 5678,
  "port": 8080,
  "nacosRegistered": true,
  "healthCheckPassed": true,
  "errors": []
}
```

### 功能测试结果

```json
{
  "functionName": "Gateway路由转发",
  "success": true,
  "responseTime": 123,
  "statusCode": 200,
  "errors": []
}
```

### 依赖兼容性结果

```json
{
  "dependencyName": "spring-boot-starter-web",
  "version": "3.5.9",
  "compatible": true,
  "conflicts": [],
  "runtimeErrors": []
}
```

## 验证流程

1. **构建验证** → 验证项目能够成功构建
2. **编译验证** → 验证各模块能够成功编译
3. **启动验证** → 验证服务能够成功启动
4. **功能验证** → 验证核心功能正常工作
5. **兼容性验证** → 验证依赖兼容性

## 验证工具

- **Maven**: 用于构建和编译验证
- **Spring Boot**: 用于服务启动验证
- **HTTP 客户端**: 用于功能测试（curl、Postman 等）
- **Maven 依赖分析工具**: 用于依赖兼容性验证
