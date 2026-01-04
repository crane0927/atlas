# 数据模型

## 概述

本功能主要涉及配置和规范定义，不涉及业务数据模型。本文档描述配置实体和规范结构。

## 配置实体

### 父 POM 配置实体

**实体名称**: `ParentPomConfig`

**描述**: 父 POM 的配置信息

**属性**:
- `javaVersion` (String): Java 版本，固定为 "21"
- `springBootVersion` (String): Spring Boot 版本，固定为 "4.0.1"
- `springCloudVersion` (String): Spring Cloud 版本，固定为 "2025.1.0"
- `springCloudAlibabaVersion` (String): Spring Cloud Alibaba 版本，固定为 "2025.1.0"
- `encoding` (String): 字符编码，固定为 "UTF-8"
- `mavenCompilerSource` (String): Maven 编译源代码版本，固定为 "21"
- `mavenCompilerTarget` (String): Maven 编译目标版本，固定为 "21"

**验证规则**:
- 所有版本号必须符合项目宪法要求
- 编码必须为 UTF-8
- Java 版本必须为 21

### 依赖版本管理实体

**实体名称**: `DependencyVersion`

**描述**: 依赖版本管理信息

**属性**:
- `groupId` (String): 依赖的 GroupId，必填
- `artifactId` (String): 依赖的 ArtifactId，必填
- `version` (String): 依赖版本号，必填
- `scope` (String): 依赖作用域（可选）

**验证规则**:
- GroupId 和 ArtifactId 不能为空
- 版本号必须符合语义化版本规范
- 禁止子模块覆盖父 POM 中已管理的版本

### 错误码实体

**实体名称**: `ErrorCode`

**描述**: 错误码定义

**属性**:
- `code` (String): 错误码，格式为 6 位数字（MMTTSS）
- `module` (String): 模块名称，必填
- `type` (String): 错误类型（系统错误、参数错误、业务错误等），必填
- `message` (String): 错误消息，必填
- `description` (String): 错误描述，可选

**验证规则**:
- 错误码必须符合 6 位数字格式
- 错误码必须在对应模块的段位范围内
- 错误消息不能为空

**段位分配规则**:
- 模块码（MM）: 01-99
- 类型码（TT）: 00-99
- 序号（SS）: 00-99

### 配置命名实体

**实体名称**: `ConfigNaming`

**描述**: Nacos 配置命名信息

**属性**:
- `dataId` (String): 配置 DataId，格式为 `{application-name}-{profile}.{extension}`
- `group` (String): 配置 Group，按环境分组
- `key` (String): 配置项 Key，格式为 `{module}.{category}.{key}`
- `value` (String): 配置值
- `description` (String): 配置描述，可选

**验证规则**:
- DataId 必须符合命名规范
- Group 必须使用预定义的分组名称
- Key 必须使用点分隔的层级结构

## 规范结构

### 包名规范结构

**根包名**: `com.atlas`

**模块包名结构**:
- 业务模块: `com.atlas.{module-name}.{layer}`
  - 示例: `com.atlas.system.controller`
  - 示例: `com.atlas.system.service`
  - 示例: `com.atlas.system.mapper`

**公共模块包名结构**:
- 基础设施模块: `com.atlas.common.infra.{module-name}`
  - 示例: `com.atlas.common.infra.web`
  - 示例: `com.atlas.common.infra.redis`
- 功能特性模块: `com.atlas.common.feature.{module-name}`
  - 示例: `com.atlas.common.feature.core`
  - 示例: `com.atlas.common.feature.security`

**验证规则**:
- 包名必须符合 Java 包名规范
- 包名必须与模块结构一致
- 禁止使用保留关键字

### 日志格式结构

**日志格式模板**: 
```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n
```

**日志级别**: TRACE, DEBUG, INFO, WARN, ERROR

**TraceId 输出**: 在日志格式中包含 TraceId，格式为 `[TraceId: %X{traceId}]`

**验证规则**:
- 日志格式必须包含时间、线程、级别、类名、消息
- TraceId 必须在日志中输出
- 日志级别配置必须合理（生产环境不低于 INFO）

### 错误码段位分配表

**模块错误码分配**:

| 模块码 | 模块名称 | 错误码范围 | 说明 |
|--------|----------|------------|------|
| 01 | atlas-gateway | 010000-019999 | API 网关模块 |
| 02 | atlas-auth | 020000-029999 | 认证授权模块 |
| 03 | atlas-system | 030000-039999 | 系统管理模块 |
| 04 | atlas-common-infra | 040000-049999 | 基础设施模块 |
| 05 | atlas-common-feature | 050000-059999 | 功能特性模块 |
| 06-99 | 业务模块 | 060000-999999 | 预留业务模块 |

**错误类型码分配**:

| 类型码 | 错误类型 | 说明 |
|--------|----------|------|
| 00-09 | 系统错误 | 系统级错误（如服务不可用、超时等） |
| 10-19 | 参数错误 | 参数校验错误（如必填项缺失、格式错误等） |
| 20-29 | 业务错误 | 业务逻辑错误（如数据不存在、状态不正确等） |
| 30-39 | 权限错误 | 权限相关错误（如无权限访问、Token 失效等） |
| 40-49 | 数据错误 | 数据相关错误（如数据冲突、数据格式错误等） |
| 50-99 | 预留扩展 | 预留扩展类型 |

## 关系说明

### 父 POM 与子模块关系

- **一对多**: 一个父 POM 对应多个子模块
- **继承关系**: 子模块通过 `<parent>` 标签继承父 POM
- **依赖管理**: 子模块的依赖版本由父 POM 统一管理

### 错误码与模块关系

- **多对一**: 多个错误码属于一个模块
- **段位分配**: 每个模块分配固定的错误码段位
- **类型分类**: 错误码按类型进行分类管理

### 配置命名与模块关系

- **多对一**: 多个配置属于一个模块
- **环境隔离**: 配置按环境进行分组管理
- **层级结构**: 配置项 Key 使用层级结构组织

## 状态说明

本功能不涉及状态转换，所有实体为静态配置和规范定义。

