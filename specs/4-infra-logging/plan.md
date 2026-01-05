# 功能规划文档

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 提取公共方法，避免重复代码
- ✅ **模块化**: 遵循分层架构原则
- ✅ **包名规范**: 遵循 `com.atlas.common.infra.logging` 包名结构
- ✅ **配置文件格式**: 使用 YAML 格式（日志配置使用 XML，符合 Logback 规范）

**宪法合规性评估**:
- ✅ 所有技术栈版本符合宪法要求
- ✅ 模块归属 `atlas-common-infra-logging`，符合模块化设计原则
- ✅ 提供可复用的日志配置和工具类，符合代码复用原则
- ✅ 所有代码将使用中文注释
- ✅ 遵循包名规范
- ✅ 日志配置符合项目日志格式规范

## 功能概述

实现 `atlas-common-infra-logging` 模块，提供项目日志基础设施功能，为所有业务模块提供统一的日志格式、链路追踪支持和数据脱敏能力。该模块确保：

1. **统一性**: 所有模块使用统一的日志格式和配置
2. **可追溯性**: TraceId 自动注入和传递，支持分布式链路追踪
3. **安全性**: 敏感信息自动脱敏，保护用户隐私
4. **易用性**: 提供便捷的工具类和配置模板，降低使用成本

## 技术方案

### 架构设计

**模块结构**:
```
atlas/
└── atlas-common/              # 公共模块
    └── atlas-common-infra/    # 基础设施模块
        └── atlas-common-infra-logging/  # 日志基础设施模块
            ├── pom.xml                    # 模块 POM
            ├── README.md                  # 模块文档
            ├── src/
            │   ├── main/java/
            │   │   └── com/atlas/common/infra/logging/
            │   │       ├── config/        # 日志配置
            │   │       │   └── logback-default.xml
            │   │       ├── trace/         # TraceId 管理
            │   │       │   ├── TraceIdUtil.java
            │   │       │   ├── TraceIdGenerator.java
            │   │       │   ├── TraceIdInterceptor.java
            │   │       │   └── TraceIdFeignInterceptor.java
            │   │       ├── desensitize/  # 脱敏工具
            │   │       │   ├── DesensitizeUtil.java
            │   │       │   ├── DesensitizeRule.java
            │   │       │   ├── DesensitizeInterceptor.java
            │   │       │   └── annotation/
            │   │       │       └── Sensitive.java
            │   │       └── async/        # 异步任务支持
            │   │           └── TraceIdTaskDecorator.java
            │   └── test/java/             # 测试代码
            │       └── com/atlas/common/infra/logging/
            │           ├── trace/
            │           ├── desensitize/
            │           └── async/
```

**核心组件**:

1. **日志配置模板**: logback-default.xml
   - 统一的日志格式配置
   - 支持控制台和文件输出
   - 支持日志轮转和错误日志单独输出
   - 支持环境特定配置

2. **TraceId 管理**: TraceIdUtil、TraceIdGenerator
   - TraceId 生成工具（UUID、雪花算法）
   - ThreadLocal 存储和管理
   - MDC 自动注入
   - 清理机制

3. **TraceId 拦截器**: TraceIdInterceptor、TraceIdFeignInterceptor
   - HTTP 请求拦截器，自动获取或生成 TraceId
   - Feign 拦截器，自动传递 TraceId
   - 异步任务装饰器，传递 TraceId

4. **脱敏工具**: DesensitizeUtil、DesensitizeInterceptor
   - 常见敏感字段脱敏方法
   - 自定义脱敏规则支持
   - 日志消息自动脱敏
   - 对象字段脱敏注解

### 技术选型

**构建工具**: Maven 3.8+
- **理由**: 项目使用 Maven，继承父 POM 配置

**核心依赖**:
- **Logback**: 日志框架（Spring Boot 内置）
- **SLF4J**: 日志门面（Spring Boot 内置）
- **Spring Boot Web**: 用于 HTTP 拦截器
- **Spring Cloud OpenFeign**: 用于 Feign 拦截器（可选）

**技术决策**:
- 使用 Logback 作为日志框架（Spring Boot 默认）
- 使用 MDC（Mapped Diagnostic Context）存储 TraceId
- 使用 ThreadLocal 管理 TraceId（线程安全）
- 使用正则表达式进行敏感信息匹配和脱敏
- 使用 Spring 拦截器实现自动注入和传递

## 实施计划

### 阶段 0: 研究与设计

**目标**: 完成技术调研和设计决策

**任务**:
1. 研究 Logback 配置最佳实践
2. 研究 TraceId 传递机制（HTTP、Feign、异步任务）
3. 研究敏感信息脱敏规则和性能优化
4. 研究 MDC 和 ThreadLocal 的使用模式
5. 设计 TraceId 生成策略
6. 设计脱敏规则和匹配算法

**输出**: `research.md`

### 阶段 1: 日志配置和 TraceId 实现

**目标**: 实现日志配置模板和 TraceId 管理功能

**任务**:
1. 创建 logback-default.xml 配置模板
   - 定义统一日志格式
   - 配置控制台和文件输出
   - 配置日志轮转策略
   - 配置环境特定日志级别
   - 添加中文注释

2. 创建 TraceId 生成工具
   - 实现 TraceIdGenerator（UUID、雪花算法）
   - 实现 TraceIdUtil（设置、获取、清理）
   - 实现 ThreadLocal 存储
   - 实现 MDC 自动注入
   - 添加中文注释

3. 创建 TraceId 拦截器
   - 实现 TraceIdInterceptor（HTTP 请求拦截）
   - 实现 TraceIdFeignInterceptor（Feign 调用拦截）
   - 实现 TraceIdTaskDecorator（异步任务装饰器）
   - 添加中文注释

**输出**: 日志配置模板和 TraceId 管理功能完成

### 阶段 2: 脱敏工具实现

**目标**: 实现敏感信息脱敏工具

**任务**:
1. 创建脱敏工具类
   - 实现 DesensitizeUtil（常见字段脱敏方法）
   - 实现 DesensitizeRule（脱敏规则定义）
   - 实现 @Sensitive 注解
   - 添加中文注释

2. 创建脱敏拦截器
   - 实现 DesensitizeInterceptor（日志消息脱敏）
   - 实现对象字段自动脱敏
   - 支持自定义脱敏规则
   - 添加中文注释

**输出**: 脱敏工具功能完成

### 阶段 3: 单元测试

**目标**: 编写完整的单元测试

**任务**:
1. 测试 TraceId 生成工具
   - 测试 UUID 生成
   - 测试雪花算法生成
   - 测试 ThreadLocal 存储和清理
   - 测试 MDC 注入

2. 测试 TraceId 拦截器
   - 测试 HTTP 请求拦截器
   - 测试 Feign 拦截器
   - 测试异步任务装饰器

3. 测试脱敏工具
   - 测试各种敏感字段脱敏
   - 测试自定义脱敏规则
   - 测试日志拦截器脱敏

4. 测试日志配置
   - 测试日志格式输出
   - 测试日志文件轮转
   - 测试环境特定配置

**输出**: 单元测试覆盖率 ≥ 80%

### 阶段 4: 文档和示例

**目标**: 创建使用文档和示例代码

**任务**:
1. 创建 quickstart.md 快速开始指南
2. 创建日志配置使用示例
3. 创建 TraceId 使用示例
4. 创建脱敏工具使用示例
5. 更新模块 README.md

**输出**: 完整的文档和示例

## 风险评估

### 风险 1: TraceId 传递失败

- **描述**: TraceId 在异步任务或 Feign 调用中传递失败
- **影响**: 中
- **应对**: 使用 ThreadLocal 和装饰器模式确保传递，提供完善的测试覆盖

### 风险 2: 脱敏性能问题

- **描述**: 日志拦截器脱敏可能影响日志输出性能
- **影响**: 中
- **应对**: 使用高效的正则表达式，考虑异步处理或采样

### 风险 3: 内存泄漏

- **描述**: ThreadLocal 未正确清理导致内存泄漏
- **影响**: 高
- **应对**: 使用拦截器和装饰器确保清理，提供完善的清理机制

### 风险 4: 日志配置兼容性

- **描述**: 日志配置模板可能与某些模块的特定需求不兼容
- **影响**: 低
- **应对**: 提供灵活的配置选项，支持模块自定义扩展

## 验收标准

### 功能验收标准

- ✅ 日志配置模板可以被所有模块使用
- ✅ TraceId 可以自动生成、传递和清理
- ✅ 脱敏工具可以正确处理各种敏感字段
- ✅ 日志格式符合项目规范
- ✅ 所有类和方法包含完整的中文注释

### 质量验收标准

- ✅ 单元测试覆盖率 ≥ 80%
- ✅ 所有代码通过 Spotless 格式化检查
- ✅ 所有代码通过 Maven Enforcer 检查
- ✅ 包名符合 `com.atlas.common.infra.logging` 规范
- ✅ TraceId 传递成功率 ≥ 99%
- ✅ 敏感信息脱敏覆盖率 ≥ 95%

### 文档验收标准

- ✅ 快速开始指南完整
- ✅ 使用示例清晰易懂
- ✅ 模块 README.md 完整
- ✅ 配置模板包含完整的中文注释

## 依赖关系

### 内部依赖

- 依赖父 POM（atlas）的版本管理
- 依赖日志格式规范文档的定义

### 外部依赖

- Logback（日志框架，Spring Boot 内置）
- SLF4J（日志门面，Spring Boot 内置）
- Spring Boot Web（用于 HTTP 拦截器）
- Spring Cloud OpenFeign（用于 Feign 拦截器，可选）

## 后续工作

1. 在其他模块中应用日志配置模板
2. 集成到微服务调用链中，验证 TraceId 传递
3. 根据实际使用情况优化脱敏规则
4. 监控日志性能和脱敏性能

