# 日志格式规范

## 概述

本文档定义了 Atlas 项目的日志输出格式规范，确保所有模块的日志格式统一，便于问题排查、监控和日志分析。

## 日志格式模板

### 标准格式

所有模块必须使用以下统一的日志格式模板：

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} [TraceId: %X{traceId}] - %msg%n
```

### 格式说明

| 占位符 | 说明 | 示例 |
|--------|------|------|
| `%d{yyyy-MM-dd HH:mm:ss.SSS}` | 时间戳，精确到毫秒 | `2025-01-27 14:30:45.123` |
| `[%thread]` | 线程名称 | `[main]` 或 `[http-nio-8080-exec-1]` |
| `%-5level` | 日志级别，左对齐，5 位宽度 | `INFO `、`ERROR`、`DEBUG` |
| `%logger{50}` | Logger 名称，最大 50 个字符 | `com.atlas.system.service.UserService` |
| `[TraceId: %X{traceId}]` | 链路追踪 ID | `[TraceId: abc123def456]` |
| `%msg` | 日志消息内容 | 实际的日志消息 |
| `%n` | 换行符 | 平台相关的换行符 |

### 日志输出示例

```
2025-01-27 14:30:45.123 [http-nio-8080-exec-1] INFO  c.a.s.service.UserService [TraceId: abc123def456] - 创建用户成功: username=admin
2025-01-27 14:30:45.234 [http-nio-8080-exec-1] ERROR c.a.s.controller.UserController [TraceId: abc123def456] - 用户创建失败: username=admin, error=用户名已存在
```

## 日志级别

### 级别定义

Atlas 项目使用以下日志级别（按严重程度从低到高）：

1. **TRACE**: 最详细的调试信息，通常只在开发时使用
2. **DEBUG**: 调试信息，用于开发环境问题排查
3. **INFO**: 一般信息，记录程序正常运行的关键信息
4. **WARN**: 警告信息，表示潜在问题，但不影响程序运行
5. **ERROR**: 错误信息，表示发生了错误，但程序可以继续运行
6. **FATAL**: 严重错误，通常会导致程序终止（Logback 中通常使用 ERROR）

### 级别使用规范

| 级别 | 使用场景 | 示例 |
|------|----------|------|
| **TRACE** | 详细的执行流程跟踪 | 方法进入/退出、参数值、返回值 |
| **DEBUG** | 开发调试信息 | 变量值、条件判断结果、中间状态 |
| **INFO** | 业务关键操作 | 用户登录、数据创建、重要状态变更 |
| **WARN** | 异常情况但不影响功能 | 配置缺失使用默认值、性能警告、降级操作 |
| **ERROR** | 错误和异常 | 异常捕获、业务失败、系统错误 |

### 环境级别配置

| 环境 | 推荐级别 | 说明 |
|------|----------|------|
| **开发环境 (dev)** | DEBUG | 显示详细的调试信息 |
| **测试环境 (test)** | INFO | 显示业务关键信息 |
| **生产环境 (prod)** | INFO | 显示业务关键信息，避免过多日志 |

**注意**: 生产环境不应使用 DEBUG 或 TRACE 级别，避免产生过多日志影响性能。

## TraceId 输出规范

### TraceId 作用

TraceId（链路追踪 ID）用于：
- 追踪一次请求在整个微服务系统中的完整调用链
- 关联同一请求在不同服务中的日志
- 快速定位问题发生的完整上下文

### TraceId 格式

- **格式**: 字符串，建议使用 UUID 或雪花算法生成的 ID
- **长度**: 建议 16-32 个字符
- **示例**: `abc123def456`、`550e8400-e29b-41d4-a716-446655440000`

### TraceId 传递

TraceId 应在以下场景中传递：

1. **HTTP 请求**: 通过 HTTP Header（如 `X-Trace-Id`）传递
2. **Feign 调用**: 通过 Feign 拦截器自动传递
3. **消息队列**: 通过消息 Header 传递
4. **异步任务**: 通过 ThreadLocal 或 Context 传递

### TraceId 输出位置

TraceId 必须在日志格式中输出，位置在 Logger 名称之后、消息内容之前：

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} [TraceId: %X{traceId}] - %msg%n
```

**注意**: 如果 TraceId 不存在，将输出 `[TraceId: ]`，这是正常情况。

## 日志配置

### Logback 配置示例

在 `src/main/resources/logback-spring.xml` 中配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 引入 Spring Boot 默认配置 -->
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>

    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} [TraceId: %X{traceId}] - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 文件输出 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/atlas.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} [TraceId: %X{traceId}] - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/atlas.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
    </appender>

    <!-- 错误日志单独输出 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/atlas-error.log</file>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} [TraceId: %X{traceId}] - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/atlas-error.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>90</maxHistory>
        </rollingPolicy>
    </appender>

    <!-- 根 Logger 配置 -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>

    <!-- 应用特定 Logger 配置 -->
    <logger name="com.atlas" level="DEBUG"/>
    <logger name="org.springframework" level="INFO"/>
    <logger name="org.mybatis" level="DEBUG"/>
</configuration>
```

### 环境特定配置

使用 `logback-spring.xml` 支持环境特定配置：

```xml
<!-- 开发环境 -->
<springProfile name="dev">
    <root level="DEBUG">
        <appender-ref ref="CONSOLE"/>
    </root>
</springProfile>

<!-- 生产环境 -->
<springProfile name="prod">
    <root level="INFO">
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
</springProfile>
```

## 日志使用规范

### 代码中使用日志

```java
package com.atlas.system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 * 
 * @author Atlas Team
 * @date 2025-01-27
 */
@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void createUser(User user) {
        log.info("开始创建用户: username={}", user.getUsername());
        
        try {
            // 业务逻辑
            log.debug("用户数据验证通过: username={}", user.getUsername());
            
            // 创建用户
            log.info("用户创建成功: username={}, userId={}", user.getUsername(), user.getId());
        } catch (Exception e) {
            log.error("用户创建失败: username={}, error={}", user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }
}
```

### 日志消息规范

1. **使用占位符**: 使用 `{}` 占位符，避免字符串拼接
   ```java
   // ✅ 正确
   log.info("用户登录成功: username={}, ip={}", username, ip);
   
   // ❌ 错误
   log.info("用户登录成功: username=" + username + ", ip=" + ip);
   ```

2. **包含关键信息**: 日志消息应包含足够的信息用于问题排查
   ```java
   // ✅ 正确
   log.error("订单创建失败: orderId={}, userId={}, amount={}, error={}", 
             orderId, userId, amount, e.getMessage(), e);
   
   // ❌ 错误（信息不足）
   log.error("订单创建失败", e);
   ```

3. **避免敏感信息**: 不要在日志中输出密码、Token、身份证号等敏感信息
   ```java
   // ✅ 正确
   log.info("用户登录: username={}", username);
   
   // ❌ 错误
   log.info("用户登录: username={}, password={}", username, password);
   ```

4. **异常日志**: 记录异常时，应同时记录异常对象
   ```java
   // ✅ 正确
   log.error("处理失败: message={}", message, e);
   
   // ❌ 错误
   log.error("处理失败: message={}, error={}", message, e.getMessage());
   ```

## 日志文件管理

### 文件命名

- **应用日志**: `atlas-{module-name}.log`
- **错误日志**: `atlas-{module-name}-error.log`
- **滚动日志**: `atlas-{module-name}.{date}.{index}.log`

### 日志轮转

- **按时间轮转**: 每天生成新日志文件
- **按大小轮转**: 单个日志文件不超过 100MB
- **保留策略**: 普通日志保留 30 天，错误日志保留 90 天

### 日志目录

建议的日志目录结构：

```
logs/
├── atlas-system.log
├── atlas-system-error.log
├── atlas-gateway.log
├── atlas-gateway-error.log
└── ...
```

## 性能考虑

1. **避免频繁日志**: 避免在循环中输出大量日志
2. **使用合适的级别**: 生产环境使用 INFO 级别，避免 DEBUG/TRACE
3. **异步日志**: 对于高并发场景，考虑使用异步日志 Appender
4. **日志采样**: 对于高频日志，可以考虑采样输出

## 工具支持

### 日志分析工具

- **ELK Stack**: Elasticsearch + Logstash + Kibana
- **Loki + Grafana**: 轻量级日志聚合方案
- **SkyWalking**: 链路追踪和日志关联

### IDE 插件

- **IntelliJ IDEA**: 内置日志查看器，支持日志级别过滤
- **VS Code**: Log Viewer 插件

## 常见问题

### Q1: TraceId 为空怎么办？

**A**: TraceId 为空是正常情况，可能原因：
- 非 HTTP 请求触发的日志（如定时任务）
- TraceId 未正确传递
- 日志框架配置问题

### Q2: 如何在不同环境使用不同的日志级别？

**A**: 使用 `logback-spring.xml` 和 Spring Profile 配置环境特定的日志级别。

### Q3: 日志文件过大怎么办？

**A**: 配置日志轮转策略，按时间和大小轮转日志文件。

## 参考

- [Logback 官方文档](http://logback.qos.ch/)
- [SLF4J 使用指南](http://www.slf4j.org/manual.html)
- [项目宪法 - 代码注释规范](.specify/memory/constitution.md)

