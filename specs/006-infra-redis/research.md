# 技术调研文档

## 调研目标

为"实现 atlas-common-infra-redis 模块"功能提供技术决策依据，解决规划中的技术选型和设计问题。

## 决策记录

### 决策 1: Redis 序列化方式选择

**问题**: 如何选择 Redis 的序列化方式？

**决策**: 使用 GenericJackson2JsonRedisSerializer（Value）和 StringRedisSerializer（Key）

**理由**:
1. **可读性**: JSON 格式的 Value 在 Redis 中可读，便于调试和维护
2. **兼容性**: JSON 格式与前端和其他系统兼容性好
3. **性能**: JSON 序列化性能良好，满足大部分场景需求
4. **Key 格式**: String 格式的 Key 便于管理和查询，符合 Redis 最佳实践

**实现方式**:
```java
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        // Key 使用 String 序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Value 使用 JSON 序列化
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        return template;
    }
}
```

**替代方案考虑**:
- **JdkSerializationRedisSerializer**: 二进制格式，不可读，不便于调试，不推荐
- **Jackson2JsonRedisSerializer**: 功能类似，但 GenericJackson2JsonRedisSerializer 支持类型信息，更灵活

### 决策 2: Key 命名规范实现方式

**问题**: 如何实现统一的 Redis Key 命名规范？

**决策**: 使用 Builder 模式构建 Key，支持链式调用

**理由**:
1. **易用性**: 链式调用简洁易用，符合现代 Java 编程习惯
2. **灵活性**: Builder 模式支持可选参数，灵活配置
3. **可维护性**: 集中管理 Key 命名规范，便于维护和扩展
4. **一致性**: 统一的 Key 格式，避免命名冲突

**实现方式**:
```java
public class RedisKeyBuilder {
    private String module;
    private String business;
    private String id;
    private String prefix;
    
    public static RedisKeyBuilder builder() {
        return new RedisKeyBuilder();
    }
    
    public RedisKeyBuilder module(String module) {
        this.module = module;
        return this;
    }
    
    public RedisKeyBuilder business(String business) {
        this.business = business;
        return this;
    }
    
    public RedisKeyBuilder id(String id) {
        this.id = id;
        return this;
    }
    
    public String build() {
        // 构建 Key: {prefix}:{module}:{business}:{id}
        return String.join(":", prefix, module, business, id);
    }
}
```

**替代方案考虑**:
- **字符串拼接**: 简单但容易出错，不推荐
- **配置类**: 不够灵活，不推荐

### 决策 3: 缓存工具类设计方式

**问题**: 如何设计缓存工具类，简化业务代码使用？

**决策**: 使用静态工具类封装 RedisTemplate，提供统一的异常处理

**理由**:
1. **简化使用**: 业务代码无需注入 RedisTemplate，直接调用静态方法
2. **统一异常处理**: 集中处理异常，记录日志，不抛出异常
3. **类型安全**: 支持泛型，类型安全
4. **易于测试**: 可以 Mock RedisTemplate，便于单元测试

**实现方式**:
```java
@Component
public class CacheUtil {
    private static RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        CacheUtil.redisTemplate = redisTemplate;
    }
    
    public static <T> void set(String key, T value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("缓存设置失败: key={}", key, e);
        }
    }
    
    public static <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return clazz.cast(value);
        } catch (Exception e) {
            log.error("缓存获取失败: key={}", key, e);
            return null;
        }
    }
}
```

**替代方案考虑**:
- **实例方法**: 需要注入，使用不够简洁，不推荐
- **Spring Cache 注解**: 功能强大但配置复杂，适合高级场景，本模块提供基础工具类

### 决策 4: Key 前缀配置方式

**问题**: 如何配置 Key 前缀，统一管理 Key 命名空间？

**决策**: 使用配置文件（application.yml）配置 Key 前缀

**理由**:
1. **灵活性**: 不同环境可以使用不同的前缀
2. **集中管理**: 配置集中管理，便于维护
3. **默认值**: 提供默认前缀，简化配置

**实现方式**:
```yaml
# application.yml
atlas:
  redis:
    key-prefix: "atlas"
```

```java
@ConfigurationProperties(prefix = "atlas.redis")
public class RedisProperties {
    private String keyPrefix = "atlas";
    
    // getter and setter
}
```

**替代方案考虑**:
- **硬编码**: 不够灵活，不推荐
- **环境变量**: 可以，但配置文件更直观

## 技术选型总结

| 技术点 | 选型 | 理由 |
|--------|------|------|
| Redis 序列化 | GenericJackson2JsonRedisSerializer（Value）+ StringRedisSerializer（Key） | JSON 格式可读性好，便于调试 |
| Key 命名规范 | Builder 模式 | 链式调用简洁易用，符合现代 Java 编程习惯 |
| 缓存工具类 | 静态工具类 | 简化使用，统一异常处理 |
| Key 前缀配置 | 配置文件（YAML） | 灵活，集中管理 |

## 参考资源

- [Spring Data Redis 官方文档](https://spring.io/projects/spring-data-redis)
- [Redis 最佳实践](https://redis.io/docs/manual/patterns/)
- [Jackson JSON 序列化](https://github.com/FasterXML/jackson)

