# 技术调研文档

## 调研目标

为"实现 atlas-common-infra-web 模块"功能提供技术决策依据，解决规划中的技术选型和设计问题。

## 决策记录

### 决策 1: 全局异常处理实现方式

**问题**: 如何实现全局异常处理？

**决策**: 使用 Spring Boot 的 `@RestControllerAdvice` 注解

**理由**:
1. **官方推荐**: Spring Boot 官方推荐使用 `@RestControllerAdvice` 实现全局异常处理
2. **简单易用**: 无需额外配置，Spring Boot 自动扫描和注册
3. **功能完整**: 支持处理 Controller 层抛出的所有异常
4. **灵活扩展**: 可以针对不同异常类型定义不同的处理方法

**实现方式**:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getErrorCode(), e.getMessage());
    }
}
```

**替代方案考虑**:
- **@ControllerAdvice + @ResponseBody**: 功能相同，但需要额外注解
- **实现 HandlerExceptionResolver**: 更底层，但配置复杂
- **AOP 切面**: 可以实现，但不如 `@RestControllerAdvice` 直观

### 决策 2: 参数校验异常处理方式

**问题**: 如何处理 Spring Validation 的参数校验异常？

**决策**: 在全局异常处理器中统一处理 `MethodArgumentNotValidException` 和 `ConstraintViolationException`

**理由**:
1. **统一处理**: 与业务异常处理保持一致，统一返回 `Result` 格式
2. **信息提取**: 可以从异常中提取字段错误信息（字段名、错误消息）
3. **格式统一**: 校验错误响应格式与业务异常响应格式一致

**实现方式**:
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public Result<ValidationError> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {
    List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
    ValidationError validationError = ValidationError.from(fieldErrors);
    return Result.error(CommonErrorCode.PARAMETER_ERROR, "参数校验失败", validationError);
}
```

**替代方案考虑**:
- **自定义 Validator**: 可以实现，但需要修改现有代码
- **AOP 切面**: 可以实现，但不如异常处理统一

### 决策 3: Jackson 配置方式

**问题**: 如何配置 Jackson 的 JSON 序列化？

**决策**: 使用 `@Configuration` 配置类，创建 `ObjectMapper` Bean

**理由**:
1. **Spring Boot 集成**: Spring Boot 自动配置机制，可以覆盖默认配置
2. **统一管理**: 所有 Jackson 配置集中在一个配置类中
3. **易于扩展**: 可以方便地添加自定义序列化器和反序列化器

**实现方式**:
```java
@Configuration
public class JacksonConfig {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // ... 其他配置
        return mapper;
    }
}
```

**替代方案考虑**:
- **application.yml 配置**: 功能有限，无法配置自定义序列化器
- **@JsonFormat 注解**: 需要每个字段单独配置，不够统一

### 决策 4: TraceId Filter 实现方式

**问题**: 如何实现 TraceId Filter？

**决策**: 使用 Servlet `Filter` 接口，在请求的最早阶段设置 TraceId

**理由**:
1. **执行顺序**: Filter 在请求的最早阶段执行，早于 Interceptor 和 Controller
2. **覆盖全面**: 可以处理所有 HTTP 请求，包括静态资源请求
3. **与 Interceptor 互补**: 虽然 `atlas-common-infra-logging` 模块已经提供了 `TraceIdInterceptor`，但 Filter 执行更早，可以确保 TraceId 在所有场景下都能正确设置

**实现方式**:
```java
@Component
public class TraceIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String traceId = httpRequest.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = TraceIdUtil.generate();
        }
        TraceIdUtil.setTraceId(traceId);
        try {
            chain.doFilter(request, response);
        } finally {
            TraceIdUtil.clear();
        }
    }
}
```

**替代方案考虑**:
- **仅使用 Interceptor**: 执行顺序较晚，可能在某些场景下无法正确设置 TraceId
- **仅使用 Filter**: 可以满足需求，但 Interceptor 提供了更细粒度的控制

### 决策 5: Long 类型序列化策略

**问题**: 如何处理 Long 类型序列化，避免前端 JavaScript 精度丢失？

**决策**: 使用自定义序列化器，将 Long 类型序列化为 String

**理由**:
1. **精度问题**: JavaScript 的 Number 类型只能安全表示 2^53 以内的整数，超过此范围的 Long 值会丢失精度
2. **前端兼容**: 将 Long 序列化为 String，前端可以安全处理
3. **统一处理**: 通过自定义序列化器，统一处理所有 Long 类型字段

**实现方式**:
```java
public class LongToStringSerializer extends JsonSerializer<Long> {
    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) 
            throws IOException {
        gen.writeString(String.valueOf(value));
    }
}
```

**替代方案考虑**:
- **前端处理**: 前端可以使用 `bigint` 或字符串处理，但需要每个字段单独处理
- **@JsonSerialize 注解**: 需要每个字段单独配置，不够统一

### 决策 6: 异常日志记录策略

**问题**: 如何记录异常日志？

**决策**: 在全局异常处理器中记录异常日志，区分不同日志级别

**理由**:
1. **统一管理**: 所有异常日志在全局异常处理器中统一记录
2. **级别区分**: 业务异常使用 INFO 级别，系统异常使用 ERROR 级别
3. **信息完整**: 记录异常堆栈信息，便于问题排查

**实现方式**:
```java
@ExceptionHandler(BusinessException.class)
public Result<Void> handleBusinessException(BusinessException e) {
    log.info("业务异常: errorCode={}, message={}", e.getErrorCode(), e.getMessage());
    return Result.error(e.getErrorCode(), e.getMessage());
}

@ExceptionHandler(Exception.class)
public Result<Void> handleException(Exception e) {
    log.error("系统异常", e);
    return Result.error(CommonErrorCode.SYSTEM_ERROR, "系统错误");
}
```

**替代方案考虑**:
- **AOP 切面记录**: 可以实现，但不如在异常处理器中记录直观
- **不记录日志**: 不利于问题排查，不推荐

## 技术选型总结

| 技术点 | 选型 | 理由 |
|--------|------|------|
| 全局异常处理 | `@RestControllerAdvice` | Spring Boot 官方推荐，简单易用 |
| 参数校验异常处理 | 统一在全局异常处理器中处理 | 统一返回格式，易于维护 |
| Jackson 配置 | `@Configuration` 配置类 | Spring Boot 集成，易于扩展 |
| TraceId Filter | Servlet `Filter` 接口 | 执行顺序早，覆盖全面 |
| Long 序列化 | 自定义序列化器 | 解决前端精度问题 |
| 异常日志记录 | 在异常处理器中记录 | 统一管理，信息完整 |

## 参考资源

- [Spring Boot Exception Handling](https://spring.io/guides/tutorials/rest/)
- [Jackson Custom Serializers](https://www.baeldung.com/jackson-custom-serialization)
- [Servlet Filter vs Interceptor](https://www.baeldung.com/spring-mvc-handlerinterceptor-vs-filter)

