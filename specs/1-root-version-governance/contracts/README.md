# 合约说明

## 概述

本功能主要涉及基础设施配置和工程规范，不涉及 API 接口合约。

## 配置合约

### Maven POM 合约

**父 POM 配置合约**: 所有子模块必须通过 `<parent>` 标签继承父 POM，并遵循以下约定：

- Java 版本: 21
- Spring Boot 版本: 4.0.1
- Spring Cloud 版本: 2025.1.0
- Spring Cloud Alibaba 版本: 2025.1.0
- 字符编码: UTF-8

### 依赖管理合约

**依赖版本管理合约**: 所有依赖版本由父 POM 统一管理，子模块禁止显式指定版本。

**BOM 管理**:
- `spring-cloud-dependencies:2025.1.0`
- `spring-cloud-alibaba-dependencies:2025.1.0`

### 插件配置合约

**插件管理合约**: 所有插件配置由父 POM 统一管理，子模块按需启用。

**核心插件**:
- Maven Enforcer Plugin: 强制规范检查
- Maven Surefire Plugin: 单元测试执行
- Maven Failsafe Plugin: 集成测试执行
- Spotless Maven Plugin: 代码格式化
- Maven Compiler Plugin: Java 编译配置

## 规范合约

### 包名规范合约

**包名结构合约**: 所有模块必须遵循定义的包名结构。

- 根包名: `com.atlas`
- 业务模块: `com.atlas.{module-name}.{layer}`
- 公共模块: `com.atlas.common.{infra|feature}.{module-name}`

### 错误码规范合约

**错误码格式合约**: 所有错误码必须符合 6 位数字格式（MMTTSS）。

- 模块码（MM）: 01-99
- 类型码（TT）: 00-99
- 序号（SS）: 00-99

### 配置命名合约

**Nacos 配置命名合约**: 所有配置必须遵循命名规范。

- DataId: `{application-name}-{profile}.{extension}`
- Group: 按环境分组（DEV_GROUP、TEST_GROUP、PROD_GROUP）
- Key: `{module}.{category}.{key}`

### 日志格式合约

**日志格式合约**: 所有日志输出必须符合统一格式。

- 格式模板: `%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n`
- TraceId 输出: 必须包含在日志中
- 日志级别: TRACE, DEBUG, INFO, WARN, ERROR

## 验证机制

### 构建时验证

- Maven Enforcer Plugin: 检查 Java 版本和依赖版本一致性
- Spotless Maven Plugin: 检查代码格式
- Maven Compiler Plugin: 检查 Java 版本兼容性

### 代码审查验证

- 包名规范遵循情况
- 错误码格式和段位使用情况
- 配置命名规范遵循情况
- 日志格式使用情况

## 变更管理

### 版本升级流程

1. 在父 POM 中更新版本号
2. 运行 `mvn clean install` 验证构建
3. 更新相关文档
4. 通知团队版本变更

### 规范变更流程

1. 提出规范变更提案
2. 团队评审和批准
3. 更新规范文档
4. 通知团队规范变更
5. 提供迁移指南（如需要）

