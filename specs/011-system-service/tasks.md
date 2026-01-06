# 任务清单

## 宪法合规检查清单

在开始实施前，确认以下事项：

- [ ] 技术栈版本符合宪法要求（JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0）
- [ ] API 设计遵循 RESTful 规范
- [ ] 代码注释使用中文
- [ ] 已识别可复用的公共方法（`atlas-common-feature-core`、`atlas-common-infra-db`）
- [ ] 包结构符合规范（业务模块按业务模块再按技术分层组织）
- [ ] 所有对象类型（DTO、VO、Entity、BO、枚举）放在 `model` 包下
- [ ] 数据库技术使用 PostgreSQL + MyBatis-Plus + Flyway
- [ ] SQL 目录管理符合规范（在服务目录下建立 `sql` 目录，按版本管理）

## 依赖关系图

```
Phase 1 (Setup) → Phase 2 (Foundational) → Phase 3 (US1: 用户查询) → Phase 4 (US2: 权限查询)
                                                                              ↓
                                                          Phase 5 (US3: 用户/角色/权限管理)
                                                                              ↓
                                                          Phase 6 (US4: 集成验收)
```

**说明**:
- Phase 1-2: 必须完成，为所有后续阶段提供基础
- Phase 3: 用户查询功能（US1），Auth 服务查询用户信息进行认证
- Phase 4: 权限查询功能（US2），Auth 服务查询用户权限进行授权
- Phase 5: 用户/角色/权限管理功能（US3），支持最小闭环
- Phase 6: 集成验收（US4），端到端测试

## 并行执行示例

### Phase 3 内部可以并行
- 用户实体和 Mapper（T030-T033）
- 用户 Service 实现（T034-T036）
- 用户 Controller 实现（T037-T039）

### Phase 4 内部可以并行
- 角色和权限实体及 Mapper（T040-T047）
- 权限 Service 实现（T048-T051）
- 权限 Controller 实现（T052-T055）

### Phase 5 内部可以并行
- 用户管理接口（T056-T059）
- 角色管理接口（T060-T063）
- 权限管理接口（T064-T067）
- 关联管理接口（T068-T071）

## 实施策略

**MVP 范围**: Phase 1 + Phase 2 + Phase 3（用户查询功能）
- 最小可行产品：Auth 服务能够查询用户信息进行认证
- 后续增量：权限查询、管理功能、集成验收

**增量交付**:
1. **增量 1**: 用户查询功能（US1）- 支持 Auth 服务查询用户信息
2. **增量 2**: 权限查询功能（US2）- 支持 Auth 服务查询用户权限
3. **增量 3**: 用户/角色/权限管理功能（US3）- 支持最小闭环
4. **增量 4**: 集成验收（US4）- 端到端测试

---

## Phase 1: 项目初始化

**目标**: 创建 `atlas-service/atlas-system` 模块，配置基础依赖和项目结构。

**独立测试标准**: 项目可以成功编译，依赖配置正确，目录结构符合规范，服务能够启动并注册到 Nacos。

### 任务列表

- [X] T001 在 `atlas-service/pom.xml` 中添加 `atlas-system` 模块（如果 `atlas-service` 模块不存在，先创建）
- [X] T002 创建 `atlas-service/atlas-system/pom.xml`，配置父模块和基础依赖
- [X] T003 在 `atlas-service/atlas-system/pom.xml` 中添加 `spring-boot-starter-web` 依赖
- [X] T004 在 `atlas-service/atlas-system/pom.xml` 中添加 `spring-cloud-starter-alibaba-nacos-discovery` 依赖
- [X] T005 在 `atlas-service/atlas-system/pom.xml` 中添加 `spring-cloud-starter-alibaba-nacos-config` 依赖
- [X] T006 在 `atlas-service/atlas-system/pom.xml` 中添加 `spring-cloud-starter-openfeign` 依赖
- [X] T007 在 `atlas-service/atlas-system/pom.xml` 中添加 `mybatis-plus-boot-starter` 依赖
- [X] T008 在 `atlas-service/atlas-system/pom.xml` 中添加 `postgresql` 驱动依赖
- [X] T009 在 `atlas-service/atlas-system/pom.xml` 中添加 `flyway-core` 依赖
- [X] T010 在 `atlas-service/atlas-system/pom.xml` 中添加 `atlas-system-api` 依赖
- [X] T011 在 `atlas-service/atlas-system/pom.xml` 中添加 `atlas-common-infra-db` 依赖
- [X] T012 在 `atlas-service/atlas-system/pom.xml` 中添加 `atlas-common-feature-core` 依赖
- [X] T013 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/user/controller`
- [X] T014 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/user/service/impl`
- [X] T015 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/user/mapper`
- [X] T016 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/user/model/entity`
- [X] T017 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/role/controller`
- [X] T018 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/role/service/impl`
- [X] T019 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/role/mapper`
- [X] T020 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/role/model/entity`
- [X] T021 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/permission/controller`
- [X] T022 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/permission/service/impl`
- [X] T023 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/permission/mapper`
- [X] T024 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/permission/model/entity`
- [X] T025 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/config`
- [X] T026 创建目录结构：`atlas-service/atlas-system/src/main/java/com/atlas/system/constant`
- [X] T027 创建目录结构：`atlas-service/atlas-system/src/test/java/com/atlas/system`
- [X] T028 创建 `atlas-service/atlas-system/src/main/resources/application.yml` 配置文件
- [X] T029 创建 `atlas-service/atlas-system/src/main/resources/application-dev.yml` 开发环境配置
- [X] T030 创建 `atlas-service/atlas-system/src/main/resources/application-prod.yml` 生产环境配置
- [X] T031 创建 `atlas-service/atlas-system/src/main/resources/application-test.yml` 测试环境配置
- [X] T032 创建 `atlas-service/atlas-system/src/main/resources/db/migration` 目录（Flyway 迁移脚本目录）
- [X] T033 创建 `atlas-service/atlas-system/README.md` 模块文档
- [X] T034 创建 `atlas-service/atlas-system/sql/v1.0.0` 目录（SQL 脚本版本目录）
- [X] T035 验证项目可以成功编译（`mvn clean compile`）- 注意：需要 JDK 21，当前环境为 JDK 24，模块结构已正确创建

---

## Phase 2: 数据库设计和迁移脚本

**目标**: 设计数据库表结构，创建 Flyway 迁移脚本和 SQL 目录脚本。

**独立测试标准**: 数据库迁移脚本可以成功执行，所有表结构创建成功，`sql` 目录结构符合规范。

### 任务列表

- [ ] T036 设计用户表（`sys_user`）结构，包含所有必需字段和索引
- [ ] T037 设计角色表（`sys_role`）结构，包含所有必需字段和索引
- [ ] T038 设计权限表（`sys_permission`）结构，包含所有必需字段和索引
- [ ] T039 设计用户角色关联表（`sys_user_role`）结构，包含外键约束
- [ ] T040 设计角色权限关联表（`sys_role_permission`）结构，包含外键约束
- [ ] T041 创建 Flyway 迁移脚本 `atlas-service/atlas-system/src/main/resources/db/migration/V1__Create_user_role_permission_tables.sql`
- [ ] T042 在 Flyway 迁移脚本中创建用户表（`sys_user`）
- [ ] T043 在 Flyway 迁移脚本中创建角色表（`sys_role`）
- [ ] T044 在 Flyway 迁移脚本中创建权限表（`sys_permission`）
- [ ] T045 在 Flyway 迁移脚本中创建用户角色关联表（`sys_user_role`）
- [ ] T046 在 Flyway 迁移脚本中创建角色权限关联表（`sys_role_permission`）
- [ ] T047 创建 SQL 脚本 `atlas-service/atlas-system/sql/v1.0.0/001_create_user_table.sql`
- [ ] T048 创建 SQL 脚本 `atlas-service/atlas-system/sql/v1.0.0/002_create_role_table.sql`
- [ ] T049 创建 SQL 脚本 `atlas-service/atlas-system/sql/v1.0.0/003_create_permission_table.sql`
- [ ] T050 创建 SQL 脚本 `atlas-service/atlas-system/sql/v1.0.0/004_create_user_role_table.sql`
- [ ] T051 创建 SQL 脚本 `atlas-service/atlas-system/sql/v1.0.0/005_create_role_permission_table.sql`
- [ ] T052 创建 `atlas-service/atlas-system/sql/v1.0.0/README.md`，说明版本变更内容
- [ ] T053 验证数据库迁移脚本可以成功执行（启动服务或手动执行 Flyway）

---

## Phase 3: US1 - Auth 服务查询用户信息进行认证

**目标**: 实现用户查询功能，支持 Auth 服务通过 Feign 接口查询用户信息进行认证。

**独立测试标准**: 
- Auth 服务能够成功调用 System 服务查询用户信息（根据用户ID和用户名）
- 用户不存在时返回适当的错误码
- 接口返回格式符合 `atlas-system-api` 定义的契约

**用户故事**: Auth 服务查询用户信息进行认证

### 任务列表

#### 实体类和 Mapper

- [ ] T054 [P] [US1] 创建 `User` 实体类在 `atlas-service/atlas-system/src/main/java/com/atlas/system/user/model/entity/User.java`
- [ ] T055 [P] [US1] 在 `User` 实体类中添加 MyBatis-Plus 注解（`@TableName`、`@TableId` 等）
- [ ] T056 [P] [US1] 在 `User` 实体类中添加所有字段（userId、username、password、nickname、email、phone、status、avatar、createdAt、updatedAt）
- [ ] T057 [P] [US1] 创建 `UserMapper` 接口在 `atlas-service/atlas-system/src/main/java/com/atlas/system/user/mapper/UserMapper.java`
- [ ] T058 [US1] 在 `UserMapper` 中继承 `BaseMapper<User>`
- [ ] T059 [US1] 在 `UserMapper` 中添加 `selectByUsername` 方法（根据用户名查询用户）

#### Service 层

- [ ] T060 [US1] 创建 `UserService` 接口在 `atlas-service/atlas-system/src/main/java/com/atlas/system/user/service/UserService.java`
- [ ] T061 [US1] 在 `UserService` 接口中添加 `getUserById(Long userId)` 方法
- [ ] T062 [US1] 在 `UserService` 接口中添加 `getUserByUsername(String username)` 方法
- [ ] T063 [US1] 创建 `UserServiceImpl` 实现类在 `atlas-service/atlas-system/src/main/java/com/atlas/system/user/service/impl/UserServiceImpl.java`
- [ ] T064 [US1] 在 `UserServiceImpl` 中实现 `getUserById` 方法，调用 Mapper 查询并转换为 DTO
- [ ] T065 [US1] 在 `UserServiceImpl` 中实现 `getUserByUsername` 方法，调用 Mapper 查询并转换为 DTO
- [ ] T066 [US1] 在 `UserServiceImpl` 中实现 Entity 到 DTO 的转换方法
- [ ] T067 [US1] 在 `UserServiceImpl` 中处理用户不存在的情况，抛出 `BusinessException`（错误码 `032001`）

#### Controller 层

- [ ] T068 [US1] 创建 `UserController` 在 `atlas-service/atlas-system/src/main/java/com/atlas/system/user/controller/UserController.java`
- [ ] T069 [US1] 在 `UserController` 中实现 `atlas-system-api` 的 `UserQueryApi` 接口
- [ ] T070 [US1] 在 `UserController` 中实现 `GET /api/v1/users/{userId}` 接口
- [ ] T071 [US1] 在 `UserController` 中实现 `GET /api/v1/users/by-username?username={username}` 接口
- [ ] T072 [US1] 在 `UserController` 中确保接口返回 `Result<UserDTO>` 格式
- [ ] T073 [US1] 在 `UserController` 中添加完整的中文注释（类、方法、参数）

#### 配置和启动

- [ ] T074 [US1] 创建 `SystemApplication` 启动类在 `atlas-service/atlas-system/src/main/java/com/atlas/system/SystemApplication.java`
- [ ] T075 [US1] 在 `SystemApplication` 中添加 `@SpringBootApplication` 注解
- [ ] T076 [US1] 在 `SystemApplication` 中添加 `@EnableDiscoveryClient` 注解（如需要）
- [ ] T077 [US1] 在 `SystemApplication` 中添加 `@EnableFeignClients` 注解（如需要）
- [ ] T078 [US1] 在 `application.yml` 中配置数据库连接信息
- [ ] T079 [US1] 在 `application.yml` 中配置 Nacos 服务注册与发现
- [ ] T080 [US1] 在 `application.yml` 中配置 Flyway 迁移脚本路径
- [ ] T081 [US1] 验证服务能够成功启动并注册到 Nacos

---

## Phase 4: US2 - Auth 服务查询用户权限进行授权

**目标**: 实现权限查询功能，支持 Auth 服务通过 Feign 接口查询用户权限进行授权。

**独立测试标准**: 
- Auth 服务能够成功调用 System 服务查询用户角色和权限
- 用户不存在时返回空列表或适当的错误码
- 接口返回格式符合 `atlas-system-api` 定义的契约

**用户故事**: Auth 服务查询用户权限进行授权

### 任务列表

#### 实体类和 Mapper

- [ ] T082 [P] [US2] 创建 `Role` 实体类在 `atlas-service/atlas-system/src/main/java/com/atlas/system/role/model/entity/Role.java`
- [ ] T083 [P] [US2] 在 `Role` 实体类中添加 MyBatis-Plus 注解和所有字段
- [ ] T084 [P] [US2] 创建 `Permission` 实体类在 `atlas-service/atlas-system/src/main/java/com/atlas/system/permission/model/entity/Permission.java`
- [ ] T085 [P] [US2] 在 `Permission` 实体类中添加 MyBatis-Plus 注解和所有字段
- [ ] T086 [P] [US2] 创建 `UserRole` 实体类在 `atlas-service/atlas-system/src/main/java/com/atlas/system/user/model/entity/UserRole.java`
- [ ] T087 [P] [US2] 在 `UserRole` 实体类中添加 MyBatis-Plus 注解和所有字段
- [ ] T088 [P] [US2] 创建 `RolePermission` 实体类在 `atlas-service/atlas-system/src/main/java/com/atlas/system/role/model/entity/RolePermission.java`
- [ ] T089 [P] [US2] 在 `RolePermission` 实体类中添加 MyBatis-Plus 注解和所有字段
- [ ] T090 [P] [US2] 创建 `RoleMapper` 接口在 `atlas-service/atlas-system/src/main/java/com/atlas/system/role/mapper/RoleMapper.java`
- [ ] T091 [P] [US2] 在 `RoleMapper` 中继承 `BaseMapper<Role>`
- [ ] T092 [P] [US2] 创建 `PermissionMapper` 接口在 `atlas-service/atlas-system/src/main/java/com/atlas/system/permission/mapper/PermissionMapper.java`
- [ ] T093 [P] [US2] 在 `PermissionMapper` 中继承 `BaseMapper<Permission>`
- [ ] T094 [US2] 创建 `UserRoleMapper` 接口在 `atlas-service/atlas-system/src/main/java/com/atlas/system/user/mapper/UserRoleMapper.java`
- [ ] T095 [US2] 在 `UserRoleMapper` 中继承 `BaseMapper<UserRole>`
- [ ] T096 [US2] 在 `UserRoleMapper` 中添加 `selectRoleCodesByUserId` 方法（根据用户ID查询角色代码列表）
- [ ] T097 [US2] 创建 `RolePermissionMapper` 接口在 `atlas-service/atlas-system/src/main/java/com/atlas/system/role/mapper/RolePermissionMapper.java`
- [ ] T098 [US2] 在 `RolePermissionMapper` 中继承 `BaseMapper<RolePermission>`
- [ ] T099 [US2] 在 `RolePermissionMapper` 中添加 `selectPermissionCodesByRoleIds` 方法（根据角色ID列表查询权限代码列表）

#### Service 层

- [ ] T100 [US2] 创建 `PermissionService` 接口在 `atlas-service/atlas-system/src/main/java/com/atlas/system/permission/service/PermissionService.java`
- [ ] T101 [US2] 在 `PermissionService` 接口中添加 `getRolesByUserId(Long userId)` 方法
- [ ] T102 [US2] 在 `PermissionService` 接口中添加 `getPermissionsByUserId(Long userId)` 方法
- [ ] T103 [US2] 在 `PermissionService` 接口中添加 `getAuthoritiesByUserId(Long userId)` 方法
- [ ] T104 [US2] 创建 `PermissionServiceImpl` 实现类在 `atlas-service/atlas-system/src/main/java/com/atlas/system/permission/service/impl/PermissionServiceImpl.java`
- [ ] T105 [US2] 在 `PermissionServiceImpl` 中实现 `getRolesByUserId` 方法，查询用户角色列表
- [ ] T106 [US2] 在 `PermissionServiceImpl` 中实现 `getPermissionsByUserId` 方法，通过角色关联查询权限列表并去重
- [ ] T107 [US2] 在 `PermissionServiceImpl` 中实现 `getAuthoritiesByUserId` 方法，返回 `UserAuthoritiesDTO`
- [ ] T108 [US2] 在 `PermissionServiceImpl` 中处理用户不存在的情况，返回空列表

#### Controller 层

- [ ] T109 [US2] 创建 `PermissionController` 在 `atlas-service/atlas-system/src/main/java/com/atlas/system/permission/controller/PermissionController.java`
- [ ] T110 [US2] 在 `PermissionController` 中实现 `atlas-system-api` 的 `PermissionQueryApi` 接口
- [ ] T111 [US2] 在 `PermissionController` 中实现 `GET /api/v1/users/{userId}/roles` 接口
- [ ] T112 [US2] 在 `PermissionController` 中实现 `GET /api/v1/users/{userId}/permissions` 接口
- [ ] T113 [US2] 在 `PermissionController` 中实现 `GET /api/v1/users/{userId}/authorities` 接口
- [ ] T114 [US2] 在 `PermissionController` 中确保接口返回格式符合 API 契约
- [ ] T115 [US2] 在 `PermissionController` 中添加完整的中文注释（类、方法、参数）

---

## Phase 5: US3 - 管理员新增用户/角色/权限后立即生效

**目标**: 实现用户、角色、权限的创建和关联功能，支持最小闭环，数据变更后立即生效。

**独立测试标准**: 
- 能够创建用户、角色、权限
- 能够建立用户与角色的关联
- 能够建立角色与权限的关联
- 数据保存后立即生效，Auth 服务能够查询到最新数据

**用户故事**: 管理员新增用户/角色/权限后立即生效

### 任务列表

#### 用户管理

- [ ] T116 [P] [US3] 创建 `UserManagementController` 在 `atlas-service/atlas-system/src/main/java/com/atlas/system/user/controller/UserManagementController.java`
- [ ] T117 [P] [US3] 在 `UserManagementController` 中实现 `POST /api/v1/users` 接口（创建用户）
- [ ] T118 [P] [US3] 在 `UserService` 接口中添加 `createUser(UserCreateDTO userCreateDTO)` 方法
- [ ] T119 [P] [US3] 在 `UserServiceImpl` 中实现 `createUser` 方法，保存用户到数据库
- [ ] T120 [P] [US3] 在 `UserServiceImpl` 中处理用户名重复的情况，抛出 `BusinessException`（错误码 `032004`）

#### 角色管理

- [ ] T121 [P] [US3] 创建 `RoleManagementController` 在 `atlas-service/atlas-system/src/main/java/com/atlas/system/role/controller/RoleManagementController.java`
- [ ] T122 [P] [US3] 在 `RoleManagementController` 中实现 `POST /api/v1/roles` 接口（创建角色）
- [ ] T123 [P] [US3] 创建 `RoleService` 接口在 `atlas-service/atlas-system/src/main/java/com/atlas/system/role/service/RoleService.java`
- [ ] T124 [P] [US3] 在 `RoleService` 接口中添加 `createRole(RoleCreateDTO roleCreateDTO)` 方法
- [ ] T125 [P] [US3] 创建 `RoleServiceImpl` 实现类在 `atlas-service/atlas-system/src/main/java/com/atlas/system/role/service/impl/RoleServiceImpl.java`
- [ ] T126 [P] [US3] 在 `RoleServiceImpl` 中实现 `createRole` 方法，保存角色到数据库
- [ ] T127 [P] [US3] 在 `RoleServiceImpl` 中处理角色代码重复的情况，抛出 `BusinessException`（错误码 `032005`）

#### 权限管理

- [ ] T128 [P] [US3] 创建 `PermissionManagementController` 在 `atlas-service/atlas-system/src/main/java/com/atlas/system/permission/controller/PermissionManagementController.java`
- [ ] T129 [P] [US3] 在 `PermissionManagementController` 中实现 `POST /api/v1/permissions` 接口（创建权限）
- [ ] T130 [P] [US3] 在 `PermissionService` 接口中添加 `createPermission(PermissionCreateDTO permissionCreateDTO)` 方法
- [ ] T131 [P] [US3] 在 `PermissionServiceImpl` 中实现 `createPermission` 方法，保存权限到数据库
- [ ] T132 [P] [US3] 在 `PermissionServiceImpl` 中处理权限代码重复的情况，抛出 `BusinessException`（错误码 `032006`）

#### 关联管理

- [ ] T133 [US3] 在 `UserManagementController` 中实现 `POST /api/v1/users/{userId}/roles` 接口（为用户分配角色）
- [ ] T134 [US3] 在 `UserService` 接口中添加 `assignRoleToUser(Long userId, Long roleId)` 方法
- [ ] T135 [US3] 在 `UserServiceImpl` 中实现 `assignRoleToUser` 方法，保存用户角色关联到数据库
- [ ] T136 [US3] 在 `UserServiceImpl` 中处理用户或角色不存在的情况，抛出适当的异常
- [ ] T137 [US3] 在 `RoleManagementController` 中实现 `POST /api/v1/roles/{roleId}/permissions` 接口（为角色分配权限）
- [ ] T138 [US3] 在 `RoleService` 接口中添加 `assignPermissionToRole(Long roleId, Long permissionId)` 方法
- [ ] T139 [US3] 在 `RoleServiceImpl` 中实现 `assignPermissionToRole` 方法，保存角色权限关联到数据库
- [ ] T140 [US3] 在 `RoleServiceImpl` 中处理角色或权限不存在的情况，抛出适当的异常

#### DTO 和 VO（如需要）

- [ ] T141 [P] [US3] 创建 `UserCreateDTO` 在 `atlas-service/atlas-system/src/main/java/com/atlas/system/user/model/dto/UserCreateDTO.java`（如需要）
- [ ] T142 [P] [US3] 创建 `RoleCreateDTO` 在 `atlas-service/atlas-system/src/main/java/com/atlas/system/role/model/dto/RoleCreateDTO.java`（如需要）
- [ ] T143 [P] [US3] 创建 `PermissionCreateDTO` 在 `atlas-service/atlas-system/src/main/java/com/atlas/system/permission/model/dto/PermissionCreateDTO.java`（如需要）

---

## Phase 6: US4 - 集成验收和测试

**目标**: 完成集成测试和验收测试，确保端到端功能正常。

**独立测试标准**: 
- Auth 服务能够成功调用 System 服务查询用户和权限
- Gateway 能够通过 Auth 服务验证权限，正确放行或拒绝请求
- 新增用户/角色/权限后，授权立即生效，无需重启服务

**用户故事**: 通过 Gateway 访问受保护接口

### 任务列表

#### 错误码和常量

- [ ] T144 [US4] 创建 `SystemErrorCode` 常量类在 `atlas-service/atlas-system/src/main/java/com/atlas/system/constant/SystemErrorCode.java`
- [ ] T145 [US4] 在 `SystemErrorCode` 中定义用户相关错误码（032001-032099）
- [ ] T146 [US4] 在 `SystemErrorCode` 中定义角色相关错误码（032101-032199）
- [ ] T147 [US4] 在 `SystemErrorCode` 中定义权限相关错误码（032201-032299）

#### 异常处理

- [ ] T148 [US4] 创建全局异常处理器在 `atlas-service/atlas-system/src/main/java/com/atlas/system/config/GlobalExceptionHandler.java`（如需要）
- [ ] T149 [US4] 在全局异常处理器中处理 `BusinessException`，返回统一的错误响应格式

#### 单元测试

- [ ] T150 [P] [US4] 编写 `UserService` 单元测试在 `atlas-service/atlas-system/src/test/java/com/atlas/system/user/service/UserServiceTest.java`
- [ ] T151 [P] [US4] 编写 `PermissionService` 单元测试在 `atlas-service/atlas-system/src/test/java/com/atlas/system/permission/service/PermissionServiceTest.java`
- [ ] T152 [P] [US4] 编写 `UserMapper` 单元测试在 `atlas-service/atlas-system/src/test/java/com/atlas/system/user/mapper/UserMapperTest.java`
- [ ] T153 [P] [US4] 编写 `PermissionMapper` 单元测试在 `atlas-service/atlas-system/src/test/java/com/atlas/system/permission/mapper/PermissionMapperTest.java`
- [ ] T154 [US4] 确保单元测试覆盖率 ≥ 70%

#### 集成测试

- [ ] T155 [US4] 编写 `UserController` 集成测试在 `atlas-service/atlas-system/src/test/java/com/atlas/system/user/controller/UserControllerTest.java`
- [ ] T156 [US4] 编写 `PermissionController` 集成测试在 `atlas-service/atlas-system/src/test/java/com/atlas/system/permission/controller/PermissionControllerTest.java`
- [ ] T157 [US4] 测试用户查询接口（根据用户ID和用户名）
- [ ] T158 [US4] 测试权限查询接口（角色列表、权限列表、完整权限信息）

#### 验收测试

- [ ] T159 [US4] 验证 Auth 服务能够成功调用 System 服务查询用户信息
- [ ] T160 [US4] 验证 Auth 服务能够成功调用 System 服务查询用户权限
- [ ] T161 [US4] 验证新增用户后，Auth 服务能够立即查询到新用户
- [ ] T162 [US4] 验证新增角色并分配给用户后，Auth 服务能够立即查询到新角色
- [ ] T163 [US4] 验证新增权限并分配给角色后，Auth 服务能够立即查询到新权限
- [ ] T164 [US4] 验证通过 Gateway 访问受保护接口能正确放行（有权限）
- [ ] T165 [US4] 验证通过 Gateway 访问受保护接口能正确拒绝（无权限）

---

## Phase 7: 文档和优化

**目标**: 完善文档，代码优化，确保代码质量。

**独立测试标准**: 文档完整，代码通过所有检查，符合项目规范。

### 任务列表

- [ ] T166 更新 `atlas-service/atlas-system/README.md`，包含模块简介、主要功能、快速开始、相关文档链接
- [ ] T167 确保所有类和方法添加完整的中文注释
- [ ] T168 检查并提取可复用的公共方法
- [ ] T169 运行代码格式化工具（Spotless），确保代码格式符合规范
- [ ] T170 运行静态代码分析工具（Checkstyle、PMD），修复发现的问题
- [ ] T171 验证所有测试通过（`mvn test`）
- [ ] T172 验证服务能够成功启动并注册到 Nacos
- [ ] T173 验证数据库迁移脚本执行成功
- [ ] T174 验证 `sql` 目录结构符合规范

---

## 任务统计

- **总任务数**: 174
- **Phase 1 (Setup)**: 35 个任务
- **Phase 2 (Foundational)**: 18 个任务
- **Phase 3 (US1)**: 28 个任务
- **Phase 4 (US2)**: 34 个任务
- **Phase 5 (US3)**: 28 个任务
- **Phase 6 (US4)**: 22 个任务
- **Phase 7 (Polish)**: 9 个任务

## 并行执行机会

- **Phase 3 内部**: 实体类、Mapper、Service、Controller 可以并行开发（不同文件）
- **Phase 4 内部**: 角色和权限相关代码可以并行开发
- **Phase 5 内部**: 用户、角色、权限管理功能可以并行开发
- **Phase 6 内部**: 单元测试可以并行编写

## MVP 范围建议

**最小可行产品**: Phase 1 + Phase 2 + Phase 3（用户查询功能）
- 支持 Auth 服务查询用户信息进行认证
- 后续增量：权限查询、管理功能、集成验收

