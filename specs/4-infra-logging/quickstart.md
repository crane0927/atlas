# 快速开始指南

## 概述

本指南帮助您快速了解和使用 `atlas-common-infra-logging` 模块提供的日志基础设施功能。

## 模块介绍

`atlas-common-infra-logging` 是 Atlas 项目的日志基础设施模块，提供：

1. **Logback 日志配置规范**: 统一的日志格式和配置模板
2. **TraceId 自动注入和管理**: 支持分布式链路追踪
3. **敏感信息脱敏工具**: 保护敏感信息，防止泄露

## 快速开始

### 1. 添加依赖

在子模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.atlas</groupId>
    <artifactId>atlas-common-infra-logging</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置日志

在模块的 `src/main/resources` 目录下创建 `logback-spring.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 引入标准配置模板 -->
    <include resource="com/atlas/common/infra/logging/config/logback-default.xml"/>
    
    <!-- 模块特定配置 -->
    <logger name="com.atlas.yourmodule" level="DEBUG"/>
    
    <!-- 环境特定配置 -->
    <springProfile name="dev">
        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>
    
    <springProfile name="prod">
        <root level="INFO">
            <appender-ref ref="FILE"/>
            <appender-ref ref="ERROR_FILE"/>
        </root>
    </springProfile>
</configuration>
```

### 3. 配置 TraceId 拦截器

在 Spring Boot 配置类中注册拦截器：

```java
import com.atlas.common.infra.logging.trace.TraceIdInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TraceIdInterceptor())
                .addPathPatterns("/**");
    }
}
```

### 4. 配置 Feign 拦截器（可选）

如果使用 Feign 调用下游服务，配置 Feign 拦截器：

```java
import com.atlas.common.infra.logging.trace.TraceIdFeignInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    
    @Bean
    public TraceIdFeignInterceptor traceIdFeignInterceptor() {
        return new TraceIdFeignInterceptor();
    }
}
```

### 5. 配置异步任务 TraceId 传递（可选）

如果使用异步任务，配置 TaskDecorator：

```java
import com.atlas.common.infra.logging.async.TraceIdTaskDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setTaskDecorator(new TraceIdTaskDecorator());
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
```

## 使用示例

### 示例 1: 正常使用日志（TraceId 自动包含）

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    public void createUser(User user) {
        // TraceId 会自动包含在日志中，无需手动处理
        log.info("开始创建用户: username={}", user.getUsername());
        
        try {
            // 业务逻辑
            log.debug("用户数据验证通过: username={}", user.getUsername());
            
            // 创建用户
            log.info("用户创建成功: username={}, userId={}", 
                    user.getUsername(), user.getId());
        } catch (Exception e) {
            log.error("用户创建失败: username={}, error={}", 
                    user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }
}
```

**日志输出示例**:
```
2025-01-27 14:30:45.123 [http-nio-8080-exec-1] INFO  c.a.s.service.UserService [TraceId: abc123def456] - 开始创建用户: username=admin
2025-01-27 14:30:45.234 [http-nio-8080-exec-1] INFO  c.a.s.service.UserService [TraceId: abc123def456] - 用户创建成功: username=admin, userId=1001
```

### 示例 2: 手动设置 TraceId

```java
import com.atlas.common.infra.logging.trace.TraceIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduledTask {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);
    
    @Scheduled(cron = "0 0 1 * * ?")
    public void dailyTask() {
        // 手动设置 TraceId（定时任务没有 HTTP 请求）
        TraceIdUtil.setTraceId(TraceIdUtil.generate());
        try {
            log.info("开始执行定时任务");
            // 业务逻辑
            log.info("定时任务执行完成");
        } finally {
            // 清理 TraceId
            TraceIdUtil.clear();
        }
    }
}
```

### 示例 3: 使用脱敏工具

```java
import com.atlas.common.infra.logging.desensitize.DesensitizeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    public void logUserInfo(User user) {
        // 手动脱敏
        String maskedPhone = DesensitizeUtil.maskPhone(user.getPhone());
        String maskedIdCard = DesensitizeUtil.maskIdCard(user.getIdCard());
        
        log.info("用户信息: phone={}, idCard={}", maskedPhone, maskedIdCard);
        // 输出: 用户信息: phone=138****5678, idCard=110101****1234
    }
}
```

### 示例 4: 使用 @Sensitive 注解自动脱敏

```java
import com.atlas.common.infra.logging.desensitize.annotation.Sensitive;
import com.atlas.common.infra.logging.desensitize.annotation.SensitiveType;

public class User {
    private Long id;
    private String username;
    
    @Sensitive(type = SensitiveType.PHONE)
    private String phone;
    
    @Sensitive(type = SensitiveType.ID_CARD)
    private String idCard;
    
    @Sensitive(type = SensitiveType.EMAIL)
    private String email;
    
    // getters and setters
}

// 使用
@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    public void logUser(User user) {
        // 对象字段会自动脱敏
        log.info("用户信息: {}", user);
        // 输出: 用户信息: User{id=1, username=admin, phone=138****5678, idCard=110101****1234, email=ab****@example.com}
    }
}
```

### 示例 5: 自定义脱敏规则

```java
import com.atlas.common.infra.logging.desensitize.DesensitizeRule;
import com.atlas.common.infra.logging.desensitize.DesensitizeUtil;
import java.util.regex.Pattern;

// 自定义脱敏规则
DesensitizeRule customRule = DesensitizeRule.builder()
    .fieldType("custom")
    .pattern(Pattern.compile("\\d{4}-\\d{4}-\\d{4}"))
    .prefixLength(4)
    .suffixLength(4)
    .replacement("****")
    .build();

// 使用自定义规则
String masked = DesensitizeUtil.mask("1234-5678-9012", customRule);
// 输出: 1234****9012
```

### 示例 6: Feign 调用自动传递 TraceId

```java
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/users/{id}")
    User getUser(@PathVariable Long id);
    // TraceId 会自动通过请求头 X-Trace-Id 传递到下游服务
}
```

### 示例 7: 异步任务 TraceId 传递

```java
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AsyncService {
    private static final Logger log = LoggerFactory.getLogger(AsyncService.class);
    
    @Async
    public void asyncTask() {
        // TraceId 会自动从父线程继承
        log.info("异步任务执行: TraceId 已自动传递");
        // 业务逻辑
    }
}
```

## 配置说明

### 日志格式配置

日志格式模板定义在 `logback-default.xml` 中：

```xml
<property name="LOG_PATTERN" 
          value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} [TraceId: %X{traceId}] - %msg%n"/>
```

### TraceId 请求头配置

默认使用 `X-Trace-Id` 作为请求头名称，可以通过配置修改：

```yaml
# application.yml
atlas:
  logging:
    trace-id:
      header-name: X-Trace-Id
      mdc-key: traceId
      generator: UUID  # UUID 或 SNOWFLAKE
```

### 脱敏规则配置

可以通过配置文件自定义脱敏规则：

```yaml
# application.yml
atlas:
  logging:
    desensitize:
      enable-auto: true
      rules:
        - type: phone
          prefix-length: 3
          suffix-length: 4
        - type: id-card
          prefix-length: 6
          suffix-length: 4
```

## 注意事项

1. **TraceId 清理**: TraceId 会在请求结束后自动清理，无需手动处理
2. **异步任务**: 使用 `@Async` 时，确保配置了 `TraceIdTaskDecorator`
3. **Feign 调用**: 确保配置了 `TraceIdFeignInterceptor`，TraceId 才会自动传递
4. **脱敏性能**: 自动脱敏可能影响日志性能，生产环境建议评估性能影响
5. **日志级别**: 生产环境应使用 INFO 级别，避免 DEBUG/TRACE 产生过多日志

## 常见问题

### Q1: TraceId 为空怎么办？

**A**: TraceId 为空可能是以下原因：
- 非 HTTP 请求触发的日志（如定时任务），需要手动设置 TraceId
- 拦截器未正确配置
- 日志配置中未包含 TraceId 格式

**解决方案**: 
- 检查拦截器配置
- 手动设置 TraceId：`TraceIdUtil.setTraceId(TraceIdUtil.generate())`

### Q2: 如何在不同环境使用不同的日志级别？

**A**: 使用 `logback-spring.xml` 和 Spring Profile：

```xml
<springProfile name="dev">
    <root level="DEBUG">
        <appender-ref ref="CONSOLE"/>
    </root>
</springProfile>

<springProfile name="prod">
    <root level="INFO">
        <appender-ref ref="FILE"/>
    </root>
</springProfile>
```

### Q3: 脱敏规则不匹配怎么办？

**A**: 
- 检查字段格式是否符合规则
- 使用自定义脱敏规则
- 手动调用脱敏方法

### Q4: 如何禁用自动脱敏？

**A**: 在配置文件中设置：

```yaml
atlas:
  logging:
    desensitize:
      enable-auto: false
```

## 相关文档

- [功能规格说明](./spec.md) - 完整的功能需求说明
- [实施计划](./plan.md) - 技术实现方案
- [数据模型](./data-model.md) - 数据模型定义
- [技术调研](./research.md) - 技术决策和设计依据
- [日志格式规范](../../docs/engineering-standards/logging-format.md) - 项目日志格式规范

