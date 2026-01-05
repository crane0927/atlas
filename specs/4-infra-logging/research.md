# 技术调研文档

## 调研目标

为"实现 atlas-common-infra-logging 模块"功能提供技术决策依据，解决规划中的技术选型和设计问题。

## 决策记录

### 决策 1: TraceId 生成策略

**问题**: 如何生成 TraceId？

**决策**: 优先使用 UUID（32位，去除连字符），可选使用雪花算法

**理由**:
1. **UUID 优势**: 
   - 全局唯一，无需协调
   - 实现简单，性能好
   - 适合分布式环境
   - 32位字符串长度适中（16-32字符）

2. **雪花算法优势**:
   - 有序性，便于排序和查询
   - 包含时间戳信息
   - 适合高并发场景

3. **默认选择**: UUID 作为默认方案，因为实现简单且满足需求

**实现方式**:
```java
// UUID 生成（默认）
String traceId = UUID.randomUUID().toString().replace("-", "");

// 雪花算法生成（可选）
// 需要实现雪花算法生成器
```

**替代方案考虑**:
- **自增ID**: 不适合分布式环境，需要协调
- **时间戳+随机数**: 可能重复，不够可靠

### 决策 2: TraceId 存储方式

**问题**: 如何存储和管理 TraceId？

**决策**: 使用 ThreadLocal + MDC 双重存储

**理由**:
1. **ThreadLocal**: 
   - 线程隔离，线程安全
   - 性能好，访问快速
   - 支持手动设置和获取

2. **MDC (Mapped Diagnostic Context)**:
   - Logback 原生支持
   - 自动输出到日志
   - 与日志框架深度集成

3. **双重存储**: 
   - ThreadLocal 用于业务代码访问
   - MDC 用于日志自动输出
   - 确保一致性和可靠性

**实现方式**:
```java
// ThreadLocal 存储
private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

// MDC 存储
MDC.put("traceId", traceId);
```

**替代方案考虑**:
- **仅使用 ThreadLocal**: 无法自动输出到日志
- **仅使用 MDC**: 业务代码访问不便

### 决策 3: TraceId 传递机制

**问题**: 如何在 HTTP 请求、Feign 调用、异步任务中传递 TraceId？

**决策**: 使用拦截器和装饰器模式

**理由**:
1. **HTTP 请求拦截器**:
   - Spring MVC 的 HandlerInterceptor
   - 自动从请求头获取或生成 TraceId
   - 请求结束后自动清理

2. **Feign 拦截器**:
   - Feign 的 RequestInterceptor
   - 自动将 TraceId 添加到请求头
   - 支持微服务调用链追踪

3. **异步任务装饰器**:
   - Spring 的 TaskDecorator
   - 继承父线程的 TraceId
   - 确保异步任务日志可追溯

**实现方式**:
```java
// HTTP 拦截器
public class TraceIdInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(...) {
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null) {
            traceId = TraceIdGenerator.generate();
        }
        TraceIdUtil.setTraceId(traceId);
        return true;
    }
    
    @Override
    public void afterCompletion(...) {
        TraceIdUtil.clear();
    }
}

// Feign 拦截器
public class TraceIdFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String traceId = TraceIdUtil.getTraceId();
        if (traceId != null) {
            template.header("X-Trace-Id", traceId);
        }
    }
}
```

**替代方案考虑**:
- **手动传递**: 容易遗漏，维护成本高
- **AOP 切面**: 实现复杂，性能开销大

### 决策 4: 敏感信息脱敏策略

**问题**: 如何实现敏感信息脱敏？

**决策**: 使用正则表达式匹配 + 规则替换

**理由**:
1. **正则表达式**:
   - 灵活，支持各种模式匹配
   - 性能可接受（编译后缓存）
   - 易于维护和扩展

2. **规则替换**:
   - 保留前后缀，便于识别
   - 中间部分用 `****` 替代
   - 符合常见脱敏规范

3. **双重保护**:
   - 手动脱敏：开发人员主动调用
   - 自动脱敏：日志拦截器自动处理

**实现方式**:
```java
// 手机号脱敏：138****5678
public static String maskPhone(String phone) {
    if (phone == null || phone.length() < 7) {
        return phone;
    }
    return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
}

// 正则匹配脱敏
private static final Pattern PHONE_PATTERN = Pattern.compile("1[3-9]\\d{9}");
```

**替代方案考虑**:
- **完全替换**: 信息丢失，不利于问题排查
- **加密存储**: 性能开销大，不适合日志场景

### 决策 5: 日志配置模板设计

**问题**: 如何设计日志配置模板？

**决策**: 提供可复用的配置模板，支持模块自定义扩展

**理由**:
1. **统一格式**: 
   - 所有模块使用相同的日志格式
   - 包含 TraceId，便于链路追踪
   - 符合项目日志格式规范

2. **灵活扩展**:
   - 使用 `<include>` 引入基础配置
   - 模块可以覆盖特定配置
   - 支持环境特定配置

3. **中文注释**:
   - 配置模板包含完整的中文注释
   - 便于理解和维护

**实现方式**:
```xml
<!-- 基础配置模板 logback-default.xml -->
<configuration>
    <!-- 日志格式定义 -->
    <property name="LOG_PATTERN" 
              value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} [TraceId: %X{traceId}] - %msg%n"/>
    
    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- 文件输出和轮转配置 -->
    <!-- ... -->
</configuration>

<!-- 模块使用 -->
<configuration>
    <include resource="com/atlas/common/infra/logging/config/logback-default.xml"/>
    <!-- 模块特定配置 -->
</configuration>
```

**替代方案考虑**:
- **完全统一**: 不够灵活，难以满足特殊需求
- **各自配置**: 格式不统一，维护成本高

## 最佳实践参考

### TraceId 管理最佳实践

1. **生成时机**: 在请求入口处生成，避免重复生成
2. **传递方式**: 使用请求头传递，标准化的 Header 名称（X-Trace-Id）
3. **清理机制**: 使用拦截器的 afterCompletion 确保清理
4. **异步任务**: 使用装饰器继承父线程的 TraceId

### 脱敏工具最佳实践

1. **性能优化**: 编译正则表达式并缓存，避免重复编译
2. **规则配置**: 支持配置化，便于调整和扩展
3. **双重保护**: 手动脱敏 + 自动脱敏，确保安全
4. **保留信息**: 保留前后缀，便于问题排查

### 日志配置最佳实践

1. **格式统一**: 所有模块使用相同的日志格式
2. **环境区分**: 使用 Spring Profile 区分不同环境的日志级别
3. **文件管理**: 配置日志轮转，避免文件过大
4. **错误分离**: 错误日志单独输出，便于问题定位

## 参考资料

1. [Logback 官方文档](http://logback.qos.ch/)
2. [SLF4J MDC 使用指南](http://www.slf4j.org/manual.html#mdc)
3. [Spring MVC 拦截器文档](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/interceptors.html)
4. [Feign 拦截器文档](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
5. [项目日志格式规范](../../docs/engineering-standards/logging-format.md)
6. [项目包名规范](../../docs/engineering-standards/package-naming.md)

## 待确认事项

无（所有技术决策已明确）

