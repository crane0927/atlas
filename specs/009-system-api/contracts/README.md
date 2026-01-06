# API 契约定义

## 概述

本文档定义了 `atlas-system-api` 模块的 API 接口契约，包括用户查询和权限查询接口。

## 接口列表

### 用户查询接口

**接口名称**: `UserQueryApi`

**包名**: `com.atlas.system.api.v1.feign`

**接口方法**:
- `getUserById(Long userId)` - 根据用户ID查询用户信息
- `getUserByUsername(String username)` - 根据用户名查询用户信息

### 权限查询接口

**接口名称**: `PermissionQueryApi`

**包名**: `com.atlas.system.api.v1.feign`

**接口方法**:
- `getUserRoles(Long userId)` - 查询用户角色列表
- `getUserPermissions(Long userId)` - 查询用户权限列表
- `getUserAuthorities(Long userId)` - 查询用户完整权限信息（角色+权限）

## DTO 定义

### UserDTO

用户基本信息 DTO，包含用户ID、用户名、状态等基本信息。

### UserAuthoritiesDTO

用户权限信息 DTO，包含用户角色列表和权限列表。

## 枚举定义

### UserStatus

用户状态枚举，包含 ACTIVE、INACTIVE、LOCKED、DELETED 等状态。

## 版本管理

当前版本：v1

包结构：`com.atlas.system.api.v1.*`

## 兼容性规则

- 不允许破坏性变更（字段删除、字段改名、字段语义改变）
- DTO 新增字段必须向后兼容（可空或提供默认值）
- 破坏性变更必须通过新版本包（v2）实现

