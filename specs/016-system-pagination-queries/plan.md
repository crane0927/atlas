# 功能规划文档：通用分页 DTO 与 atlas-system 分页查询接口

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 3.5.9, Spring Cloud 2025.0.1, Spring Cloud Alibaba 2025.0.0.0
- ✅ **数据库**: PostgreSQL + MyBatis-Plus
- ✅ **API 设计**: 遵循 RESTful 风格，统一使用 `Result<T>` 包装，分页使用 page、size、sort 参数
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 通用分页 DTO 放在 common-feature-core，供多模块复用
- ✅ **模块化**: 遵循分层架构；DTO/VO 放在各模块 model.dto / model.vo
- ✅ **实体继承**: 数据库实体必须继承 BaseEntity（本需求不新增实体）
- ✅ **单元测试**: 非必要不生成，仅在明确要求时编写

## 功能概述

1. **通用分页请求**：在 `atlas-common-feature-core` 的 `page` 包下新增通用分页请求 DTO（page、size、sort），与现有 `PageResult` 对称，供 atlas-system 及后续模块复用。
2. **atlas-system 分页接口**：为用户、角色、权限、系统设置四类业务提供分页查询接口，请求参数与通用规范一致，响应统一为 `Result<PageResult<VO>>`；系统设置已有分页接口则对齐请求/响应规范。

## 技术方案

### 架构设计

```
atlas-common-feature-core
  └── page/
        ├── PageResult.java        （已有）
        └── PageQueryDTO.java     （新增：page, size, sort）

atlas-service/atlas-system
  ├── user/
  │     ├── model.dto.UserQueryDTO      （新增或扩充分页相关语义）
  │     ├── model.vo.UserVO             （列表项，若无则新增或复用 DTO）
  │     ├── service.UserService         （新增分页方法）
  │     └── controller.UserController  （新增 GET 分页接口）
  ├── role/
  │     ├── model.dto.RoleQueryDTO
  │     ├── model.vo.RoleVO / DTO
  │     ├── service.RoleService
  │     └── controller（新增 GET 分页接口，或扩展现有）
  ├── permission/
  │     ├── model.dto.PermissionQueryDTO
  │     ├── model.vo.PermissionVO / DTO
  │     ├── service.PermissionService
  │     └── controller（新增 GET 分页接口）
  └── settings/
        └── 现有分页接口对齐 PageQueryDTO 语义（page/size/sort）
```

### 技术选型

| 组件 | 选型 | 说明 |
|------|------|------|
| 分页响应 | 现有 PageResult（common-feature-core） | 已满足 list/total/page/size/pages |
| 分页请求 | 新增 PageQueryDTO（同包） | page, size, sort |
| 持久层 | MyBatis-Plus Page、QueryWrapper | 与现有 SystemSetting 分页实现一致 |
| 排序 | sort 参数解析 + 白名单字段 | 防止注入与无效排序 |

### 实施计划

#### 阶段 1：通用分页 DTO

- **目标**：在 common-feature-core 中新增通用分页请求 DTO，并做简单校验（如 page≥1，size 上限）。
- **任务**：新增 `PageQueryDTO`（page、size、sort）；可选工具方法（如解析 sort 为字段+方向）。文档与注释说明与 PageResult 的对应关系。

#### 阶段 2：用户分页接口

- **目标**：提供用户列表分页查询接口。
- **任务**：新增或扩展 `UserQueryDTO`（业务条件）；新增 Service 分页方法；新增 Controller GET 接口；返回 `Result<PageResult<UserVO>>`（或 DTO）；排序字段白名单（如 createTime、username）。

#### 阶段 3：角色分页接口

- **目标**：提供角色列表分页查询接口。
- **任务**：新增 `RoleQueryDTO`；RoleService 分页方法；Controller GET /api/v1/roles；返回 `Result<PageResult<RoleVO>>`（或 DTO）；排序白名单。

#### 阶段 4：权限分页接口

- **目标**：提供权限列表分页查询接口。
- **任务**：新增 `PermissionQueryDTO`；PermissionService 分页方法；Controller GET /api/v1/permissions；返回 `Result<PageResult<PermissionVO>>`（或 DTO）；排序白名单。

#### 阶段 5：系统设置分页对齐

- **目标**：系统设置分页请求与通用规范一致。
- **任务**：在 SystemSettingController 分页接口中统一使用与 PageQueryDTO 一致的 page、size、sort 参数及默认值；响应已为 PageResult，无需改动；必要时在文档中说明与通用 DTO 的对应关系。

## 风险评估

| 风险 | 影响 | 应对 |
|------|------|------|
| 与现有 User/Role/Permission 单条查询路径冲突 | 中 | 分页使用独立路径（如 /users 返回分页，/users/{id} 保留）或统一约定 QueryParam 区分 |
| 排序字段注入 | 高 | 仅允许白名单字段排序，不拼接未校验字段 |
| 系统设置现有调用方依赖 /page 与参数 | 低 | 保留路径与兼容参数，仅补充 sort 与默认值对齐 |

## 验收标准

- 通用分页请求 DTO 已存在于 common-feature-core，且被至少两个分页接口使用。
- 用户、角色、权限、系统设置均有分页查询接口，且响应均包含 list、total、page、size、pages。
- 所有分页接口的请求参数（page、size、sort）语义与 PageQueryDTO 一致。
- 系统设置分页接口与通用规范对齐（参数与默认值一致）。

## 相关文档

- [功能规格说明](./spec.md)
- [研究文档](./research.md)
- [数据模型](./data-model.md)
- [API 契约](./contracts/README.md)
- [快速开始](./quickstart.md)
