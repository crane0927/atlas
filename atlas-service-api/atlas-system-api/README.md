# atlas-system-api

## 模块简介

`atlas-system-api` 是 Atlas 项目的系统服务 API 接口定义模块，定义系统域的 Feign 接口契约、DTO 对象和枚举常量，供 `atlas-auth` 服务和其他业务服务调用。

## 主要功能

### 1. 用户查询接口

提供用户查询的 Feign 接口，支持：
- 根据用户ID查询用户信息
- 根据用户名查询用户信息

**接口**: `UserQueryApi`

### 2. 权限查询接口

提供权限查询的 Feign 接口，支持：
- 查询用户角色列表
- 查询用户权限列表
- 查询用户完整权限信息（角色+权限）

**接口**: `PermissionQueryApi`

### 3. 数据传输对象（DTO）

提供用户和权限相关的 DTO 对象：
- `UserDTO` - 用户基本信息 DTO
- `UserAuthoritiesDTO` - 用户权限信息 DTO

### 4. 枚举常量

提供用户状态枚举：
- `UserStatus` - 用户状态枚举（ACTIVE、INACTIVE、LOCKED、DELETED）

### 5. 版本包管理

使用版本包管理接口，遵循接口兼容性规则：
- 当前版本：v1（`com.atlas.system.api.v1.*`）
- 破坏性变更必须通过新版本包（v2）实现

## 快速开始

### 添加依赖

在业务服务的 `pom.xml` 中添加依赖：

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

### 启用 Feign 客户端

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

### 使用示例

#### 查询用户信息

```java
@Service
public class AuthService {
    
    @Autowired
    private UserQueryApi userQueryApi;
    
    public UserDTO getUserByUsername(String username) {
        Result<UserDTO> result = userQueryApi.getUserByUsername(username);
        
        if (!result.isSuccess()) {
            throw new BusinessException("查询用户失败: " + result.getMessage());
        }
        
        return result.getData();
    }
}
```

#### 查询用户权限

```java
@Service
public class AuthService {
    
    @Autowired
    private PermissionQueryApi permissionQueryApi;
    
    public UserAuthoritiesDTO getUserAuthorities(Long userId) {
        Result<UserAuthoritiesDTO> result = permissionQueryApi.getUserAuthorities(userId);
        
        if (!result.isSuccess()) {
            throw new BusinessException("查询用户权限失败: " + result.getMessage());
        }
        
        return result.getData();
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

## 模块依赖

### 允许的依赖

- `atlas-common-feature-core` - 用于 Result、错误码、异常体系
- Spring Cloud OpenFeign - 用于 Feign 接口定义
- Lombok - 用于简化代码（可选）
- Jackson - 用于 JSON 序列化（Spring Boot 自带）

### 禁止的依赖

- `atlas-common-infra-web` - Web 实现层，API 模块不应依赖
- `atlas-common-infra-db` - 数据库实现层，API 模块不应依赖
- `atlas-common-infra-redis` - Redis 实现层，API 模块不应依赖
- Spring Web - Web 实现层，API 模块不应依赖
- MyBatis-Plus - 数据库实现层，API 模块不应依赖

**理由**: API 模块仅定义接口契约和 DTO，不包含实现逻辑，因此不应依赖实现层组件。

## 版本包管理

### 包结构

```
com.atlas.system.api.v1/
├── feign/          # Feign 接口定义
│   ├── UserQueryApi.java
│   └── PermissionQueryApi.java
├── model/          # 数据模型
│   ├── dto/        # DTO 对象定义
│   │   ├── UserDTO.java
│   │   └── UserAuthoritiesDTO.java
│   └── vo/         # VO 对象定义（预留，当前未使用）
└── enums/          # 枚举常量定义
    └── UserStatus.java
```

### 版本管理策略

- **当前版本**: v1
- **版本包组织**: `com.atlas.system.api.v1.*`
- **破坏性变更**: 必须通过新版本包（v2）实现

## 接口兼容性规则

### 接口兼容性

- 不允许删除接口方法
- 不允许修改接口方法签名（参数类型、返回类型）
- 新增接口方法必须向后兼容

### DTO 兼容性

- 不允许删除字段
- 不允许修改字段类型或语义
- 新增字段必须可空或提供默认值

### 枚举兼容性

- 不允许删除现有枚举值
- 新增枚举值必须向后兼容

## 注意事项

1. **服务发现**: Feign 客户端需要通过服务注册中心（Nacos）发现 `atlas-system` 服务
2. **负载均衡**: Feign 客户端自动使用 Spring Cloud LoadBalancer 进行负载均衡
3. **链路追踪**: TraceId 会自动通过 `TraceIdFeignInterceptor` 传递到下游服务
4. **异常处理**: 接口调用失败时，Feign 会抛出异常，需要客户端进行异常处理

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
- [API 契约定义](../../specs/009-system-api/contracts/README.md)
- [数据模型定义](../../specs/009-system-api/data-model.md)
- [快速开始指南](../../specs/009-system-api/quickstart.md)
- [技术调研文档](../../specs/009-system-api/research.md)

