# API 契约：通用分页与 atlas-system 分页查询接口

## 通用分页参数（各接口共用）

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | integer | 否 | 1 | 当前页码，从 1 开始 |
| size | integer | 否 | 10 | 每页条数 |
| sort | string | 否 | - | 排序，格式：`字段名,asc` 或 `字段名,desc` |

## 接口列表

### 1. 用户分页列表

- **方法**：GET
- **路径**：`/api/v1/users`（或与现有路由约定一致，如 `/api/v1/users/page`）
- **Query 参数**：page, size, sort；可选：username（模糊）, status
- **响应**：`Result<PageResult<UserVO>>`（或 `Result<PageResult<UserDTO>>`）
- **说明**：列表项不包含密码等敏感字段

### 2. 角色分页列表

- **方法**：GET
- **路径**：`/api/v1/roles`
- **Query 参数**：page, size, sort；可选：roleCode, roleName, status
- **响应**：`Result<PageResult<RoleVO>>`（或 `Result<PageResult<RoleDTO>>`）

### 3. 权限分页列表

- **方法**：GET
- **路径**：`/api/v1/permissions`
- **Query 参数**：page, size, sort；可选：permissionCode, permissionName, status
- **响应**：`Result<PageResult<PermissionVO>>`（或 `Result<PageResult<PermissionDTO>>`）

### 4. 系统设置分页列表（对齐通用规范）

- **方法**：GET
- **路径**：`/api/v1/system-settings/page`（保留现有路径）
- **Query 参数**：page, size, sort（与通用规范一致）；可选：type, keyword（现有 SystemSettingQueryDTO）
- **响应**：`Result<PageResult<SystemSettingVO>>`

## 统一分页响应结构（PageResult）

| 字段 | 类型 | 说明 |
|------|------|------|
| list | array | 当前页数据列表 |
| total | long | 总记录数 |
| page | integer | 当前页码 |
| size | integer | 每页大小 |
| pages | integer | 总页数 |
| traceId | string | 可选，链路追踪 ID |

## 错误与校验

- 参数非法（如 page&lt;1、size 超上限）：返回 400，message 说明原因。
- 排序字段不在白名单：忽略或返回 400，由实现统一约定。
