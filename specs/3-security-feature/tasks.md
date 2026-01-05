# 任务清单

## 宪法合规检查清单

在开始实施前，确认以下事项：

- [ ] 技术栈版本符合宪法要求（JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0）
- [ ] 代码注释使用中文
- [ ] 包名规范遵循 `com.atlas.common.feature.security`
- [ ] 配置文件使用 YAML 格式
- [ ] 抽象设计：所有接口和注解不包含实现细节

## 任务概览

**总任务数**: 45  
**阶段数**: 4  
**可并行任务**: 18

## 依赖关系

### 阶段完成顺序

1. **Phase 1: 项目初始化** → 必须先完成
2. **Phase 2: 核心接口实现** → 依赖 Phase 1
3. **Phase 3: 单元测试** → 依赖 Phase 2
4. **Phase 4: 文档和示例** → 依赖 Phase 2

### 功能需求映射

- **FR1（LoginUser 接口）**: Phase 2 实现，Phase 3 测试
- **FR2（权限注解）**: Phase 2 实现，Phase 3 测试
- **FR3（安全上下文接口）**: Phase 2 实现，Phase 3 测试

## 实施策略

**MVP 范围**: Phase 1 + Phase 2（核心接口和注解实现）  
**增量交付**: 
1. 先完成项目初始化和核心接口实现
2. 再完成单元测试，确保质量
3. 最后完善文档和示例

## Phase 1: 项目初始化

**目标**: 创建模块基础结构和配置

**独立测试标准**: 模块目录结构创建完成，POM 配置正确，可以开始实现核心接口

- [ ] T001 创建模块目录结构 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security`
- [ ] T002 创建源代码目录 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security`
- [ ] T003 创建测试代码目录 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security`
- [ ] T004 创建模块 POM 文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/pom.xml`
- [ ] T005 [FR1-FR3] 配置模块基本信息（groupId、artifactId、version、packaging）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/pom.xml`
- [ ] T006 [FR1-FR3] 配置继承父 POM 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/pom.xml`
- [ ] T007 [FR1-FR3] 确保无强制外部依赖（保持抽象层纯净性）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/pom.xml`
- [ ] T008 将模块添加到父 POM 的 modules 列表在 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`

## Phase 2: 核心接口实现

**目标**: 实现所有核心抽象接口和注解

**独立测试标准**: 所有核心接口和注解实现完成，可以编译通过，包含完整的中文注释，不包含实现细节

### FR1: LoginUser 接口

- [ ] T009 [FR1] 创建 LoginUser 接口文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/user/LoginUser.java`
- [ ] T010 [FR1] 定义 getUserId() 方法（返回 Object）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/user/LoginUser.java`
- [ ] T011 [FR1] 定义 getUsername() 方法（返回 String）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/user/LoginUser.java`
- [ ] T012 [FR1] 定义 getRoles() 方法（返回 List<String>）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/user/LoginUser.java`
- [ ] T013 [FR1] 定义 getPermissions() 方法（返回 List<String>）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/user/LoginUser.java`
- [ ] T014 [FR1] 定义 hasRole(String role) 方法（返回 boolean）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/user/LoginUser.java`
- [ ] T015 [FR1] 定义 hasPermission(String permission) 方法（返回 boolean）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/user/LoginUser.java`
- [ ] T016 [FR1] 添加完整的中文注释（接口、方法）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/user/LoginUser.java`

### FR2: 权限注解定义

- [ ] T017 [P] [FR2] 创建 Logical 枚举文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/Logical.java`
- [ ] T018 [FR2] 定义 Logical 枚举值（AND、OR）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/Logical.java`
- [ ] T019 [FR2] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/Logical.java`
- [ ] T020 [P] [FR2] 创建 @RequiresPermission 注解文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/RequiresPermission.java`
- [ ] T021 [FR2] 定义 @RequiresPermission 注解的 value 属性（String[]）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/RequiresPermission.java`
- [ ] T022 [FR2] 定义 @RequiresPermission 注解的 logical 属性（Logical，默认 AND）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/RequiresPermission.java`
- [ ] T023 [FR2] 配置 @RequiresPermission 注解的 @Target（TYPE、METHOD）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/RequiresPermission.java`
- [ ] T024 [FR2] 配置 @RequiresPermission 注解的 @Retention（RUNTIME）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/RequiresPermission.java`
- [ ] T025 [FR2] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/RequiresPermission.java`
- [ ] T026 [P] [FR2] 创建 @RequiresRole 注解文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/RequiresRole.java`
- [ ] T027 [FR2] 定义 @RequiresRole 注解的 value 属性（String[]）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/RequiresRole.java`
- [ ] T028 [FR2] 定义 @RequiresRole 注解的 logical 属性（Logical，默认 AND）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/RequiresRole.java`
- [ ] T029 [FR2] 配置 @RequiresRole 注解的 @Target（TYPE、METHOD）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/RequiresRole.java`
- [ ] T030 [FR2] 配置 @RequiresRole 注解的 @Retention（RUNTIME）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/RequiresRole.java`
- [ ] T031 [FR2] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/annotation/RequiresRole.java`

### FR3: 安全上下文接口

- [ ] T032 [P] [FR3] 创建 SecurityContext 接口文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/context/SecurityContext.java`
- [ ] T033 [FR3] 定义 SecurityContext.getLoginUser() 方法（返回 LoginUser）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/context/SecurityContext.java`
- [ ] T034 [FR3] 定义 SecurityContext.isAuthenticated() 方法（返回 boolean）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/context/SecurityContext.java`
- [ ] T035 [FR3] 定义 SecurityContext.clear() 方法（返回 void，可选）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/context/SecurityContext.java`
- [ ] T036 [FR3] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/context/SecurityContext.java`
- [ ] T037 [P] [FR3] 创建 SecurityContextHolder 抽象类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/context/SecurityContextHolder.java`
- [ ] T038 [FR3] 定义 SecurityContextHolder.getContext() 静态方法（返回 SecurityContext，抽象）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/context/SecurityContextHolder.java`
- [ ] T039 [FR3] 实现 SecurityContextHolder.getLoginUser() 静态方法（基于 getContext()）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/context/SecurityContextHolder.java`
- [ ] T040 [FR3] 实现 SecurityContextHolder.isAuthenticated() 静态方法（基于 getContext()）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/context/SecurityContextHolder.java`
- [ ] T041 [FR3] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/context/SecurityContextHolder.java`

### 可选：安全异常接口

- [ ] T042 [P] 创建 AuthenticationException 接口文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/exception/AuthenticationException.java`
- [ ] T043 定义 AuthenticationException.getMessage() 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/exception/AuthenticationException.java`
- [ ] T044 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/exception/AuthenticationException.java`
- [ ] T045 [P] 创建 AuthorizationException 接口文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/exception/AuthorizationException.java`
- [ ] T046 定义 AuthorizationException.getMessage() 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/exception/AuthorizationException.java`
- [ ] T047 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/main/java/com/atlas/common/feature/security/exception/AuthorizationException.java`

## Phase 3: 单元测试

**目标**: 编写接口和注解的单元测试

**独立测试标准**: 单元测试覆盖率 ≥ 80%，所有测试用例通过

### LoginUser 接口测试

- [ ] T048 [FR1] 创建 LoginUserTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/user/LoginUserTest.java`
- [ ] T049 [FR1] 创建测试实现类 DefaultLoginUser 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/user/LoginUserTest.java`
- [ ] T050 [FR1] 测试 getUserId() 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/user/LoginUserTest.java`
- [ ] T051 [FR1] 测试 getUsername() 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/user/LoginUserTest.java`
- [ ] T052 [FR1] 测试 getRoles() 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/user/LoginUserTest.java`
- [ ] T053 [FR1] 测试 getPermissions() 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/user/LoginUserTest.java`
- [ ] T054 [FR1] 测试 hasRole() 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/user/LoginUserTest.java`
- [ ] T055 [FR1] 测试 hasPermission() 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/user/LoginUserTest.java`
- [ ] T056 [FR1] 测试 LoginUser 扩展性（创建扩展实现类）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/user/LoginUserTest.java`

### 权限注解测试

- [ ] T057 [P] [FR2] 创建 RequiresPermissionTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/annotation/RequiresPermissionTest.java`
- [ ] T058 [FR2] 测试 @RequiresPermission 注解属性（value、logical）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/annotation/RequiresPermissionTest.java`
- [ ] T059 [FR2] 测试 @RequiresPermission 注解元数据（@Target、@Retention）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/annotation/RequiresPermissionTest.java`
- [ ] T060 [FR2] 测试 @RequiresPermission 注解在类和方法上的使用在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/annotation/RequiresPermissionTest.java`
- [ ] T061 [P] [FR2] 创建 RequiresRoleTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/annotation/RequiresRoleTest.java`
- [ ] T062 [FR2] 测试 @RequiresRole 注解属性（value、logical）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/annotation/RequiresRoleTest.java`
- [ ] T063 [FR2] 测试 @RequiresRole 注解元数据（@Target、@Retention）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/annotation/RequiresRoleTest.java`
- [ ] T064 [FR2] 测试 @RequiresRole 注解在类和方法上的使用在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/annotation/RequiresRoleTest.java`
- [ ] T065 [P] [FR2] 创建 LogicalTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/annotation/LogicalTest.java`
- [ ] T066 [FR2] 测试 Logical 枚举值（AND、OR）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/annotation/LogicalTest.java`

### SecurityContext 接口测试

- [ ] T067 [P] [FR3] 创建 SecurityContextTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/context/SecurityContextTest.java`
- [ ] T068 [FR3] 创建测试实现类 ThreadLocalSecurityContext 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/context/SecurityContextTest.java`
- [ ] T069 [FR3] 测试 SecurityContext.getLoginUser() 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/context/SecurityContextTest.java`
- [ ] T070 [FR3] 测试 SecurityContext.isAuthenticated() 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/context/SecurityContextTest.java`
- [ ] T071 [FR3] 测试 SecurityContext.clear() 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/context/SecurityContextTest.java`

### SecurityContextHolder 测试

- [ ] T072 [P] [FR3] 创建 SecurityContextHolderTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/context/SecurityContextHolderTest.java`
- [ ] T073 [FR3] 测试 SecurityContextHolder.getContext() 静态方法（使用 Mock）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/context/SecurityContextHolderTest.java`
- [ ] T074 [FR3] 测试 SecurityContextHolder.getLoginUser() 静态方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/context/SecurityContextHolderTest.java`
- [ ] T075 [FR3] 测试 SecurityContextHolder.isAuthenticated() 静态方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-security/src/test/java/com/atlas/common/feature/security/context/SecurityContextHolderTest.java`

## Phase 4: 文档和示例

**目标**: 创建使用文档和示例代码

**独立测试标准**: 文档完整，示例代码清晰易懂

- [ ] T076 验证 quickstart.md 文档完整性 `/Users/liuhuan/workspace/project/java/backend/atlas/specs/3-security-feature/quickstart.md`
- [ ] T077 创建 LoginUser 使用示例代码在文档中
- [ ] T078 创建权限注解使用示例代码在文档中
- [ ] T079 创建 SecurityContext 使用示例代码在文档中
- [ ] T080 创建扩展 LoginUser 示例代码在文档中
- [ ] T081 验证所有代码通过 Spotless 格式化检查
- [ ] T082 验证所有代码通过 Maven Enforcer 检查
- [ ] T083 验证单元测试覆盖率 ≥ 80%

## 并行执行示例

### Phase 2 可并行任务

以下任务可以并行执行（处理不同的接口/注解文件）：

- T009, T017, T020, T026, T032, T037, T042, T045 可以并行（创建不同的接口/注解文件）
- T010-T016 需要在 T009 后顺序执行（LoginUser 接口方法定义）
- T021-T025 需要在 T020 后顺序执行（@RequiresPermission 注解属性）
- T027-T031 需要在 T026 后顺序执行（@RequiresRole 注解属性）
- T033-T036 需要在 T032 后顺序执行（SecurityContext 接口方法）
- T038-T041 需要在 T037 后顺序执行（SecurityContextHolder 方法）

### Phase 3 可并行任务

以下任务可以并行执行（测试不同的接口/注解）：

- T048, T057, T061, T065, T067, T072 可以并行（创建不同的测试类）
- T049-T056 需要在 T048 后顺序执行（LoginUser 测试）
- T058-T060 需要在 T057 后顺序执行（@RequiresPermission 测试）
- T062-T064 需要在 T061 后顺序执行（@RequiresRole 测试）
- T068-T071 需要在 T067 后顺序执行（SecurityContext 测试）
- T073-T075 需要在 T072 后顺序执行（SecurityContextHolder 测试）

## 代码审查检查点

- [ ] 所有接口和注解不包含实现细节
- [ ] 所有接口和方法添加中文注释
- [ ] 包名符合 `com.atlas.common.feature.security` 规范
- [ ] LoginUser 接口设计支持扩展
- [ ] 权限注解支持灵活的权限组合（AND/OR）
- [ ] SecurityContextHolder 设计支持多种实现方案
- [ ] 无具体安全框架依赖，保持抽象层纯净
- [ ] 单元测试覆盖率 ≥ 80%
- [ ] 所有代码通过 Spotless 格式化检查
- [ ] 所有代码通过 Maven Enforcer 检查

## 实施注意事项

- [ ] 确保所有接口和注解都是抽象的，不包含实现细节
- [ ] 确保 LoginUser 接口设计支持扩展
- [ ] 确保权限注解支持灵活的权限组合（AND/OR）
- [ ] 确保 SecurityContextHolder 设计支持多种实现方案
- [ ] 确保所有类和方法添加中文注释
- [ ] 遵循包名规范：`com.atlas.common.feature.security`
- [ ] 不引入具体的安全框架依赖，保持抽象层的独立性
- [ ] 接口方法签名要清晰，易于实现和使用

