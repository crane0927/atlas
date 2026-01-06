# 数据模型定义

## 概述

本文档定义了 `atlas-system-api` 模块中使用的所有数据模型，包括 DTO 对象和枚举常量。

## DTO 对象

### UserDTO（用户基本信息 DTO）

**包名**: `com.atlas.system.api.v1.dto`

**描述**: 用户基本信息数据传输对象，用于用户查询接口的响应。

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 | 示例 |
|--------|------|------|------|--------|------|
| userId | Long | 用户ID | 是 | - | 1 |
| username | String | 用户名 | 是 | - | "admin" |
| nickname | String | 昵称 | 否 | null | "管理员" |
| email | String | 邮箱 | 否 | null | "admin@example.com" |
| phone | String | 手机号 | 否 | null | "13800138000" |
| status | UserStatus | 用户状态 | 是 | - | UserStatus.ACTIVE |
| avatar | String | 头像URL | 否 | null | "https://example.com/avatar.jpg" |

**约束规则**:
- `userId` 不能为 null
- `username` 不能为 null 或空字符串
- `status` 不能为 null
- 其他字段可以为 null（向后兼容）

**序列化规则**:
- 使用 Jackson 进行 JSON 序列化/反序列化
- 枚举字段 `status` 序列化为字符串（枚举名称）

**向后兼容性**:
- 新增字段必须可空或提供默认值
- 不允许删除或修改现有字段
- 不允许修改字段类型或语义

### UserAuthoritiesDTO（用户权限信息 DTO）

**包名**: `com.atlas.system.api.v1.dto`

**描述**: 用户权限信息数据传输对象，包含用户角色和权限列表。

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 | 示例 |
|--------|------|------|------|--------|------|
| userId | Long | 用户ID | 是 | - | 1 |
| roles | List<String> | 角色列表 | 是 | [] | ["ADMIN", "USER"] |
| permissions | List<String> | 权限列表 | 是 | [] | ["user:read", "user:write"] |

**约束规则**:
- `userId` 不能为 null
- `roles` 不能为 null（可以为空列表）
- `permissions` 不能为 null（可以为空列表）

**序列化规则**:
- 使用 Jackson 进行 JSON 序列化/反序列化
- 列表字段序列化为 JSON 数组

**向后兼容性**:
- 新增字段必须可空或提供默认值
- 不允许删除或修改现有字段
- 不允许修改字段类型或语义

## 枚举常量

### UserStatus（用户状态枚举）

**包名**: `com.atlas.system.api.v1.enums`

**描述**: 用户状态枚举，定义用户的各种状态。

**枚举值**:

| 枚举值 | 说明 | 序列化值 |
|--------|------|----------|
| ACTIVE | 激活状态 | "ACTIVE" |
| INACTIVE | 未激活状态 | "INACTIVE" |
| LOCKED | 锁定状态 | "LOCKED" |
| DELETED | 已删除状态 | "DELETED" |

**序列化规则**:
- 使用 Jackson 默认枚举序列化方式（枚举名称）
- 序列化结果为字符串，如 `"ACTIVE"`

**向后兼容性**:
- 不允许删除现有枚举值
- 新增枚举值必须向后兼容（不影响现有客户端）

## 数据关系

### UserDTO 与 UserStatus 关系

- `UserDTO.status` 字段类型为 `UserStatus` 枚举
- 一个用户只能有一个状态
- 状态值必须来自 `UserStatus` 枚举定义的值

### UserAuthoritiesDTO 与 UserDTO 关系

- `UserAuthoritiesDTO.userId` 对应 `UserDTO.userId`
- 一个用户可以有多个角色和权限
- 角色和权限以字符串列表形式存储

## 数据验证

### 字段验证规则

1. **必填字段验证**:
   - `UserDTO.userId`: 必须大于 0
   - `UserDTO.username`: 不能为 null 或空字符串，长度不超过 50
   - `UserDTO.status`: 必须为有效的 `UserStatus` 枚举值
   - `UserAuthoritiesDTO.userId`: 必须大于 0

2. **可选字段验证**:
   - `UserDTO.email`: 如果提供，必须符合邮箱格式
   - `UserDTO.phone`: 如果提供，必须符合手机号格式（11位数字）
   - `UserDTO.avatar`: 如果提供，必须为有效的 URL

3. **列表字段验证**:
   - `UserAuthoritiesDTO.roles`: 列表中的每个元素不能为 null 或空字符串
   - `UserAuthoritiesDTO.permissions`: 列表中的每个元素不能为 null 或空字符串

## 示例数据

### UserDTO 示例

```json
{
  "userId": 1,
  "username": "admin",
  "nickname": "管理员",
  "email": "admin@example.com",
  "phone": "13800138000",
  "status": "ACTIVE",
  "avatar": "https://example.com/avatar.jpg"
}
```

### UserAuthoritiesDTO 示例

```json
{
  "userId": 1,
  "roles": ["ADMIN", "USER"],
  "permissions": ["user:read", "user:write", "user:delete"]
}
```

### UserStatus 示例

```json
"ACTIVE"
```

