# 快速开始指南

## 概述

本文档提供 `atlas-system-api` 模块的快速开始指南，帮助开发人员快速集成和使用该模块。

## 前置条件

- JDK 21
- Spring Boot 4.0.1
- Spring Cloud 2025.1.0
- Spring Cloud Alibaba 2025.1.0
- Maven 3.6+

## 添加依赖

### 1. 在父 POM 中添加模块

如果 `atlas-system-api` 是项目的一个子模块，需要在父 `pom.xml` 中添加：

```xml
<modules>
    <module>atlas-system-api</module>
</modules>
```

### 2. 在业务服务中添加依赖

在需要使用 `atlas-system-api` 的业务服务（如 `atlas-auth`）的 `pom.xml` 中添加依赖：

```xml
<dependencies>
    <!-- Atlas System API -->
    <dependency>
        <groupId>com.atlas</groupId>
        <artifactId>atlas-system-api</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <!-- Spring Cloud OpenFeign -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
</dependencies>
```

## 启用 Feign 客户端

在业务服务的启动类上添加 `@EnableFeignClients` 注解：

```java
@SpringBootApplication
@EnableFeignClients(basePackages = "com.atlas.system.api.v1.feign")
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
```

## 使用示例

### 示例 1: 查询用户信息

```java
@Service
public class AuthService {
    
    @Autowired
    private UserQueryApi userQueryApi;
    
    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    public UserDTO getUserByUsername(String username) {
        Result<UserDTO> result = userQueryApi.getUserByUsername(username);
        
        if (!result.isSuccess()) {
            throw new BusinessException("查询用户失败: " + result.getMessage());
        }
        
        return result.getData();
    }
    
    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    public UserDTO getUserById(Long userId) {
        Result<UserDTO> result = userQueryApi.getUserById(userId);
        
        if (!result.isSuccess()) {
            throw new BusinessException("查询用户失败: " + result.getMessage());
        }
        
        return result.getData();
    }
}
```

### 示例 2: 查询用户权限

```java
@Service
public class AuthService {
    
    @Autowired
    private PermissionQueryApi permissionQueryApi;
    
    /**
     * 查询用户角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<String> getUserRoles(Long userId) {
        Result<List<String>> result = permissionQueryApi.getUserRoles(userId);
        
        if (!result.isSuccess()) {
            throw new BusinessException("查询用户角色失败: " + result.getMessage());
        }
        
        return result.getData();
    }
    
    /**
     * 查询用户权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public List<String> getUserPermissions(Long userId) {
        Result<List<String>> result = permissionQueryApi.getUserPermissions(userId);
        
        if (!result.isSuccess()) {
            throw new BusinessException("查询用户权限失败: " + result.getMessage());
        }
        
        return result.getData();
    }
    
    /**
     * 查询用户完整权限信息（角色+权限）
     *
     * @param userId 用户ID
     * @return 用户权限信息
     */
    public UserAuthoritiesDTO getUserAuthorities(Long userId) {
        Result<UserAuthoritiesDTO> result = permissionQueryApi.getUserAuthorities(userId);
        
        if (!result.isSuccess()) {
            throw new BusinessException("查询用户权限信息失败: " + result.getMessage());
        }
        
        return result.getData();
    }
}
```

### 示例 3: 完整的认证流程

```java
@Service
public class AuthService {
    
    @Autowired
    private UserQueryApi userQueryApi;
    
    @Autowired
    private PermissionQueryApi permissionQueryApi;
    
    /**
     * 用户登录认证
     *
     * @param username 用户名
     * @param password 密码
     * @return 认证结果
     */
    public AuthResult authenticate(String username, String password) {
        // 1. 查询用户信息
        Result<UserDTO> userResult = userQueryApi.getUserByUsername(username);
        if (!userResult.isSuccess()) {
            throw new AuthenticationException("用户不存在");
        }
        
        UserDTO user = userResult.getData();
        
        // 2. 验证用户状态
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AuthenticationException("用户状态异常: " + user.getStatus());
        }
        
        // 3. 验证密码（这里简化处理，实际应该使用加密验证）
        // if (!passwordEncoder.matches(password, user.getPassword())) {
        //     throw new AuthenticationException("密码错误");
        // }
        
        // 4. 查询用户权限
        Result<UserAuthoritiesDTO> authoritiesResult = 
            permissionQueryApi.getUserAuthorities(user.getUserId());
        UserAuthoritiesDTO authorities = authoritiesResult.isSuccess() 
            ? authoritiesResult.getData() 
            : new UserAuthoritiesDTO();
        
        // 5. 构建认证结果
        AuthResult authResult = new AuthResult();
        authResult.setUser(user);
        authResult.setRoles(authorities.getRoles());
        authResult.setPermissions(authorities.getPermissions());
        
        return authResult;
    }
}
```

## 配置说明

### Feign 客户端配置

在 `application.yml` 中配置 Feign 客户端：

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          atlas-system:
            connect-timeout: 5000
            read-timeout: 10000
            logger-level: basic
```

### 服务发现配置

确保 `atlas-system` 服务已注册到 Nacos：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: dev
        group: DEFAULT_GROUP
```

## 注意事项

1. **服务发现**: 确保 `atlas-system` 服务已启动并注册到服务注册中心
2. **异常处理**: Feign 调用可能抛出异常，需要适当处理
3. **超时配置**: 根据实际业务场景配置合适的超时时间
4. **负载均衡**: Feign 客户端自动使用 Spring Cloud LoadBalancer 进行负载均衡
5. **链路追踪**: TraceId 会自动通过 `TraceIdFeignInterceptor` 传递到下游服务

## 常见问题

### Q1: Feign 调用失败，提示服务不可用？

**A**: 检查以下几点：
- 确认 `atlas-system` 服务已启动
- 确认服务已注册到 Nacos
- 确认服务名称配置正确（`atlas-system`）
- 检查网络连接和防火墙设置

### Q2: 如何自定义 Feign 客户端配置？

**A**: 可以通过 `@FeignClient` 注解的 `configuration` 属性指定配置类：

```java
@FeignClient(
    name = "atlas-system",
    path = "/api/v1",
    configuration = CustomFeignConfig.class
)
public interface UserQueryApi {
    // ...
}
```

### Q3: 如何处理 Feign 调用异常？

**A**: 可以通过 `@FeignClient` 注解的 `fallback` 或 `fallbackFactory` 属性指定降级处理：

```java
@FeignClient(
    name = "atlas-system",
    path = "/api/v1",
    fallbackFactory = UserQueryApiFallbackFactory.class
)
public interface UserQueryApi {
    // ...
}
```

## 参考资源

- [Spring Cloud OpenFeign 官方文档](https://spring.io/projects/spring-cloud-openfeign)
- [API 契约定义](./contracts/README.md)
- [数据模型定义](./data-model.md)
- [技术调研文档](./research.md)

