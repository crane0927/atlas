# 数据模型说明：宪法原则 20/21 合规改造

## 概述

本需求**不新增**任何实体、DTO 或 VO，也不变更既有类的字段定义。仅调整**已有** Entity/DTO/VO 之间的**转换实现方式**以及**入参判空/默认值**的实现方式。

## 涉及的既有类型（atlas-system）

以下类型仅作为改造时的“被转换对象”或“入参对象”，其结构保持不变：

| 类型 | 包路径 | 说明 |
|------|--------|------|
| User | com.atlas.system.user.model.entity | 用户实体 |
| UserListVO | com.atlas.system.user.model.vo | 用户列表 VO |
| UserQueryDTO | com.atlas.system.user.model.dto | 用户查询 DTO（继承 PageQueryDTO） |
| Role | com.atlas.system.role.model.entity | 角色实体 |
| RoleListVO | com.atlas.system.role.model.vo | 角色列表 VO |
| RoleQueryDTO | com.atlas.system.role.model.dto | 角色查询 DTO |
| Permission | com.atlas.system.permission.model.entity | 权限实体 |
| PermissionListVO | com.atlas.system.permission.model.vo | 权限列表 VO |
| PermissionQueryDTO | com.atlas.system.permission.model.dto | 权限查询 DTO |
| SystemSetting | com.atlas.system.settings.model.entity | 系统设置实体 |
| SystemSettingVO | com.atlas.system.settings.model.vo | 系统设置 VO |
| SystemSettingQueryDTO | com.atlas.system.settings.model.dto | 系统设置查询 DTO |
| PageQueryDTO | com.atlas.common.feature.core.page | 分页查询 DTO（通用） |

## 转换关系（改造目标）

- **Entity → ListVO**：User → UserListVO、Role → RoleListVO、Permission → PermissionListVO 等，由手写 set/get 改为 BeanUtils.copyProperties 或 MapStruct。
- **Entity → VO**：SystemSetting → SystemSettingVO 等，同上。
- **Entity → DTO**：如 User → UserDTO，若存在手写转换则改为 BeanUtils/MapStruct。

## 校验与默认值（改造目标）

- **分页入参**：UserQueryDTO、RoleQueryDTO、PermissionQueryDTO、SystemSettingQueryDTO 等作为方法入参时，当前使用 `query != null ? query.getPageSafe() : 1` 的形式，改为 Optional 或 Assert（视必填/可选语义）。
- **排序/筛选**：对 sort、keyword 等可选字符串的判空，在保持可读性的前提下可改为 Optional 或 Assert.hasText。

无需变更数据库表结构或 API 契约。
