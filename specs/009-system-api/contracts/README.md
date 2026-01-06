# API 契约定义

## 概述

本文档定义了 `atlas-system-api` 模块的 API 接口契约，包括用户查询和权限查询接口。这些接口通过 Feign 客户端定义，供 `atlas-auth` 服务和其他业务服务调用。

## 接口列表

### 用户查询接口

**接口名称**: `UserQueryApi`

**包名**: `com.atlas.system.api.v1.feign`

**服务名称**: `atlas-system`

**基础路径**: `/api/v1`

**接口方法**:

| 方法 | HTTP 方法 | 路径 | 描述 | 请求参数 | 响应体 |
|------|-----------|------|------|----------|--------|
| getUserById | GET | `/users/{userId}` | 根据用户ID查询用户信息 | `@PathVariable Long userId` | `Result<UserDTO>` |
| getUserByUsername | GET | `/users/by-username` | 根据用户名查询用户信息 | `@RequestParam String username` | `Result<UserDTO>` |

**接口定义示例**:

```java
@FeignClient(name = "atlas-system", path = "/api/v1")
public interface UserQueryApi {
    
    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/users/{userId}")
    Result<UserDTO> getUserById(@PathVariable Long userId);
    
    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/users/by-username")
    Result<UserDTO> getUserByUsername(@RequestParam String username);
}
```

### 权限查询接口

**接口名称**: `PermissionQueryApi`

**包名**: `com.atlas.system.api.v1.feign`

**服务名称**: `atlas-system`

**基础路径**: `/api/v1`

**接口方法**:

| 方法 | HTTP 方法 | 路径 | 描述 | 请求参数 | 响应体 |
|------|-----------|------|------|----------|--------|
| getUserRoles | GET | `/users/{userId}/roles` | 查询用户角色列表 | `@PathVariable Long userId` | `Result<List<String>>` |
| getUserPermissions | GET | `/users/{userId}/permissions` | 查询用户权限列表 | `@PathVariable Long userId` | `Result<List<String>>` |
| getUserAuthorities | GET | `/users/{userId}/authorities` | 查询用户完整权限信息（角色+权限） | `@PathVariable Long userId` | `Result<UserAuthoritiesDTO>` |

**接口定义示例**:

```java
@FeignClient(name = "atlas-system", path = "/api/v1")
public interface PermissionQueryApi {
    
    /**
     * 查询用户角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @GetMapping("/users/{userId}/roles")
    Result<List<String>> getUserRoles(@PathVariable Long userId);
    
    /**
     * 查询用户权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @GetMapping("/users/{userId}/permissions")
    Result<List<String>> getUserPermissions(@PathVariable Long userId);
    
    /**
     * 查询用户完整权限信息（角色+权限）
     *
     * @param userId 用户ID
     * @return 用户权限信息
     */
    @GetMapping("/users/{userId}/authorities")
    Result<UserAuthoritiesDTO> getUserAuthorities(@PathVariable Long userId);
}
```

## DTO 定义

### UserDTO

用户基本信息 DTO，包含用户ID、用户名、状态等基本信息。

**详细定义**: 参见 [data-model.md](../data-model.md#userdto用户基本信息-dto)

### UserAuthoritiesDTO

用户权限信息 DTO，包含用户角色列表和权限列表。

**详细定义**: 参见 [data-model.md](../data-model.md#userauthoritiesdto用户权限信息-dto)

## 枚举定义

### UserStatus

用户状态枚举，包含 ACTIVE、INACTIVE、LOCKED、DELETED 等状态。

**详细定义**: 参见 [data-model.md](../data-model.md#userstatus用户状态枚举)

## 版本管理

**当前版本**: v1

**包结构**: `com.atlas.system.api.v1.*`

**版本策略**:
- 使用包名版本管理
- 当前版本为 v1，后续破坏性变更通过 v2 实现
- 同一版本内的变更必须保持向后兼容

## 兼容性规则

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

## 错误处理

### 错误响应格式

所有接口使用统一的 `Result<T>` 响应格式：

```json
{
  "code": "错误码",
  "message": "错误消息",
  "data": null,
  "timestamp": 1704067200000,
  "traceId": "abc123def456"
}
```

### 常见错误码

- `000000`: 操作成功
- `052001`: 数据不存在（用户不存在）
- `051000`: 参数错误
- `050001`: 服务不可用

**详细错误码**: 参见 `atlas-common-feature-core` 模块的 `CommonErrorCode` 常量

## 使用示例

### Auth 服务调用示例

```java
@Service
public class AuthService {
    
    @Autowired
    private UserQueryApi userQueryApi;
    
    @Autowired
    private PermissionQueryApi permissionQueryApi;
    
    public void authenticate(String username, String password) {
        // 查询用户信息
        Result<UserDTO> userResult = userQueryApi.getUserByUsername(username);
        if (!userResult.isSuccess()) {
            throw new AuthenticationException("用户不存在");
        }
        
        UserDTO user = userResult.getData();
        // 验证密码...
        
        // 查询用户权限
        Result<UserAuthoritiesDTO> authoritiesResult = 
            permissionQueryApi.getUserAuthorities(user.getUserId());
        if (authoritiesResult.isSuccess()) {
            UserAuthoritiesDTO authorities = authoritiesResult.getData();
            // 使用权限信息进行授权...
        }
    }
}
```

## 注意事项

1. **服务发现**: Feign 客户端需要通过服务注册中心（Nacos）发现 `atlas-system` 服务
2. **负载均衡**: Feign 客户端自动使用 Spring Cloud LoadBalancer 进行负载均衡
3. **链路追踪**: TraceId 会自动通过 `TraceIdFeignInterceptor` 传递到下游服务
4. **异常处理**: 接口调用失败时，Feign 会抛出异常，需要客户端进行异常处理
