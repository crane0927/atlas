# 数据模型文档

## 概述

本文档定义 `atlas-system` 服务的数据模型，包括实体类、数据库表结构、字段定义、约束规则和关联关系。

## 实体关系图

```
User (用户)
  │
  ├── UserRole (用户角色关联)
  │     │
  │     └── Role (角色)
  │           │
  │           └── RolePermission (角色权限关联)
  │                 │
  │                 └── Permission (权限)
```

## 实体定义

### 1. User（用户）

**数据库表名**: `sys_user`

**实体类**: `com.atlas.system.user.model.entity.User`

**字段定义**:

| 字段名 | 类型 | 说明 | 约束 | 默认值 | 示例 |
|--------|------|------|------|--------|------|
| user_id | BIGINT | 用户ID | 主键，自增 | - | 1 |
| username | VARCHAR(50) | 用户名 | 唯一，非空 | - | "admin" |
| password | VARCHAR(255) | 密码（加密） | 非空 | - | "加密后的密码" |
| nickname | VARCHAR(100) | 昵称 | 可空 | null | "管理员" |
| email | VARCHAR(100) | 邮箱 | 可空 | null | "admin@example.com" |
| phone | VARCHAR(20) | 手机号 | 可空 | null | "13800138000" |
| status | VARCHAR(20) | 用户状态 | 非空 | 'ACTIVE' | "ACTIVE" |
| avatar | VARCHAR(500) | 头像URL | 可空 | null | "https://example.com/avatar.jpg" |
| created_at | TIMESTAMP | 创建时间 | 非空 | CURRENT_TIMESTAMP | 2025-01-06 10:00:00 |
| updated_at | TIMESTAMP | 更新时间 | 非空 | CURRENT_TIMESTAMP | 2025-01-06 10:00:00 |

**索引**:
- PRIMARY KEY (`user_id`)
- UNIQUE KEY `uk_username` (`username`)
- INDEX `idx_status` (`status`)

**状态枚举**:
- `ACTIVE`: 激活状态
- `INACTIVE`: 未激活状态
- `LOCKED`: 锁定状态
- `DELETED`: 已删除状态

**业务规则**:
- 用户名必须唯一
- 密码必须加密存储
- 状态为 `DELETED` 的用户不能登录
- 状态为 `LOCKED` 的用户不能登录

### 2. Role（角色）

**数据库表名**: `sys_role`

**实体类**: `com.atlas.system.role.model.entity.Role`

**字段定义**:

| 字段名 | 类型 | 说明 | 约束 | 默认值 | 示例 |
|--------|------|------|------|--------|------|
| role_id | BIGINT | 角色ID | 主键，自增 | - | 1 |
| role_code | VARCHAR(50) | 角色代码 | 唯一，非空 | - | "ADMIN" |
| role_name | VARCHAR(100) | 角色名称 | 非空 | - | "管理员" |
| description | VARCHAR(500) | 角色描述 | 可空 | null | "系统管理员" |
| status | VARCHAR(20) | 角色状态 | 非空 | 'ACTIVE' | "ACTIVE" |
| created_at | TIMESTAMP | 创建时间 | 非空 | CURRENT_TIMESTAMP | 2025-01-06 10:00:00 |
| updated_at | TIMESTAMP | 更新时间 | 非空 | CURRENT_TIMESTAMP | 2025-01-06 10:00:00 |

**索引**:
- PRIMARY KEY (`role_id`)
- UNIQUE KEY `uk_role_code` (`role_code`)
- INDEX `idx_status` (`status`)

**状态枚举**:
- `ACTIVE`: 激活状态
- `INACTIVE`: 未激活状态
- `DELETED`: 已删除状态

**业务规则**:
- 角色代码必须唯一
- 状态为 `DELETED` 的角色不能分配给用户

### 3. Permission（权限）

**数据库表名**: `sys_permission`

**实体类**: `com.atlas.system.permission.model.entity.Permission`

**字段定义**:

| 字段名 | 类型 | 说明 | 约束 | 默认值 | 示例 |
|--------|------|------|------|--------|------|
| permission_id | BIGINT | 权限ID | 主键，自增 | - | 1 |
| permission_code | VARCHAR(100) | 权限代码 | 唯一，非空 | - | "user:read" |
| permission_name | VARCHAR(100) | 权限名称 | 非空 | - | "用户查询" |
| description | VARCHAR(500) | 权限描述 | 可空 | null | "查询用户信息" |
| status | VARCHAR(20) | 权限状态 | 非空 | 'ACTIVE' | "ACTIVE" |
| created_at | TIMESTAMP | 创建时间 | 非空 | CURRENT_TIMESTAMP | 2025-01-06 10:00:00 |
| updated_at | TIMESTAMP | 更新时间 | 非空 | CURRENT_TIMESTAMP | 2025-01-06 10:00:00 |

**索引**:
- PRIMARY KEY (`permission_id`)
- UNIQUE KEY `uk_permission_code` (`permission_code`)
- INDEX `idx_status` (`status`)

**状态枚举**:
- `ACTIVE`: 激活状态
- `INACTIVE`: 未激活状态
- `DELETED`: 已删除状态

**业务规则**:
- 权限代码必须唯一
- 权限代码格式建议：`{resource}:{action}`（如 `user:read`、`user:write`）
- 状态为 `DELETED` 的权限不能分配给角色

### 4. UserRole（用户角色关联）

**数据库表名**: `sys_user_role`

**实体类**: `com.atlas.system.user.model.entity.UserRole`

**字段定义**:

| 字段名 | 类型 | 说明 | 约束 | 默认值 | 示例 |
|--------|------|------|------|--------|------|
| id | BIGINT | 关联ID | 主键，自增 | - | 1 |
| user_id | BIGINT | 用户ID | 外键，非空 | - | 1 |
| role_id | BIGINT | 角色ID | 外键，非空 | - | 1 |
| created_at | TIMESTAMP | 创建时间 | 非空 | CURRENT_TIMESTAMP | 2025-01-06 10:00:00 |

**索引**:
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
- INDEX `idx_user_id` (`user_id`)
- INDEX `idx_role_id` (`role_id`)
- FOREIGN KEY `fk_user_role_user` (`user_id`) REFERENCES `sys_user` (`user_id`)
- FOREIGN KEY `fk_user_role_role` (`role_id`) REFERENCES `sys_role` (`role_id`)

**业务规则**:
- 一个用户可以有多个角色
- 一个角色可以分配给多个用户
- 同一用户不能重复分配同一角色

### 5. RolePermission（角色权限关联）

**数据库表名**: `sys_role_permission`

**实体类**: `com.atlas.system.role.model.entity.RolePermission`

**字段定义**:

| 字段名 | 类型 | 说明 | 约束 | 默认值 | 示例 |
|--------|------|------|------|--------|------|
| id | BIGINT | 关联ID | 主键，自增 | - | 1 |
| role_id | BIGINT | 角色ID | 外键，非空 | - | 1 |
| permission_id | BIGINT | 权限ID | 外键，非空 | - | 1 |
| created_at | TIMESTAMP | 创建时间 | 非空 | CURRENT_TIMESTAMP | 2025-01-06 10:00:00 |

**索引**:
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
- INDEX `idx_role_id` (`role_id`)
- INDEX `idx_permission_id` (`permission_id`)
- FOREIGN KEY `fk_role_permission_role` (`role_id`) REFERENCES `sys_role` (`role_id`)
- FOREIGN KEY `fk_role_permission_permission` (`permission_id`) REFERENCES `sys_permission` (`permission_id`)

**业务规则**:
- 一个角色可以有多个权限
- 一个权限可以分配给多个角色
- 同一角色不能重复分配同一权限

## 数据关系

### 用户与角色关系

- **关系类型**: 多对多（Many-to-Many）
- **关联表**: `sys_user_role`
- **业务规则**: 
  - 一个用户可以有多个角色
  - 一个角色可以分配给多个用户
  - 通过 `sys_user_role` 表维护关联关系

### 角色与权限关系

- **关系类型**: 多对多（Many-to-Many）
- **关联表**: `sys_role_permission`
- **业务规则**: 
  - 一个角色可以有多个权限
  - 一个权限可以分配给多个角色
  - 通过 `sys_role_permission` 表维护关联关系

### 用户与权限关系（间接）

- **关系类型**: 多对多（通过角色间接关联）
- **查询方式**: 
  - 用户 -> 用户角色 -> 角色 -> 角色权限 -> 权限
  - 一个用户的所有权限 = 该用户所有角色的权限集合（去重）

## 数据验证规则

### 用户验证

1. **用户名验证**:
   - 长度：3-50 个字符
   - 格式：字母、数字、下划线
   - 唯一性：必须唯一

2. **密码验证**:
   - 长度：至少 8 个字符
   - 格式：包含字母和数字
   - 存储：必须加密存储（BCrypt）

3. **邮箱验证**:
   - 格式：符合邮箱格式规范
   - 可空：可以为空

4. **手机号验证**:
   - 格式：11 位数字
   - 可空：可以为空

### 角色验证

1. **角色代码验证**:
   - 长度：3-50 个字符
   - 格式：大写字母、下划线
   - 唯一性：必须唯一

2. **角色名称验证**:
   - 长度：1-100 个字符
   - 格式：任意字符

### 权限验证

1. **权限代码验证**:
   - 长度：3-100 个字符
   - 格式：`{resource}:{action}`（如 `user:read`）
   - 唯一性：必须唯一

2. **权限名称验证**:
   - 长度：1-100 个字符
   - 格式：任意字符

## 数据状态管理

### 用户状态流转

```
ACTIVE (激活)
  ↓
LOCKED (锁定) ← → ACTIVE (激活)
  ↓
DELETED (已删除)
```

**状态规则**:
- 新创建的用户默认为 `ACTIVE`
- `ACTIVE` 用户可以登录
- `LOCKED` 用户不能登录，需要管理员解锁
- `DELETED` 用户不能登录，逻辑删除

### 角色/权限状态流转

```
ACTIVE (激活)
  ↓
INACTIVE (未激活)
  ↓
DELETED (已删除)
```

**状态规则**:
- 新创建的角色/权限默认为 `ACTIVE`
- `ACTIVE` 的角色/权限可以正常使用
- `INACTIVE` 的角色/权限不能分配给用户/角色
- `DELETED` 的角色/权限逻辑删除

## 数据查询优化

### 索引策略

1. **主键索引**: 所有表的主键自动创建索引
2. **唯一索引**: 用户名、角色代码、权限代码创建唯一索引
3. **外键索引**: 关联表的外键字段创建索引
4. **状态索引**: 状态字段创建索引，用于状态筛选

### 查询优化建议

1. **权限查询优化**:
   - 使用 JOIN 查询，减少数据库往返次数
   - 在关联表的 `user_id`、`role_id`、`permission_id` 字段上添加索引
   - 考虑使用缓存（但需确保数据实时性）

2. **用户查询优化**:
   - 用户名查询使用唯一索引，性能最优
   - 用户ID查询使用主键索引，性能最优

## 数据迁移策略

### 初始数据

1. **默认角色**: 创建默认角色（如 `ADMIN`、`USER`）
2. **默认权限**: 创建默认权限（如 `user:read`、`user:write`）
3. **默认用户**: 创建默认管理员用户（可选）

### 数据迁移

1. **版本管理**: 使用 Flyway 管理数据库迁移脚本
2. **脚本位置**: `src/main/resources/db/migration/`
3. **命名规范**: `V{version}__{description}.sql`
4. **SQL 目录**: 在服务目录下建立 `sql/v1.0.0/` 目录，保存 SQL 脚本

## 总结

数据模型设计遵循以下原则：

1. **规范化**: 使用第三范式，避免数据冗余
2. **完整性**: 使用外键约束，确保数据完整性
3. **性能**: 合理使用索引，优化查询性能
4. **可扩展**: 预留扩展字段，便于后续扩展
5. **一致性**: 统一的字段命名和数据类型规范

