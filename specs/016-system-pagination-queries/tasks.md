# 任务清单：通用分页 DTO 与 atlas-system 分页查询接口

## 宪法合规检查清单

在开始实施前，确认以下事项：

- [x] 技术栈版本符合宪法要求（JDK 21, Spring Boot 3.5.9）
- [x] 数据库使用 PostgreSQL + MyBatis-Plus
- [x] API 设计遵循 RESTful 规范，统一使用 `Result<T>` 包装
- [x] 代码注释使用中文
- [x] DTO/VO 放在 model 包下

## 功能概述

- **通用分页**：在 common-feature-core 新增 PageQueryDTO（page、size、sort），供各分页接口复用。
- **atlas-system 分页接口**：用户、角色、权限、系统设置四类分页查询，请求参数与 PageQueryDTO 一致，响应为 `Result<PageResult<VO>>`。

| 用户场景 | 描述 | 优先级 |
|----------|------|--------|
| US1 | 用户列表分页查询 | P1 |
| US2 | 角色列表分页查询 | P1 |
| US3 | 权限列表分页查询 | P1 |
| US4 | 系统设置分页与通用规范对齐 | P1 |

---

## Phase 1: Setup（项目初始化）

**目标**：无额外环境依赖，仅确认模块可构建。

- [x] T001 确认 atlas-common-feature-core 与 atlas-system 模块可正常编译（mvn compile -pl atlas-common-feature-core,atlas-service/atlas-system）

---

## Phase 2: Foundational（通用分页 DTO）

**目标**：新增通用分页请求 DTO，供后续所有分页接口使用。

**独立测试标准**：PageQueryDTO 存在且包含 page、size、sort 字段；可被其他模块依赖。

- [x] T002 在 atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/page/PageQueryDTO.java 新增 PageQueryDTO（字段：page 默认 1，size 默认 10，sort 可选；中文注释；可选校验如 page≥1、size 上限 100）

---

## Phase 3: US1 - 用户分页接口

**用户场景**：管理端分页查看用户列表，支持条件与排序。

**独立测试标准**：GET 用户分页接口返回 Result<PageResult<XXX>>，包含 list、total、page、size、pages；支持 page、size、sort、username、status 参数。

### 实施任务

- [x] T003 [US1] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/user/model/dto/UserQueryDTO.java 新增 UserQueryDTO（username、status 可选；中文注释）
- [x] T004 [US1] 新增用户列表项 VO 或复用 DTO 并确保列表不返回密码：在 atlas-service/atlas-system/src/main/java/com/atlas/system/user/model/vo/UserListVO.java 创建 UserListVO（或约定 UserDTO 在列表场景不序列化 password），中文注释
- [x] T005 [US1] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/user/service/UserService.java 与 impl 中新增 listUsersPage(UserQueryDTO query, Integer page, Integer size, String sort)，返回 PageResult<UserListVO>（或 PageResult<UserDTO>），使用 MyBatis-Plus Page 与排序白名单（如 createTime、username）
- [x] T006 [US1] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/user/controller/UserController.java 新增 GET 分页接口（路径与现有不冲突，如 GET /api/v1/users 通过 QueryParam 区分列表；参数：UserQueryDTO + page、size、sort；返回 Result<PageResult<UserListVO>>），中文注释

---

## Phase 4: US2 - 角色分页接口

**用户场景**：管理端分页查看角色列表，支持条件与排序。

**独立测试标准**：GET 角色分页接口返回 Result<PageResult<XXX>>；支持 page、size、sort、roleCode、roleName、status。

- [x] T007 [P] [US2] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/role/model/dto/RoleQueryDTO.java 新增 RoleQueryDTO（roleCode、roleName、status 可选），中文注释
- [x] T008 [P] [US2] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/role/model/vo/RoleVO.java（或 model/dto 下角色列表 DTO）创建角色列表项 VO/DTO，中文注释
- [x] T009 [US2] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/role/service/RoleService.java 与 impl 中新增 listRolesPage(RoleQueryDTO query, Integer page, Integer size, String sort)，返回 PageResult<RoleVO>，排序白名单
- [x] T010 [US2] 在 atlas-service/atlas-system 中新增 GET /api/v1/roles 分页接口（可在 RoleManagementController 或新建 RoleController）：参数 RoleQueryDTO + page、size、sort，返回 Result<PageResult<RoleVO>>，中文注释

---

## Phase 5: US3 - 权限分页接口

**用户场景**：管理端分页查看权限列表，支持条件与排序。

**独立测试标准**：GET 权限分页接口返回 Result<PageResult<XXX>>；支持 page、size、sort、permissionCode、permissionName、status。

- [x] T011 [P] [US3] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/permission/model/dto/PermissionQueryDTO.java 新增 PermissionQueryDTO（permissionCode、permissionName、status 可选），中文注释
- [x] T012 [P] [US3] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/permission/model/vo/PermissionVO.java（或 model/dto）创建权限列表项 VO/DTO，中文注释
- [x] T013 [US3] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/permission/service/PermissionService.java 与 impl 中新增 listPermissionsPage(PermissionQueryDTO query, Integer page, Integer size, String sort)，返回 PageResult<PermissionVO>，排序白名单
- [x] T014 [US3] 在 atlas-service/atlas-system 中新增 GET /api/v1/permissions 分页接口（PermissionManagementController 或新建 PermissionController）：参数 PermissionQueryDTO + page、size、sort，返回 Result<PageResult<PermissionVO>>，中文注释

---

## Phase 6: US4 - 系统设置分页对齐

**用户场景**：系统设置分页与通用分页请求规范一致。

**独立测试标准**：GET /api/v1/system-settings/page 支持 page、size、sort 参数及默认值与 PageQueryDTO 一致；响应仍为 Result<PageResult<SystemSettingVO>>。

- [x] T015 [US4] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/settings/controller/SystemSettingController.java 的 listSystemSettingsPage 中增加 sort 参数（默认可选），并确保 page、size 默认值与 PageQueryDTO 一致（page=1，size=10），中文注释

---

## Phase 7: Polish（验证与收尾）

**目标**：确认所有分页接口使用统一语义并包含分页元数据。

- [x] T016 验证四个分页接口（用户、角色、权限、系统设置）均使用 page、size、sort 语义且响应包含 list、total、page、size、pages
- [x] T017 若需更新调用说明，则更新 specs/016-system-pagination-queries/quickstart.md 中的路径与示例

---

## 依赖关系图

```
Phase 1 (Setup)
    T001
      ↓
Phase 2 (Foundational)
    T002  PageQueryDTO
      ↓
Phase 3 (US1)     Phase 4 (US2)     Phase 5 (US3)     Phase 6 (US4)
T003→T004→T005→T006   T007→T008→T009→T010   T011→T012→T013→T014   T015
      ↓                    ↓                    ↓                    ↓
                    Phase 7 (Polish)
                         T016 → T017
```

说明：US1～US4 在 T002 完成后可并行开发；各用户场景内部任务按顺序执行。

---

## 并行执行示例

```text
# 第一批
T001 → T002

# 第二批（US1～US4 可并行）
US1: T003 → T004 → T005 → T006
US2: T007 → T008 → T009 → T010
US3: T011 → T012 → T013 → T014
US4: T015

# 第三批
T016 → T017
```

---

## 实施策略

### MVP 范围

**建议 MVP**：Phase 2 + US1（T002 + T003～T006），交付通用分页 DTO 与用户分页接口。

### 增量交付

| 迭代 | 范围 | 交付物 |
|------|------|--------|
| 1 | Foundational + US1 | PageQueryDTO、用户分页接口 |
| 2 | US2 + US3 | 角色、权限分页接口 |
| 3 | US4 + Polish | 系统设置对齐、验证与文档 |

---

## 任务统计

| 指标 | 数值 |
|------|------|
| 总任务数 | 17 |
| Phase 1 Setup | 1 |
| Phase 2 Foundational | 1 |
| US1 | 4 |
| US2 | 4 |
| US3 | 4 |
| US4 | 1 |
| Polish | 2 |
| 可并行任务 | T007、T008、T011、T012 等（不同文件、无依赖） |

---

## 验收清单

- [x] PageQueryDTO 已定义于 common-feature-core/page，且被至少两个分页接口使用
- [x] 用户列表分页接口已实现，支持分页与可选条件/排序
- [x] 角色列表分页接口已实现，支持分页与可选条件/排序
- [x] 权限列表分页接口已实现，支持分页与可选条件/排序
- [x] 系统设置分页接口与通用规范对齐（page、size、sort）
- [x] 所有分页接口响应均包含 list、total、page、size、pages

---

## 相关文档

- [功能规格说明](./spec.md)
- [实施计划](./plan.md)
- [数据模型](./data-model.md)
- [API 契约](./contracts/README.md)
- [快速开始](./quickstart.md)
