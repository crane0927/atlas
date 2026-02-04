# 数据模型：通用分页 DTO 与 atlas-system 分页查询

## 概述

本功能不新增数据库实体，仅涉及请求/响应 DTO、VO 及与现有实体的映射关系。

## 通用分页请求（可复用）

### PageQueryDTO（或等价规范）

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | Integer | 否 | 1 | 当前页码，从 1 开始 |
| size | Integer | 否 | 10 | 每页条数，建议上限 100 |
| sort | String | 否 | - | 排序，格式如 `createTime,desc` 或 `username,asc` |

- **放置位置**：`atlas-common-feature-core` 的 `page` 包（与 `PageResult` 同包）。
- **校验**：page ≥ 1，size 在 1～合理上限之间；sort 由各业务层按白名单解析。

## 业务查询 DTO（atlas-system）

### UserQueryDTO（用户分页查询条件）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 否 | 用户名模糊 |
| status | String | 否 | 状态（如 ACTIVE） |
| （分页） | - | - | 与 PageQueryDTO 一致：page, size, sort |

- **放置位置**：`com.atlas.system.user.model.dto`。
- **用途**：用户列表分页接口的查询条件；可与 page、size、sort 一起从 QueryParam 或请求体绑定。

### RoleQueryDTO（角色分页查询条件）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| roleCode | String | 否 | 角色编码模糊 |
| roleName | String | 否 | 角色名称模糊 |
| status | String | 否 | 状态 |
| （分页） | - | - | page, size, sort |

- **放置位置**：`com.atlas.system.role.model.dto`。

### PermissionQueryDTO（权限分页查询条件）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| permissionCode | String | 否 | 权限编码模糊 |
| permissionName | String | 否 | 权限名称模糊 |
| status | String | 否 | 状态 |
| （分页） | - | - | page, size, sort |

- **放置位置**：`com.atlas.system.permission.model.dto`。

### SystemSettingQueryDTO（已有）

- 已存在，包含 type、keyword。
- 对齐：分页参数（page、size、sort）与通用规范一致；仅保证请求语义统一，DTO 可继续仅承载业务条件，分页由 Controller 的 RequestParam 提供。

## 响应模型

- **分页响应**：统一使用现有 `PageResult<T>`（`atlas-common-feature-core.page`）。
- **列表元素类型**：
  - 用户：VO 或已有 UserDTO（列表项可排除敏感字段）。
  - 角色：RoleVO 或 RoleDTO。
  - 权限：PermissionVO 或 PermissionDTO。
  - 系统设置：已有 `SystemSettingVO`。

## 实体与 DTO 映射关系

| 实体 | 查询 DTO | 列表项类型（VO/DTO） |
|------|----------|----------------------|
| User | UserQueryDTO + page/size/sort | UserVO 或 UserDTO |
| Role | RoleQueryDTO + page/size/sort | RoleVO 或 RoleDTO |
| Permission | PermissionQueryDTO + page/size/sort | PermissionVO 或 PermissionDTO |
| SystemSetting | SystemSettingQueryDTO + page/size/sort | SystemSettingVO |

## 状态与校验

- 无状态机；查询为无状态接口。
- PageQueryDTO：page ≥ 1，size 在 1～上限；sort 由后端白名单校验。
