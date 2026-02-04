# 任务清单：根据宪法原则 20 和 21 调整判断逻辑与转换逻辑

## 宪法合规检查清单

在开始实施前，确认以下事项：

- [x] 技术栈版本符合宪法要求（JDK 21, Spring Boot 3.5.9, Spring Cloud 2025.0.1, Spring Cloud Alibaba 2025.0.0.0）
- [x] 数据库使用 PostgreSQL + MyBatis-Plus
- [x] API 设计遵循 RESTful 规范，统一使用 `Result<T>` 包装（本需求不新增接口）
- [x] 代码注释使用中文
- [x] Entity/DTO/VO 转换使用 BeanUtils 或 MapStruct，禁止手写逐字段赋值
- [x] 参数判空与校验使用 Assert 或 Optional，避免冗长 if-else

## 功能概述

- **原则 20**：将手写逐字段赋值的 Entity/DTO/VO 转换改为 Spring BeanUtils.copyProperties（或 MapStruct）。
- **原则 21**：将分页/查询中的 if-else 判空改为 Spring Assert 或 Optional。
- **范围**：atlas-system 内 user、role、permission、settings 的 Service 实现类；不改变对外 API 行为。

| 用户故事 | 描述 | 优先级 |
|----------|------|--------|
| US1 | 对象转换合规（原则 20） | P1 |
| US2 | 参数与空值处理合规（原则 21） | P1 |

---

## Phase 1: Setup（项目初始化）

**目标**：确认模块可构建，无新增依赖。

- [x] T001 确认 atlas-system 模块可正常编译：在仓库根目录执行 `mvn compile -pl atlas-service/atlas-system -am`，确保通过后再进行改造

---

## Phase 2: Foundational（扫描与清单）

**目标**：产出改造点位清单，便于按序实施与核对。

**独立测试标准**：存在明确清单，列出需改造的文件与方法（转换类 / 判空类）。

- [x] T002 全局检索手写转换：在仓库根目录用 grep/IDE 检索 `convertTo`、`toVO`、`toDTO` 及成段 `setXxx(getXxx())`，在 `specs/017-constitution-principles-refactor/refactor-list.md` 中列出文件路径、方法名、改造类型（转换）
- [x] T003 全局检索分页/查询判空：检索 `query != null`、`pageQuery != null`、`x == null ?` 等模式，将结果追加到 `specs/017-constitution-principles-refactor/refactor-list.md`，改造类型为（判空）

---

## Phase 3: US1 - 对象转换合规（原则 20）

**用户场景**：开发维护时转换逻辑通过 BeanUtils 自动覆盖同名字段，代码审查无手写逐字段转换。

**独立测试标准**：已改造的 Service 中无手写 setXxx(getXxx()) 的转换方法；调用既有分页/查询接口响应与改造前一致。

### 实施任务

- [x] T004 [P] [US1] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/user/service/impl/UserServiceImpl.java 中将 convertToListVO、convertToDTO 改为使用 org.springframework.beans.BeanUtils.copyProperties，保留字段名/类型不一致时的少量手写并注释原因
- [x] T005 [P] [US1] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/role/service/impl/RoleServiceImpl.java 中将 convertToListVO 改为使用 BeanUtils.copyProperties
- [x] T006 [P] [US1] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/permission/service/impl/PermissionServiceImpl.java 中将 convertToListVO 改为使用 BeanUtils.copyProperties
- [x] T007 [P] [US1] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/settings/service/impl/SystemSettingServiceImpl.java 中将 convertToVO 改为使用 BeanUtils.copyProperties（注意 BaseEntity 与 VO 字段对应，必要时忽略或手写个别字段并注释）
- [x] T008 [US1] 运行接口回归：调用 GET /api/v1/users、/api/v1/roles、/api/v1/permissions、/api/v1/system-settings/page 等分页接口，确认响应结构与数据与改造前一致

---

## Phase 4: US2 - 参数与空值处理合规（原则 21）

**用户场景**：审查者能通过 Assert/Optional 快速理解前置条件与空值语义，无需追踪多层 if-else。

**独立测试标准**：分页/查询方法中入参与默认值逻辑使用 Optional 或 Assert，无冗长 if-else 判空链；接口行为与改造前一致。

### 实施任务

- [x] T009 [US2] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/user/service/impl/UserServiceImpl.java 的 listUsersPage 中，将 `query != null ? query.getPageSafe() : 1` 及 sort 判空改为 Optional.ofNullable(query).map(UserQueryDTO::getPageSafe).orElse(1) 与 Optional.ofNullable(query).map(UserQueryDTO::getSort).orElse(null)，对 query 内 username/status 的判空保持使用 StringUtils.hasText 或改为 Optional
- [x] T010 [US2] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/role/service/impl/RoleServiceImpl.java 的 listRolesPage 中，将 query 与 sort 的判空改为 Optional 或 Assert（语义：可选默认值用 Optional）
- [x] T011 [US2] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/permission/service/impl/PermissionServiceImpl.java 的 listPermissionsPage 中，将 query 与 sort 的判空改为 Optional 或 Assert
- [x] T012 [US2] 在 atlas-service/atlas-system/src/main/java/com/atlas/system/settings/service/impl/SystemSettingServiceImpl.java 的 listSettingsPage 中，将 queryDTO 与 sort 的判空改为 Optional 或 Assert
- [x] T013 [US2] 在 atlas-service/atlas-system 中检查 getRolesByUserId、getPermissionsByUserId 等方法的判空（如 `x == null ? Collections.emptyList() : x`），在可读性允许的前提下改为 Optional.ofNullable(x).orElse(Collections.emptyList()) 等，涉及文件如 PermissionServiceImpl.java、UserServiceImpl.java
- [x] T014 [US2] 运行接口回归：再次调用四个分页接口及受影响的其他接口，确认行为与改造前一致

---

## Phase 5: Polish（验证与收尾）

**目标**：确认原则 20/21 合规且无功能回归。

- [x] T015 按 specs/017-constitution-principles-refactor/quickstart.md 执行完整验证步骤（编译、启动、四个分页接口 curl、代码审查检查点）
- [x] T016 若 quickstart.md 需补充或修正，则更新 specs/017-constitution-principles-refactor/quickstart.md；确认 refactor-list.md 已更新为“已改造”状态或删除临时清单

---

## 依赖关系图

```
Phase 1 (Setup)
    T001
      ↓
Phase 2 (Foundational)
    T002 → T003
      ↓
Phase 3 (US1)                    Phase 4 (US2)
T004→T005→T006→T007→T008         T009→T010→T011→T012→T013→T014
      ↓                                    ↓
                    Phase 5 (Polish)
                         T015 → T016
```

说明：US1 与 US2 可在 Phase 2 完成后按顺序执行；T004～T007 为不同文件可并行实施，T009～T012 同理。

---

## 并行执行示例

```text
# 第一批
T001 → T002 → T003

# 第二批（US1 内可并行）
T004 [P] UserServiceImpl 转换
T005 [P] RoleServiceImpl 转换
T006 [P] PermissionServiceImpl 转换
T007 [P] SystemSettingServiceImpl 转换
→ T008 回归

# 第三批（US2 顺序执行，因模式一致可批量改）
T009 → T010 → T011 → T012 → T013 → T014

# 第四批
T015 → T016
```

---

## 实施策略

### MVP 范围

**建议 MVP**：Phase 1 + Phase 2 + Phase 3（T001～T008），交付扫描清单与对象转换合规（原则 20），并完成一次接口回归。

### 增量交付

| 迭代 | 范围 | 交付物 |
|------|------|--------|
| 1 | Setup + Foundational + US1 | 改造清单、User/Role/Permission/Settings 转换改为 BeanUtils、回归通过 |
| 2 | US2 + Polish | 判空改为 Optional/Assert、完整回归与文档 |

---

## 任务统计

| 指标 | 数值 |
|------|------|
| 总任务数 | 16 |
| Phase 1 Setup | 1 |
| Phase 2 Foundational | 2 |
| US1 | 5 |
| US2 | 6 |
| Polish | 2 |
| 可并行任务 | T004、T005、T006、T007 |

---

## 验收清单

- [x] 已改造的 Entity/DTO/VO 转换均使用 BeanUtils（或 MapStruct），无大段手写 setXxx(getXxx())
- [x] 已改造的分页/查询入参与默认值逻辑使用 Assert 或 Optional，无冗长 if-else 判空链
- [x] 四个分页接口（用户、角色、权限、系统设置）回归通过，行为与改造前一致
- [x] 代码审查可依据原则 20、21 对改动做合规检查

---

## 相关文档

- [功能规格说明](./spec.md)
- [实施计划](./plan.md)
- [数据模型](./data-model.md)
- [研究文档](./research.md)
- [快速开始](./quickstart.md)
- [API 契约说明](./contracts/README.md)
