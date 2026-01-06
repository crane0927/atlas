# 技术调研文档

## 调研目标

本文档记录 `atlas-system-api` 模块实现过程中的技术决策和调研结果。

## 技术决策

### 决策 1: Feign 接口定义方式

**决策**: 使用 Spring Cloud OpenFeign 的 `@FeignClient` 注解定义服务间调用接口。

**理由**:
- Spring Cloud OpenFeign 是 Spring Cloud 官方推荐的服务间调用方案
- 与 Spring Boot 深度集成，配置简单，维护成本低
- 支持负载均衡、熔断降级等微服务特性
- 符合项目宪法要求（优先使用 Spring Cloud 生态组件）

**实现方式**:
```java
@FeignClient(name = "atlas-system", path = "/api/v1")
public interface UserQueryApi {
    @GetMapping("/users/{userId}")
    Result<UserDTO> getUserById(@PathVariable Long userId);
    
    @GetMapping("/users/by-username")
    Result<UserDTO> getUserByUsername(@RequestParam String username);
}
```

**替代方案**:
- RestTemplate/OkHttp: 不符合项目宪法要求（禁止业务服务间直接使用原生 HTTP 客户端）
- gRPC: 需要额外的协议支持，增加复杂度，当前阶段不采用

### 决策 2: 版本管理策略

**决策**: 使用包名版本管理（`com.atlas.system.api.v1.*`），而非路径版本（`/api/v1/*`）。

**理由**:
- 包名版本管理更清晰，便于代码组织和维护
- 同一服务内可以同时存在多个版本（v1、v2），便于平滑迁移
- 符合项目宪法中的接口兼容性规则要求
- 便于 IDE 代码导航和重构

**实现方式**:
```
com.atlas.system.api.v1.feign    # v1 版本的 Feign 接口
com.atlas.system.api.v1.dto     # v1 版本的 DTO
com.atlas.system.api.v1.enums   # v1 版本的枚举
```

**替代方案**:
- 路径版本（`/api/v1/users`、`/api/v2/users`）: 需要修改接口路径，可能影响现有客户端，不采用

### 决策 3: DTO 向后兼容性设计

**决策**: DTO 新增字段必须可空或提供默认值，确保向后兼容。

**理由**:
- 符合项目宪法中的接口兼容性规则
- 确保旧版本客户端仍能正常使用，避免破坏性变更
- 支持平滑升级，减少客户端迁移成本

**实现方式**:
```java
@Data
public class UserDTO {
    private Long userId;           // 必填字段
    private String username;        // 必填字段
    private String nickname;       // 可空字段（向后兼容）
    private String email;          // 可空字段（向后兼容）
    private String phone;          // 可空字段（向后兼容）
    private UserStatus status;     // 必填字段
    private String avatar;         // 可空字段（向后兼容）
}
```

**替代方案**:
- 所有字段必填: 会导致向后兼容性问题，不采用
- 使用 `@Nullable` 注解: 可以明确标识可空字段，但需要额外依赖，当前阶段不采用

### 决策 4: 枚举序列化方式

**决策**: 使用 Jackson 默认的枚举序列化方式（枚举名称），而非自定义序列化器。

**理由**:
- Jackson 默认序列化方式简单直观，符合 RESTful API 设计规范
- 枚举名称作为字符串返回，便于客户端解析
- 减少代码复杂度，降低维护成本

**实现方式**:
```java
public enum UserStatus {
    ACTIVE("激活"),
    INACTIVE("未激活"),
    LOCKED("锁定"),
    DELETED("已删除");
    
    private final String description;
    
    UserStatus(String description) {
        this.description = description;
    }
}
```

**序列化结果**:
```json
{
  "status": "ACTIVE"
}
```

**替代方案**:
- 自定义序列化器: 可以返回中文描述，但增加复杂度，当前阶段不采用
- 使用数字编码: 不直观，不便于调试，不采用

### 决策 5: 依赖管理策略

**决策**: 模块仅依赖 `atlas-common-feature-core` 和 Spring Cloud OpenFeign，不引入 web/db/redis 等实现层依赖。

**理由**:
- API 模块仅定义接口契约，不包含业务逻辑实现
- 减少依赖复杂度，降低模块耦合度
- 符合项目宪法中的模块职责边界要求

**允许的依赖**:
- `atlas-common-feature-core`: 用于 Result、错误码、异常体系
- Spring Cloud OpenFeign: 用于 Feign 接口定义
- Lombok: 用于简化代码（可选）
- Jackson: 用于 JSON 序列化（Spring Boot 自带）

**禁止的依赖**:
- `atlas-common-infra-web`: Web 实现层
- `atlas-common-infra-db`: 数据库实现层
- `atlas-common-infra-redis`: Redis 实现层
- Spring Web: Web 实现层
- MyBatis-Plus: 数据库实现层

## 最佳实践

### Feign 接口设计最佳实践

1. **接口命名**: 使用清晰的接口名称，如 `UserQueryApi`、`PermissionQueryApi`
2. **路径设计**: 遵循 RESTful 设计规范，使用资源路径和 HTTP 方法
3. **参数传递**: 使用 `@PathVariable` 和 `@RequestParam` 注解明确参数类型
4. **返回类型**: 统一使用 `Result<T>` 格式，便于错误处理
5. **异常处理**: 通过 `Result` 的错误码和消息传递异常信息

### DTO 设计最佳实践

1. **字段命名**: 使用驼峰命名，与 Java 规范一致
2. **字段类型**: 使用明确的类型，避免使用 `Object` 等泛型类型
3. **字段注释**: 所有字段必须包含完整的中文注释
4. **向后兼容**: 新增字段必须可空或提供默认值
5. **序列化**: 使用 Jackson 注解控制序列化行为（如需要）

### 版本管理最佳实践

1. **版本命名**: 使用语义化版本（v1、v2），而非日期版本
2. **版本隔离**: 不同版本的代码完全隔离，避免相互影响
3. **迁移策略**: 提供清晰的迁移指南，支持平滑升级
4. **文档维护**: 每个版本维护独立的接口文档

## 参考资料

- [Spring Cloud OpenFeign 官方文档](https://spring.io/projects/spring-cloud-openfeign)
- [项目宪法 - 接口兼容性规则](.specify/memory/constitution.md)
- [项目宪法 - 模块职责边界](.specify/memory/constitution.md)

