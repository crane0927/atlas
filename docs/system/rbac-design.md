# RBAC 权限体系设计文档（Atlas System）

> 面向当前 `atlas-system` 服务的实现现状，梳理 RBAC（Role-Based Access Control）数据模型、接口、鉴权链路与菜单权限策略。

## 1. 目标与范围

**目标**：
- 统一描述用户、角色、权限、菜单之间的关系与约束
- 明确 API 结构与前端对接方式
- 界定权限校验的入口与数据流

**范围**：
- `atlas-system`：用户、角色、权限、菜单管理
- `atlas-auth`：登录、Token、用户权限下发
- `atlas-gateway`：请求鉴权、权限上下文透传
- 公共模块：`atlas-common-feature-security` / `atlas-common-infra-web`

## 2. 术语

- **用户（User）**：系统登录主体
- **角色（Role）**：权限集合的载体
- **权限（Permission）**：具体操作或资源的访问许可
- **菜单（Menu）**：前端路由/按钮权限控制项
- **RBAC**：用户 → 角色 → 权限 的授权模式

## 3. 数据模型

### 3.1 核心表

#### `sys_user`
- 用户实体，存储账号、状态等基础信息

#### `sys_role`
- 角色实体，字段：`roleCode`、`roleName`、`status` 等

#### `sys_permission`
- 权限实体，字段：`permissionCode`、`permissionName`、`status` 等

#### `sys_user_role`
- 用户与角色关联表（多对多）

#### `sys_role_permission`
- 角色与权限关联表（多对多）

#### `sys_menu`
- 菜单与按钮定义，含权限控制字段 `permissionCode`

### 3.2 实体关系

```
User (sys_user)
  1..*  <->  sys_user_role  <->  1..* Role (sys_role)
Role (sys_role)
  1..*  <->  sys_role_permission  <->  1..* Permission (sys_permission)
Menu (sys_menu)
  permission_code -> Permission.permission_code (逻辑关联)
```

### 3.3 关系说明

- 用户通过 **用户-角色** 关联拥有多个角色
- 角色通过 **角色-权限** 关联拥有多个权限
- 菜单通过 `permissionCode` 逻辑关联权限（非外键）

## 4. 权限与菜单策略

### 4.1 权限定义
- 权限码：`permissionCode`（建议格式如 `user:read`、`role:assign`）
- 权限是最小授权粒度

### 4.2 角色定义
- 角色聚合多个权限
- 用户通过角色继承权限

### 4.3 菜单权限
- `sys_menu.permission_code` 为空：**所有登录用户可见**
- 否则：用户权限集合包含该 `permissionCode` 才可见
- `type=BUTTON`：用于按钮级权限控制，不渲染页面路由

## 5. 鉴权链路与上下文

### 5.1 Token 与登录
- 登录由 `atlas-auth` 提供：`/api/v1/auth/login`
- 返回 `token`（Bearer）

### 5.2 Gateway 校验
- Gateway 校验 Token：
  - `jwt` 或 `introspection` 方式
- 校验通过后通过请求头透传用户信息（如 `X-User-*`）

### 5.3 下游服务上下文
- `atlas-common-infra-web` 的 `SecurityContextFilter`
- 从请求头或 Token 构建 `LoginUser`，放入 `SecurityContextHolder`
- 业务服务通过 `SecurityContextHolder.getLoginUser()` 获取权限集合

## 6. API 设计（当前已实现）

> 统一返回 `Result<T>`，分页使用 `Result<PageResult<T>>`

### 6.1 用户
- `GET /api/v1/users` 分页查询
- `GET /api/v1/users/{userId}` 查询详情
- `POST /api/v1/users` 创建用户
- `POST /api/v1/users/{userId}/roles` 关联角色

### 6.2 角色
- `GET /api/v1/roles` 分页查询
- `POST /api/v1/roles` 创建角色
- `POST /api/v1/roles/{roleId}/permissions` 关联权限

### 6.3 权限
- `GET /api/v1/permissions` 分页查询
- `POST /api/v1/permissions` 创建权限
- `GET /api/v1/users/{userId}/permissions` 查询用户权限
- `GET /api/v1/users/{userId}/roles` 查询用户角色
- `GET /api/v1/users/{userId}/authorities` 查询用户角色 + 权限

### 6.4 菜单
- `GET /api/v1/menus/tree` 菜单全量树
- `GET /api/v1/menus/me` 当前用户菜单树
- `POST /api/v1/menus` 创建菜单
- `PUT /api/v1/menus/{menuId}` 更新菜单
- `DELETE /api/v1/menus/{menuId}` 删除菜单（逻辑删除）

## 7. 错误码约定（SystemErrorCode）

- 用户：`032001` 用户不存在
- 角色：`032101` 角色不存在
- 权限：`032201` 权限不存在
- 菜单（新增）：
  - `032401` 菜单不存在
  - `032402` 父菜单不存在
  - `032403` 父菜单非法
  - `032404` 菜单类型不合法
  - `032405` 目录/菜单必须填写路由路径
  - `032406` 菜单必须填写组件标识
  - `032407` 权限码不存在

## 8. 分页与排序规范

- 分页参数：`page`、`size`，默认 `page=1`，`size=10`，最大 100
- 排序参数：`sort=field,asc|desc`
- Sort 白名单由 Service 层控制（`SortHelper`）

## 9. 数据一致性与约束

- `sys_menu.permission_code` 不做外键约束，通过服务层校验是否存在
- 删除菜单为逻辑删除（`status=DELETED` + `deleted=1`）
- 删除角色/权限时需确保关联数据一致性（当前依赖应用层约束）

## 10. 建议的权限命名规范

- 资源:动作 形式，例如：
  - `user:read`
  - `user:create`
  - `role:assign`
  - `menu:manage`

## 11. 与前端对接要点

- 前端根据 `permissions` 数组控制按钮/菜单显示
- 菜单树由 `/api/v1/menus/me` 获取
- 菜单结构自定义字段，前端需做路由适配

## 12. 演进建议

- 若需要强约束，可将菜单权限改为 `permission_id` 外键
- 若需要动态路由配置，扩展 `sys_menu` 增加 `meta` JSON 字段
- 若需要多系统支持，可增加 `app_code` 字段进行租户/系统划分
