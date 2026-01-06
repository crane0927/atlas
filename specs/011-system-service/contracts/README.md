# API 合约文档

## 概述

本文档说明 `atlas-system` 服务实现的 API 接口合约。所有接口合约定义在 `atlas-system-api` 模块中，本服务负责实现这些接口。

## 接口实现

### 用户查询接口

**接口定义**: `com.atlas.system.api.v1.feign.UserQueryApi`

**实现类**: `com.atlas.system.user.controller.UserController`

**接口列表**:

| 方法 | 路径 | 描述 | 请求参数 | 响应体 |
|------|------|------|----------|--------|
| GET | `/api/v1/users/{userId}` | 根据用户ID查询用户信息 | `@PathVariable Long userId` | `Result<UserDTO>` |
| GET | `/api/v1/users/by-username` | 根据用户名查询用户信息 | `@RequestParam String username` | `Result<UserDTO>` |

**实现要求**:
- Controller 必须实现 `UserQueryApi` 接口
- 接口路径必须与 API 定义一致
- 响应格式必须使用 `Result<UserDTO>`
- 用户不存在时返回错误码 `032001`

### 权限查询接口

**接口定义**: `com.atlas.system.api.v1.feign.PermissionQueryApi`

**实现类**: `com.atlas.system.permission.controller.PermissionController`

**接口列表**:

| 方法 | 路径 | 描述 | 请求参数 | 响应体 |
|------|------|------|----------|--------|
| GET | `/api/v1/users/{userId}/roles` | 查询用户角色列表 | `@PathVariable Long userId` | `Result<List<String>>` |
| GET | `/api/v1/users/{userId}/permissions` | 查询用户权限列表 | `@PathVariable Long userId` | `Result<List<String>>` |
| GET | `/api/v1/users/{userId}/authorities` | 查询用户完整权限信息 | `@PathVariable Long userId` | `Result<UserAuthoritiesDTO>` |

**实现要求**:
- Controller 必须实现 `PermissionQueryApi` 接口
- 接口路径必须与 API 定义一致
- 响应格式必须使用 `Result<List<String>>` 或 `Result<UserAuthoritiesDTO>`
- 用户不存在时返回空列表或错误码

## 管理接口（最小闭环）

除了查询接口，还需要实现以下管理接口以支持最小闭环：

### 用户管理接口

**实现类**: `com.atlas.system.user.controller.UserManagementController`

**接口列表**:

| 方法 | 路径 | 描述 | 请求体 | 响应体 |
|------|------|------|--------|--------|
| POST | `/api/v1/users` | 创建用户 | `UserCreateDTO` | `Result<UserDTO>` |

### 角色管理接口

**实现类**: `com.atlas.system.role.controller.RoleManagementController`

**接口列表**:

| 方法 | 路径 | 描述 | 请求体 | 响应体 |
|------|------|------|--------|--------|
| POST | `/api/v1/roles` | 创建角色 | `RoleCreateDTO` | `Result<RoleDTO>` |

### 权限管理接口

**实现类**: `com.atlas.system.permission.controller.PermissionManagementController`

**接口列表**:

| 方法 | 路径 | 描述 | 请求体 | 响应体 |
|------|------|------|--------|--------|
| POST | `/api/v1/permissions` | 创建权限 | `PermissionCreateDTO` | `Result<PermissionDTO>` |

### 关联管理接口

**实现类**: `com.atlas.system.user.controller.UserRoleController`

**接口列表**:

| 方法 | 路径 | 描述 | 请求体 | 响应体 |
|------|------|------|--------|--------|
| POST | `/api/v1/users/{userId}/roles` | 为用户分配角色 | `UserRoleAssignDTO` | `Result<Void>` |

**实现类**: `com.atlas.system.role.controller.RolePermissionController`

**接口列表**:

| 方法 | 路径 | 描述 | 请求体 | 响应体 |
|------|------|------|--------|--------|
| POST | `/api/v1/roles/{roleId}/permissions` | 为角色分配权限 | `RolePermissionAssignDTO` | `Result<Void>` |

## 接口契约一致性

**重要原则**:
- 所有查询接口必须严格按照 `atlas-system-api` 中定义的接口实现
- 接口路径、请求参数、响应格式必须完全一致
- 管理接口（创建、关联）不在 API 模块中定义，但需要遵循 RESTful 设计规范

## 错误码规范

**系统域错误码**: 03 开头

- `032001`: 用户不存在
- `032002`: 角色不存在
- `032003`: 权限不存在
- `032004`: 用户已存在
- `032005`: 角色已存在
- `032006`: 权限已存在
- `013001`: 参数错误

## 接口测试

所有接口都需要编写测试用例：

1. **单元测试**: Service 层测试
2. **集成测试**: Controller 层测试（使用 MockMvc）
3. **API 测试**: 端到端测试

## 参考

- [atlas-system-api 规范](../../009-system-api/spec.md)
- [RESTful API 设计规范](../../../docs/engineering-standards/package-naming.md)

