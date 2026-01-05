# atlas-common-infra-logging

## 模块简介

`atlas-common-infra-logging` 是 Atlas 项目的日志基础设施模块，提供 Logback 日志配置规范、TraceId 自动注入和管理、敏感信息脱敏工具等功能。该模块为所有业务模块提供统一的日志格式、链路追踪支持和数据脱敏能力，确保日志输出的规范性、可追溯性和安全性。

## 主要功能

### 1. Logback 日志配置规范

提供标准的 Logback 日志配置模板和规范：
- 统一的日志格式（包含时间戳、线程、级别、Logger、TraceId、消息）
- 支持控制台和文件输出
- 支持日志文件轮转（按时间和大小）
- 支持不同环境的日志级别配置（dev、test、prod）
- 支持错误日志单独输出

### 2. TraceId 自动注入和管理

提供 TraceId 的自动注入、传递和管理功能：
- TraceId 生成工具（支持 UUID 和雪花算法）
- HTTP 请求拦截器，自动从请求头获取或生成 TraceId
- Feign 拦截器，自动传递 TraceId 到下游服务
- 异步任务 TraceId 传递支持
- TraceId 自动注入到 MDC 供日志使用
- TraceId 清理机制，避免内存泄漏

### 3. 敏感信息脱敏工具

提供敏感信息脱敏工具类：
- 支持常见敏感字段脱敏（手机号、身份证号、银行卡号、邮箱、密码等）
- 支持自定义脱敏规则
- 支持对象字段自动脱敏（通过注解或配置）
- 日志脱敏拦截器，自动对日志消息中的敏感信息进行脱敏
- 支持脱敏规则的配置化

## 快速开始

### 添加依赖

在子模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.atlas</groupId>
    <artifactId>atlas-common-infra-logging</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

#### 1. 使用标准日志配置

在模块的 `src/main/resources` 目录下创建 `logback-spring.xml`，复制配置模板：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 引入标准配置模板 -->
    <include resource="com/atlas/common/infra/logging/logback-default.xml"/>
    
    <!-- 模块特定配置 -->
    <logger name="com.atlas.yourmodule" level="DEBUG"/>
</configuration>
```

#### 2. TraceId 自动使用

TraceId 会自动注入，无需手动配置。在代码中正常使用日志即可：

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    public void createUser(User user) {
        // TraceId 会自动包含在日志中
        log.info("创建用户: username={}", user.getUsername());
    }
}
```

#### 3. 使用脱敏工具

```java
import com.atlas.common.infra.logging.desensitize.DesensitizeUtil;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    public void logUserInfo(User user) {
        // 手动脱敏
        String maskedPhone = DesensitizeUtil.maskPhone(user.getPhone());
        log.info("用户信息: phone={}", maskedPhone);
        
        // 自动脱敏（通过拦截器）
        log.info("用户信息: phone={}, idCard={}", user.getPhone(), user.getIdCard());
    }
}
```

#### 4. 手动设置 TraceId

```java
import com.atlas.common.infra.logging.trace.TraceIdUtil;

public void asyncTask() {
    // 手动设置 TraceId
    TraceIdUtil.setTraceId("custom-trace-id");
    try {
        // 业务逻辑
        log.info("异步任务执行");
    } finally {
        // 清理 TraceId
        TraceIdUtil.clear();
    }
}
```

## 重要说明

- **日志配置**: 所有模块应使用统一的日志配置模板，确保日志格式一致
- **TraceId 传递**: TraceId 会自动在 HTTP 请求和 Feign 调用中传递，无需手动处理
- **敏感信息保护**: 建议使用脱敏工具处理敏感信息，避免敏感数据泄露到日志中
- **性能考虑**: 生产环境应使用 INFO 级别，避免 DEBUG/TRACE 级别产生过多日志

## 相关文档

- [快速开始指南](../../../../specs/4-infra-logging/quickstart.md) - 详细的使用指南和示例代码
- [功能规格说明](../../../../specs/4-infra-logging/spec.md) - 完整的功能需求说明
- [实施计划](../../../../specs/4-infra-logging/plan.md) - 技术实现方案
- [数据模型](../../../../specs/4-infra-logging/data-model.md) - 数据模型定义
- [日志格式规范](../../../../docs/engineering-standards/logging-format.md) - 项目日志格式规范

## 技术栈

- **Java**: JDK 21
- **Spring Boot**: 4.0.1
- **Logback**: 日志框架（Spring Boot 内置）
- **SLF4J**: 日志门面（Spring Boot 内置）
- **Spring Cloud OpenFeign**: 用于 Feign 拦截器（可选）

## 版本信息

- **当前版本**: 1.0.0
- **最后更新**: 2026-01-27

