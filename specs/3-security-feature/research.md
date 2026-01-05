# 技术调研文档

## 调研目标

为"实现 atlas-common-feature-security 模块"功能提供技术决策依据，解决规划中的技术选型和设计问题。

## 决策记录

### 决策 1: LoginUser 接口设计

**问题**: 如何设计登录用户信息模型接口？

**决策**: 采用 Java 接口设计，定义核心方法，支持扩展

**接口设计**:
```java
public interface LoginUser {
    // 基本信息
    Object getUserId();
    String getUsername();
    
    // 权限信息
    List<String> getRoles();
    List<String> getPermissions();
    
    // 权限判断
    boolean hasRole(String role);
    boolean hasPermission(String permission);
}
```

**理由**:
1. **接口抽象**: 使用接口而非抽象类，提供更大的灵活性
2. **类型灵活**: getUserId() 返回 Object，支持 String、Long 等不同类型
3. **方法清晰**: 提供明确的获取和判断方法，易于使用
4. **扩展支持**: 业务模块可以实现接口并添加自定义字段和方法

**替代方案考虑**:
- **使用抽象类**: 不够灵活，限制了实现类的继承
- **使用具体类**: 不符合抽象设计原则，绑定实现细节

### 决策 2: 权限注解设计

**问题**: 如何设计权限检查注解？

**决策**: 参考 Spring Security 和 Shiro 的设计，提供 @RequiresPermission 和 @RequiresRole 注解

**注解设计**:
```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    String[] value();
    Logical logical() default Logical.AND;
}

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
    String[] value();
    Logical logical() default Logical.AND;
}

public enum Logical {
    AND, OR
}
```

**理由**:
1. **元数据标记**: 注解仅作为元数据，不包含实现逻辑
2. **灵活组合**: 支持多个权限/角色的 AND/OR 组合
3. **作用域**: 支持类级别和方法级别
4. **标准设计**: 参考成熟框架的设计，易于理解和集成

**替代方案考虑**:
- **单一注解**: 不够灵活，难以区分权限和角色
- **使用配置**: 不符合注解驱动的开发模式

### 决策 3: SecurityContext 接口设计

**问题**: 如何设计安全上下文接口？

**决策**: 采用简洁的接口设计，只提供读取方法，不提供设置方法

**接口设计**:
```java
public interface SecurityContext {
    LoginUser getLoginUser();
    boolean isAuthenticated();
    void clear(); // 可选，由实现决定是否提供
}
```

**理由**:
1. **职责单一**: 只负责提供安全上下文信息，不负责设置
2. **实现灵活**: 设置逻辑由具体实现负责（ThreadLocal、Request Scope 等）
3. **简洁设计**: 接口方法少，易于实现和理解
4. **扩展支持**: 实现类可以添加额外的方法

**替代方案考虑**:
- **包含设置方法**: 不符合抽象层设计原则，绑定实现细节
- **使用抽象类**: 不够灵活，限制了实现方式

### 决策 4: SecurityContextHolder 设计

**问题**: 如何设计安全上下文持有者？

**决策**: 采用抽象类设计，提供静态方法签名，不包含具体实现

**设计**:
```java
public abstract class SecurityContextHolder {
    public static SecurityContext getContext() {
        // 由具体实现提供
        throw new UnsupportedOperationException("需要具体实现");
    }
    
    public static LoginUser getLoginUser() {
        SecurityContext context = getContext();
        return context != null ? context.getLoginUser() : null;
    }
    
    public static boolean isAuthenticated() {
        SecurityContext context = getContext();
        return context != null && context.isAuthenticated();
    }
}
```

**理由**:
1. **静态方法**: 提供便捷的静态方法，符合工具类设计模式
2. **抽象设计**: 不包含具体实现，由实现类提供
3. **便捷方法**: 提供 getLoginUser() 和 isAuthenticated() 便捷方法
4. **实现灵活**: 支持 ThreadLocal、Request Scope、全局单例等多种实现方式

**替代方案考虑**:
- **使用接口**: 无法提供静态方法，使用不便
- **使用具体类**: 不符合抽象设计原则

### 决策 5: 异常设计

**问题**: 是否需要定义安全相关的异常接口？

**决策**: 定义异常接口，但不包含具体实现

**异常设计**:
```java
public interface AuthenticationException {
    String getMessage();
}

public interface AuthorizationException {
    String getMessage();
}
```

**理由**:
1. **类型区分**: 区分认证异常和授权异常
2. **抽象设计**: 仅定义接口，不包含实现
3. **可选使用**: 实现类可以选择使用或定义自己的异常

**替代方案考虑**:
- **不定义异常**: 实现类需要自己定义，可能导致不一致
- **使用具体异常**: 不符合抽象设计原则

## 最佳实践参考

### Java 接口设计最佳实践

1. **接口隔离**: 接口方法应该聚焦于单一职责
2. **命名清晰**: 方法名应该清晰表达意图
3. **文档完整**: 所有接口和方法都应该有完整的 JavaDoc
4. **扩展性**: 设计时考虑未来扩展需求

### 注解设计最佳实践

1. **元数据原则**: 注解只包含元数据，不包含逻辑
2. **作用域明确**: 明确注解的作用域（类、方法、字段等）
3. **默认值**: 为可选属性提供合理的默认值
4. **组合支持**: 支持多个注解的组合使用

### 安全上下文管理最佳实践

1. **线程安全**: 考虑多线程环境下的安全性
2. **生命周期管理**: 明确安全上下文的生命周期
3. **清理机制**: 提供清理机制，避免内存泄漏
4. **性能考虑**: 考虑获取上下文的性能开销

## 参考资料

1. [Spring Security 架构文档](https://docs.spring.io/spring-security/reference/servlet/architecture.html)
2. [Apache Shiro 架构文档](https://shiro.apache.org/architecture.html)
3. [Java 接口设计最佳实践](https://docs.oracle.com/javase/tutorial/java/concepts/interface.html)
4. [Java 注解文档](https://docs.oracle.com/javase/tutorial/java/annotations/)
5. [项目包名规范](../../docs/engineering-standards/package-naming.md)

## 待确认事项

无（所有技术决策已明确）

