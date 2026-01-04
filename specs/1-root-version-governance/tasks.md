# 任务清单

## 宪法合规检查清单

在开始实施前，确认以下事项：

- [ ] 技术栈版本符合宪法要求（JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0）
- [ ] 代码注释使用中文
- [ ] 所有配置和规范文档使用中文
- [ ] 父 POM 配置符合项目宪法要求

## 任务概览

**总任务数**: 45  
**阶段数**: 4  
**可并行任务**: 12

## 依赖关系

### 阶段完成顺序

1. **Phase 1: 项目初始化** → 必须先完成
2. **Phase 2: 父 POM 创建** → 依赖 Phase 1
3. **Phase 3: 工程规范文档** → 可与 Phase 2 并行（部分任务）
4. **Phase 4: 验证与测试** → 依赖 Phase 2 和 Phase 3

### 用户场景映射

- **场景1（开发人员创建新模块）**: 依赖 Phase 2 和 Phase 3 完成
- **场景2（配置管理员管理依赖版本）**: 依赖 Phase 2 完成
- **场景3（开发人员查看工程规范）**: 依赖 Phase 3 完成

## 实施策略

**MVP 范围**: Phase 1 + Phase 2（父 POM 创建）  
**增量交付**: 
1. 先完成父 POM，使子模块可以继承
2. 再完成工程规范文档，完善治理体系
3. 最后验证和测试，确保可用性

## Phase 1: 项目初始化

**目标**: 创建项目基础结构和目录

**独立测试标准**: 项目目录结构创建完成，可以开始创建父 POM

- [x] T001 创建项目根目录结构 `/Users/liuhuan/workspace/project/java/backend/atlas`
- [x] T002 创建文档目录 `/Users/liuhuan/workspace/project/java/backend/atlas/docs`
- [x] T003 创建工程规范文档目录 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards`
- [x] T004 创建治理文档目录 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/governance`
- [x] T005 创建 Maven Wrapper 配置目录 `/Users/liuhuan/workspace/project/java/backend/atlas/.mvn/wrapper`
- [x] T006 创建项目 README 文件 `/Users/liuhuan/workspace/project/java/backend/atlas/README.md`

## Phase 2: 父 POM 创建

**目标**: 创建根工程的父 POM 文件，统一管理所有子模块的构建配置

**独立测试标准**: 父 POM 可以独立构建（`mvn clean install`），所有插件配置正确，版本号符合宪法要求

### FR1: 父 POM 统一管理

- [x] T007 [FR1] 创建根 POM 文件 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [x] T008 [FR1] 配置项目基本信息（groupId、artifactId、version、packaging）在 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [x] T009 [FR1] 配置 Java 版本为 21 在 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [x] T010 [FR1] 配置字符编码为 UTF-8 在 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [x] T011 [FR1] 配置 Maven 编译版本和源代码版本为 21 在 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`

### FR2: 依赖版本管理

- [x] T012 [FR2] 配置 Spring Boot Parent (4.0.1) 在 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [x] T013 [FR2] 配置 Spring Cloud BOM (2025.1.0) 在 dependencyManagement 中 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [x] T014 [FR2] 配置 Spring Cloud Alibaba BOM (2025.1.0) 在 dependencyManagement 中 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`（注意：版本可能尚未发布，已添加注释说明）
- [x] T015 [FR2] 配置 MyBatis-Plus 版本管理在 dependencyManagement 中 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [x] T016 [FR2] 配置 PostgreSQL 驱动版本管理在 dependencyManagement 中 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [x] T017 [FR2] 配置其他常用依赖版本管理（如 Lombok、MapStruct 等）在 dependencyManagement 中 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`

### FR3: 插件管理

- [x] T018 [FR3] 配置 Maven Compiler Plugin（Java 21）在 pluginManagement 中 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [x] T019 [FR3] 配置 Maven Enforcer Plugin（强制 Java 版本和依赖版本一致性）在 pluginManagement 中 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [x] T020 [FR3] 配置 Maven Surefire Plugin（单元测试执行）在 pluginManagement 中 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [x] T021 [FR3] 配置 Maven Failsafe Plugin（集成测试执行）在 pluginManagement 中 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [x] T022 [FR3] 配置 Spotless Maven Plugin（代码格式化，使用 Google Java Format）在 pluginManagement 中 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [x] T023 [FR3] 配置统一属性（编码、版本号等）在 properties 中 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`
- [ ] T024 [FR3] 验证父 POM 构建成功 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`（待 Spring Cloud Alibaba 2025.1.0 版本发布后验证）

## Phase 3: 工程规范文档

**目标**: 创建完整的工程规范文档，定义包名、日志格式、错误码、配置命名等规范

**独立测试标准**: 所有规范文档完整，包含清晰的示例，规范可执行且无歧义

### FR4: 包名规范

- [x] T025 [P] [FR4] 创建包名规范文档 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/package-naming.md`
- [x] T026 [FR4] 定义根包名规范（com.atlas）在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/package-naming.md`
- [x] T027 [FR4] 定义业务模块包名结构规范在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/package-naming.md`
- [x] T028 [FR4] 定义公共模块包名结构规范在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/package-naming.md`
- [x] T029 [FR4] 添加包名规范示例代码在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/package-naming.md`

### FR5: 日志格式规范

- [x] T030 [P] [FR5] 创建日志格式规范文档 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/logging-format.md`
- [x] T031 [FR5] 定义日志格式模板（包含时间、级别、线程、类名、消息、TraceId）在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/logging-format.md`
- [x] T032 [FR5] 定义日志级别配置规范在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/logging-format.md`
- [x] T033 [FR5] 定义 TraceId 输出规范在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/logging-format.md`
- [x] T034 [FR5] 添加日志配置示例（logback-spring.xml）在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/logging-format.md`

### FR6: 错误码段位分配

- [x] T035 [P] [FR6] 创建错误码规范文档 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/error-code.md`
- [x] T036 [FR6] 定义错误码格式规范（6位数字：MMTTSS）在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/error-code.md`
- [x] T037 [FR6] 创建错误码段位分配表（模块码、类型码）在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/error-code.md`
- [x] T038 [FR6] 定义错误码常量类结构规范在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/error-code.md`
- [x] T039 [FR6] 添加错误码使用示例代码在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/error-code.md`

### FR7: 配置命名规范

- [x] T040 [P] [FR7] 创建配置命名规范文档 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/config-naming.md`
- [x] T041 [FR7] 定义 DataId 命名规则（{application-name}-{profile}.{extension}）在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/config-naming.md`
- [x] T042 [FR7] 定义 Group 命名规则（按环境分组）在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/config-naming.md`
- [x] T043 [FR7] 定义配置项 Key 命名规则（{module}.{category}.{key}）在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/config-naming.md`
- [x] T044 [FR7] 添加配置命名示例在 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards/config-naming.md`

## Phase 4: 验证与测试

**目标**: 验证父 POM 和规范的可用性，创建测试子模块和快速开始指南

**独立测试标准**: 测试子模块成功继承父 POM 配置，所有插件执行正常，规范文档通过团队评审

- [ ] T045 创建版本管理说明文档 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/governance/version-management.md`
- [ ] T046 创建测试子模块验证父 POM 继承 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-test-module/pom.xml`
- [ ] T047 验证测试子模块构建成功 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-test-module`
- [ ] T048 验证 Maven Enforcer Plugin 正常工作 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-test-module`
- [ ] T049 验证 Spotless Plugin 代码格式化功能 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-test-module`
- [ ] T050 验证依赖版本管理生效（检查子模块依赖版本与父 POM 一致） `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-test-module`
- [ ] T051 更新快速开始指南 `/Users/liuhuan/workspace/project/java/backend/atlas/specs/1-root-version-governance/quickstart.md`
- [ ] T052 进行规范文档团队评审 `/Users/liuhuan/workspace/project/java/backend/atlas/docs/engineering-standards`

## 并行执行示例

### Phase 3 可并行任务

以下任务可以并行执行（处理不同的文档文件）：

- T025, T030, T035, T040 可以并行（创建不同的规范文档）
- T026-T029, T031-T034, T036-T039, T041-T044 可以在各自文档创建后并行（编辑不同文档）

### Phase 2 可并行任务

以下任务可以并行执行（配置不同的插件）：

- T018-T022 可以并行（配置不同的 Maven 插件，都在同一个 pom.xml 文件中，但可以分步骤并行编辑）

## 代码审查检查点

- [ ] 父 POM 配置符合项目宪法要求（Java 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0）
- [ ] 所有配置和规范文档使用中文
- [ ] 包名规范与模块结构原则一致
- [ ] 错误码段位分配合理，预留扩展空间
- [ ] 配置命名规范支持多环境（dev、test、prod）
- [ ] 所有插件配置正确且可执行
- [ ] 依赖版本管理生效，无版本冲突
- [ ] 规范文档包含清晰的示例

## 实施注意事项

- [ ] 确保父 POM 配置符合项目宪法要求
- [ ] 所有配置和规范使用中文注释和文档
- [ ] 创建规范文档，便于团队查阅
- [ ] 使用 Maven Enforcer 插件强制规范执行
- [ ] 考虑向后兼容性，避免破坏现有模块（如果有）
- [ ] 在创建规范文档时，参考 research.md 中的技术决策
- [ ] 验证父 POM 构建时，确保所有插件正常工作
- [ ] 测试子模块应尽可能简单，仅用于验证父 POM 继承

