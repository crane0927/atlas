# 任务清单

## 功能概述

实现 `atlas-system-api` 模块，定义系统域的 API 接口契约，包括用户查询和权限查询接口，供 `atlas-auth` 服务使用。

## 用户故事

### US1: Auth 服务查询用户信息
**优先级**: P1  
**描述**: Auth 服务通过 Feign 接口调用 System 服务查询用户信息，用于用户认证。  
**验收标准**: Auth 服务能够成功调用 `UserQueryApi` 接口查询用户信息，返回 `UserDTO` 对象。

### US2: Auth 服务查询用户权限
**优先级**: P1  
**描述**: Auth 服务通过 Feign 接口调用 System 服务查询用户权限和角色信息，用于用户授权。  
**验收标准**: Auth 服务能够成功调用 `PermissionQueryApi` 接口查询用户权限，返回 `UserAuthoritiesDTO` 对象。

### US3: 其他服务查询用户信息
**优先级**: P2  
**描述**: 其他业务服务（如 Order 服务）通过 Feign 接口调用 System 服务查询用户信息。  
**验收标准**: 业务服务能够成功调用 `UserQueryApi` 接口查询用户信息，复用 US1 的接口。

## 依赖关系

```
Phase 1 (Setup)
    ↓
Phase 2 (Foundational: 枚举、版本包管理)
    ↓
Phase 3 (US1: 用户查询接口 + UserDTO)
    ↓
Phase 4 (US2: 权限查询接口 + UserAuthoritiesDTO)
    ↓
Phase 5 (Polish: 文档、测试、验收)
```

**说明**:
- Phase 1 必须首先完成（项目初始化）
- Phase 2 必须在用户故事之前完成（枚举和版本包管理是基础）
- US1 和 US2 可以并行开发（不同的接口和 DTO）
- US3 复用 US1 的接口，无需额外实现

## 并行执行机会

- **Phase 3 和 Phase 4**: 可以并行执行（不同的接口和 DTO）
- **DTO 和枚举**: 可以并行实现（不同的文件）

## 实施策略

### MVP 范围
**最小可行产品**: Phase 1 + Phase 2 + Phase 3 (US1)
- 完成模块创建和基础设置
- 实现用户查询接口和 UserDTO
- 支持 Auth 服务查询用户信息的基本需求

### 增量交付
1. **迭代 1**: MVP (Phase 1-3)
2. **迭代 2**: 权限查询 (Phase 4 - US2)
3. **迭代 3**: 文档和测试完善 (Phase 5)

---

## Phase 1: 项目初始化

**目标**: 创建 `atlas-system-api` 模块，配置 Maven 项目结构。

**独立测试标准**: 模块可以成功编译，目录结构符合规范，`pom.xml` 配置正确。

### 任务列表

- [X] T001 创建 `atlas-service-api` 父模块目录和 `pom.xml`（packaging 为 pom）
- [X] T002 在根目录 `pom.xml` 中添加 `atlas-service-api` 模块到 `<modules>` 列表
- [X] T003 创建 `atlas-service-api/atlas-system-api` 目录结构：`atlas-service-api/atlas-system-api/src/main/java` 和 `atlas-service-api/atlas-system-api/src/test/java`
- [X] T004 创建 `atlas-service-api/atlas-system-api/pom.xml`，配置模块基本信息（groupId、artifactId、version、packaging）
- [X] T005 在 `atlas-service-api/atlas-system-api/pom.xml` 中添加父 POM 依赖（继承 `atlas-service-api` 父项目）
- [X] T006 在 `atlas-service-api/atlas-system-api/pom.xml` 中添加 `atlas-common-feature-core` 依赖
- [X] T007 在 `atlas-service-api/atlas-system-api/pom.xml` 中添加 `spring-cloud-starter-openfeign` 依赖
- [X] T008 在 `atlas-service-api/atlas-system-api/pom.xml` 中添加 `lombok` 依赖（可选，用于简化代码）
- [X] T009 验证 `atlas-service-api/atlas-system-api/pom.xml` 中不包含 web/db/redis 等实现层依赖
- [X] T010 创建版本包目录结构：`atlas-service-api/atlas-system-api/src/main/java/com/atlas/system/api/v1/model/dto`、`model/vo`、`model/enums`、`feign`

---

## Phase 2: 基础组件（枚举和版本包管理）

**目标**: 实现枚举常量和建立版本包管理规范。

**独立测试标准**: 枚举类可以成功编译，支持 JSON 序列化/反序列化，包结构符合版本管理规范。

### 任务列表

- [X] T011 [P] [US1] 创建 `UserStatus` 枚举类在 `atlas-service-api/atlas-system-api/src/main/java/com/atlas/system/api/v1/model/enums/UserStatus.java`
- [X] T012 [US1] 在 `UserStatus` 枚举中添加枚举值：ACTIVE（激活）、INACTIVE（未激活）、LOCKED（锁定）、DELETED（已删除）
- [X] T013 [US1] 为 `UserStatus` 枚举值添加完整的中文注释
- [X] T014 [P] [US1] 创建 `UserStatus` 枚举的单元测试在 `atlas-service-api/atlas-system-api/src/test/java/com/atlas/system/api/v1/model/enums/UserStatusTest.java`
- [X] T015 [US1] 在测试中验证 `UserStatus` 枚举的 JSON 序列化/反序列化功能

---

## Phase 3: US1 - Auth 服务查询用户信息

**目标**: 实现用户查询接口和 UserDTO，支持 Auth 服务查询用户信息。

**独立测试标准**: `UserQueryApi` 接口可以成功编译，`UserDTO` 支持 JSON 序列化/反序列化，接口定义符合 RESTful 规范。

### 任务列表

- [X] T016 [P] [US1] 创建 `UserDTO` 类在 `atlas-service-api/atlas-system-api/src/main/java/com/atlas/system/api/v1/model/dto/UserDTO.java`
- [X] T017 [US1] 在 `UserDTO` 中添加字段：`userId` (Long)、`username` (String)、`nickname` (String)、`email` (String)、`phone` (String)、`status` (UserStatus)、`avatar` (String)
- [X] T018 [US1] 为 `UserDTO` 的所有字段添加完整的中文注释
- [X] T019 [US1] 在 `UserDTO` 中使用 Lombok 注解（`@Data`、`@NoArgsConstructor`、`@AllArgsConstructor`）简化代码
- [X] T020 [P] [US1] 创建 `UserQueryApi` Feign 接口在 `atlas-service-api/atlas-system-api/src/main/java/com/atlas/system/api/v1/feign/UserQueryApi.java`
- [X] T021 [US1] 在 `UserQueryApi` 接口上添加 `@FeignClient(name = "atlas-system", path = "/api/v1")` 注解
- [X] T022 [US1] 在 `UserQueryApi` 接口中添加 `getUserById` 方法：`@GetMapping("/users/{userId}") Result<UserDTO> getUserById(@PathVariable Long userId)`
- [X] T023 [US1] 在 `UserQueryApi` 接口中添加 `getUserByUsername` 方法：`@GetMapping("/users/by-username") Result<UserDTO> getUserByUsername(@RequestParam String username)`
- [X] T024 [US1] 为 `UserQueryApi` 接口的所有方法添加完整的中文注释
- [X] T025 [P] [US1] 创建 `UserDTO` 的单元测试在 `atlas-service-api/atlas-system-api/src/test/java/com/atlas/system/api/v1/model/dto/UserDTOTest.java`
- [X] T026 [US1] 在测试中验证 `UserDTO` 的 JSON 序列化/反序列化功能
- [X] T027 [US1] 在测试中验证 `UserDTO` 字段的向后兼容性（新增字段可空）

---

## Phase 4: US2 - Auth 服务查询用户权限

**目标**: 实现权限查询接口和 UserAuthoritiesDTO，支持 Auth 服务查询用户权限。

**独立测试标准**: `PermissionQueryApi` 接口可以成功编译，`UserAuthoritiesDTO` 支持 JSON 序列化/反序列化，接口定义符合 RESTful 规范。

### 任务列表

- [X] T028 [P] [US2] 创建 `UserAuthoritiesDTO` 类在 `atlas-service-api/atlas-system-api/src/main/java/com/atlas/system/api/v1/model/dto/UserAuthoritiesDTO.java`
- [X] T029 [US2] 在 `UserAuthoritiesDTO` 中添加字段：`userId` (Long)、`roles` (List<String>)、`permissions` (List<String>)
- [X] T030 [US2] 为 `UserAuthoritiesDTO` 的所有字段添加完整的中文注释
- [X] T031 [US2] 在 `UserAuthoritiesDTO` 中使用 Lombok 注解（`@Data`、`@NoArgsConstructor`、`@AllArgsConstructor`）简化代码
- [X] T032 [P] [US2] 创建 `PermissionQueryApi` Feign 接口在 `atlas-service-api/atlas-system-api/src/main/java/com/atlas/system/api/v1/feign/PermissionQueryApi.java`
- [X] T033 [US2] 在 `PermissionQueryApi` 接口上添加 `@FeignClient(name = "atlas-system", path = "/api/v1")` 注解
- [X] T034 [US2] 在 `PermissionQueryApi` 接口中添加 `getUserRoles` 方法：`@GetMapping("/users/{userId}/roles") Result<List<String>> getUserRoles(@PathVariable Long userId)`
- [X] T035 [US2] 在 `PermissionQueryApi` 接口中添加 `getUserPermissions` 方法：`@GetMapping("/users/{userId}/permissions") Result<List<String>> getUserPermissions(@PathVariable Long userId)`
- [X] T036 [US2] 在 `PermissionQueryApi` 接口中添加 `getUserAuthorities` 方法：`@GetMapping("/users/{userId}/authorities") Result<UserAuthoritiesDTO> getUserAuthorities(@PathVariable Long userId)`
- [X] T037 [US2] 为 `PermissionQueryApi` 接口的所有方法添加完整的中文注释
- [X] T038 [P] [US2] 创建 `UserAuthoritiesDTO` 的单元测试在 `atlas-service-api/atlas-system-api/src/test/java/com/atlas/system/api/v1/model/dto/UserAuthoritiesDTOTest.java`
- [X] T039 [US2] 在测试中验证 `UserAuthoritiesDTO` 的 JSON 序列化/反序列化功能
- [X] T040 [US2] 在测试中验证 `UserAuthoritiesDTO` 字段的向后兼容性（新增字段可空）

---

## Phase 5: 文档与验收

**目标**: 完成模块文档编写、依赖约束验证和验收测试。

**独立测试标准**: 模块文档完整，依赖约束验证通过，包结构符合规范，所有验收标准满足。

### 任务列表

- [X] T041 创建 `atlas-service-api/atlas-system-api/README.md` 文档，包含模块简介、主要功能、快速开始、使用示例、配置说明
- [X] T042 在 `README.md` 中添加模块依赖说明（允许的依赖和禁止的依赖）
- [X] T043 在 `README.md` 中添加版本包管理说明（v1 包结构）
- [X] T044 在 `README.md` 中添加接口兼容性规则说明
- [X] T045 验证 `atlas-service-api/atlas-system-api/pom.xml` 中不包含 web/db/redis 等实现层依赖（使用 Maven 依赖分析工具）
- [X] T046 验证包结构符合技术模块规范（按技术分层组织：`com.atlas.system.api.v1.feign`、`model.dto`、`model.vo`、`model.enums`）
- [X] T047 验证所有类、方法、字段包含完整的中文注释
- [X] T048 验证接口路径遵循 RESTful 设计规范
- [X] T049 验证接口返回类型使用统一的 `Result<T>` 格式
- [X] T050 运行所有单元测试，确保测试通过
- [X] T051 验证模块可以成功编译和打包（`mvn clean compile` 和 `mvn clean package`）

---

## 任务统计

- **总任务数**: 51
- **Phase 1 (Setup)**: 10 个任务（包含创建 atlas-service-api 父模块）
- **Phase 2 (Foundational)**: 5 个任务
- **Phase 3 (US1)**: 12 个任务
- **Phase 4 (US2)**: 13 个任务
- **Phase 5 (Polish)**: 11 个任务

## 并行执行机会

- **T011 和 T016**: 可以并行（枚举和 DTO 不同文件）
- **T014 和 T025**: 可以并行（不同测试文件）
- **T016 和 T028**: 可以并行（不同 DTO 文件）
- **T020 和 T032**: 可以并行（不同 Feign 接口文件）
- **T025 和 T038**: 可以并行（不同测试文件）

## MVP 范围建议

**最小可行产品 (MVP)**: Phase 1 + Phase 2 + Phase 3 (US1)
- 总任务数: 27 个任务
- 核心功能: 用户查询接口和 UserDTO
- 支持场景: Auth 服务查询用户信息

**完整功能**: 所有 Phase (1-5)
- 总任务数: 51 个任务
- 核心功能: 用户查询接口、权限查询接口、所有 DTO 和枚举
- 支持场景: Auth 服务查询用户信息和权限，其他服务查询用户信息

## 重要说明

**关于 DTO 和 VO 的使用**:
- 根据项目宪法（原则 13），`atlas-service-api` 模块中**必须使用 DTO**，禁止使用 VO
- API 模块是服务间调用的契约，不涉及前端展示，因此应使用 DTO
- 如果需要在业务服务模块的 Controller 层使用 VO，应在 `atlas-service` 模块中定义，而不是在 `atlas-service-api` 模块中
- 本任务清单中的 DTO 定义在 `atlas-service-api/atlas-system-api` 模块中，符合宪法要求

