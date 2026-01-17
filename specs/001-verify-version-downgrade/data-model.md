# 数据模型

## 概述

本文档定义了版本降级验证过程中涉及的数据实体和验证结果。

## 验证结果实体

### BuildResult（构建结果）

**描述**: 记录项目构建验证的结果

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 |
|--------|------|------|------|
| success | Boolean | 构建是否成功 | 是 |
| exitCode | Integer | 构建退出码 | 是 |
| buildTime | Long | 构建耗时（毫秒） | 是 |
| modules | List<ModuleResult> | 各模块构建结果 | 是 |
| errors | List<String> | 构建错误列表 | 否 |
| warnings | List<String> | 构建警告列表 | 否 |

### ModuleResult（模块结果）

**描述**: 记录单个模块的编译结果

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 |
|--------|------|------|------|
| moduleName | String | 模块名称 | 是 |
| success | Boolean | 编译是否成功 | 是 |
| compileTime | Long | 编译耗时（毫秒） | 是 |
| errors | List<String> | 编译错误列表 | 否 |
| warnings | List<String> | 编译警告列表 | 否 |

### ServiceStartupResult（服务启动结果）

**描述**: 记录服务启动验证的结果

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 |
|--------|------|------|------|
| serviceName | String | 服务名称 | 是 |
| success | Boolean | 启动是否成功 | 是 |
| startupTime | Long | 启动耗时（毫秒） | 是 |
| port | Integer | 服务监听端口 | 是 |
| nacosRegistered | Boolean | 是否注册到 Nacos | 否 |
| healthCheckPassed | Boolean | 健康检查是否通过 | 否 |
| errors | List<String> | 启动错误列表 | 否 |

### FunctionTestResult（功能测试结果）

**描述**: 记录功能测试的结果

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 |
|--------|------|------|------|
| functionName | String | 功能名称 | 是 |
| success | Boolean | 测试是否通过 | 是 |
| responseTime | Long | 响应时间（毫秒） | 否 |
| statusCode | Integer | HTTP 状态码 | 否 |
| errors | List<String> | 测试错误列表 | 否 |

### DependencyCompatibilityResult（依赖兼容性结果）

**描述**: 记录依赖兼容性验证的结果

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 |
|--------|------|------|------|
| dependencyName | String | 依赖名称 | 是 |
| version | String | 依赖版本 | 是 |
| compatible | Boolean | 是否兼容 | 是 |
| conflicts | List<String> | 版本冲突列表 | 否 |
| runtimeErrors | List<String> | 运行时错误列表 | 否 |

## 验证报告实体

### VerificationReport（验证报告）

**描述**: 完整的版本降级验证报告

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 |
|--------|------|------|------|
| buildResult | BuildResult | 构建验证结果 | 是 |
| moduleResults | List<ModuleResult> | 模块编译结果列表 | 是 |
| serviceStartupResults | List<ServiceStartupResult> | 服务启动结果列表 | 是 |
| functionTestResults | List<FunctionTestResult> | 功能测试结果列表 | 是 |
| dependencyCompatibilityResults | List<DependencyCompatibilityResult> | 依赖兼容性结果列表 | 是 |
| overallSuccess | Boolean | 整体验证是否成功 | 是 |
| summary | String | 验证总结 | 是 |
| issues | List<String> | 发现的问题列表 | 否 |
| recommendations | List<String> | 建议列表 | 否 |

## 数据关系

```
VerificationReport
├── BuildResult
│   └── List<ModuleResult>
├── List<ServiceStartupResult>
├── List<FunctionTestResult>
└── List<DependencyCompatibilityResult>
```

## 验证状态枚举

### VerificationStatus（验证状态）

**枚举值**:
- `PENDING` - 待验证
- `IN_PROGRESS` - 验证中
- `SUCCESS` - 验证成功
- `FAILED` - 验证失败
- `PARTIAL` - 部分成功

## 数据验证规则

### BuildResult 验证规则

- `success` 必须为 true 时，`exitCode` 必须为 0
- `success` 为 false 时，`errors` 列表不能为空
- `buildTime` 必须大于 0

### ModuleResult 验证规则

- `moduleName` 不能为空
- `success` 为 false 时，`errors` 列表不能为空

### ServiceStartupResult 验证规则

- `serviceName` 不能为空
- `port` 必须在有效端口范围内（1-65535）
- `success` 为 false 时，`errors` 列表不能为空

### FunctionTestResult 验证规则

- `functionName` 不能为空
- `success` 为 false 时，`errors` 列表不能为空

### DependencyCompatibilityResult 验证规则

- `dependencyName` 不能为空
- `version` 不能为空
- `compatible` 为 false 时，`conflicts` 或 `runtimeErrors` 列表不能为空
