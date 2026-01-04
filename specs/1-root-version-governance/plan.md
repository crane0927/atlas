# 功能规划文档

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 提取公共方法，避免重复代码
- ✅ **模块化**: 遵循分层架构原则
- ✅ **版本管理**: 遵循语义化版本规范

**宪法合规性评估**:
- ✅ 所有技术栈版本符合宪法要求
- ✅ 父 POM 配置将统一管理版本，确保合规
- ✅ 工程规范将使用中文注释和文档

## 功能概述

建立 Atlas 项目的根工程和版本治理体系，为所有子模块提供统一的构建规范、依赖管理和工程约定。该功能是项目基础设施的核心，确保：

1. **统一性**: 所有模块使用相同的 Java 版本、依赖版本和构建配置
2. **可维护性**: 版本升级和配置修改只需在父 POM 中操作
3. **规范性**: 包名、日志、错误码、配置命名遵循统一规范
4. **可追溯性**: 所有规范文档化，便于团队查阅和执行

## 技术方案

### 架构设计

**根工程结构**:
```
atlas/
├── pom.xml                    # 父 POM（根工程）
├── README.md                  # 项目说明
├── .mvn/                      # Maven 配置
│   └── wrapper/              # Maven Wrapper
└── docs/                      # 文档目录
    ├── engineering-standards/ # 工程规范文档
    │   ├── package-naming.md # 包名规范
    │   ├── logging-format.md # 日志格式规范
    │   ├── error-code.md     # 错误码规范
    │   └── config-naming.md  # 配置命名规范
    └── governance/            # 治理文档
        └── version-management.md # 版本管理说明
```

**父 POM 职责**:
- 统一 Java 版本和编译配置
- 统一 Spring Boot/Cloud 版本管理
- 统一依赖版本管理（dependencyManagement）
- 统一插件配置（Enforcer、Surefire、Failsafe、Spotless/Checkstyle）
- 统一属性定义（编码、版本号等）

**工程规范体系**:
- 包名规范：定义模块包名结构和命名规则
- 日志格式规范：定义统一的日志输出格式和配置
- 错误码规范：定义错误码格式和段位分配规则
- 配置命名规范：定义 Nacos 配置中心的命名规则

### 技术选型

**构建工具**: Maven 3.8+
- **理由**: 项目使用 Maven，父 POM 是 Maven 标准机制

**插件选择**:
- **Maven Enforcer Plugin**: 强制依赖规则检查
- **Maven Surefire Plugin**: 单元测试执行
- **Maven Failsafe Plugin**: 集成测试执行
- **Spotless Maven Plugin**: 代码格式化（或 Checkstyle）
- **Maven Compiler Plugin**: Java 编译配置
- **Versions Maven Plugin**: 依赖版本检查（可选）

**版本管理**:
- Spring Cloud BOM: `spring-cloud-dependencies:2025.1.0`
- Spring Cloud Alibaba BOM: `spring-cloud-alibaba-dependencies:2025.1.0`
- Spring Boot Parent: `spring-boot-starter-parent:4.0.1`

**文档工具**:
- Markdown 格式的规范文档
- 可选的 Maven Site 生成（未来扩展）

## 实施计划

### 阶段 0: 研究与设计

**目标**: 完成技术调研和设计决策

**任务**:
1. 研究 Maven 父 POM 最佳实践
2. 研究 Spring Boot/Cloud 版本管理策略
3. 研究代码质量插件配置（Spotless vs Checkstyle）
4. 设计包名规范结构
5. 设计错误码段位分配方案
6. 设计配置命名规范

**输出**: `research.md`

### 阶段 1: 父 POM 创建

**目标**: 创建根工程的父 POM 文件

**任务**:
1. 创建根目录 `pom.xml`
2. 配置 Java 21 和编译设置
3. 配置 Spring Boot Parent (4.0.1)
4. 配置 Spring Cloud BOM (2025.1.0)
5. 配置 Spring Cloud Alibaba BOM (2025.1.0)
6. 配置常用依赖版本管理（MyBatis-Plus、PostgreSQL 驱动等）
7. 配置 Maven Enforcer Plugin
8. 配置 Maven Surefire Plugin
9. 配置 Maven Failsafe Plugin
10. 配置 Spotless/Checkstyle Plugin
11. 配置 Maven Compiler Plugin
12. 定义统一属性（编码 UTF-8、版本号等）

**验收标准**:
- 父 POM 可以独立构建（`mvn clean install`）
- 所有插件配置正确
- 版本号符合宪法要求

### 阶段 2: 工程规范文档

**目标**: 创建完整的工程规范文档

**任务**:
1. 创建包名规范文档
   - 定义根包名：`com.atlas`
   - 定义模块包名结构
   - 定义公共模块包名结构
   - 提供示例
2. 创建日志格式规范文档
   - 定义日志格式模板
   - 定义日志级别配置
   - 定义 TraceId 输出规范
   - 提供配置示例
3. 创建错误码规范文档
   - 定义错误码格式（模块码 + 类型码 + 序号）
   - 分配各模块错误码段位
   - 定义错误码常量类结构
   - 提供示例
4. 创建配置命名规范文档
   - 定义 DataId 命名规则
   - 定义 Group 命名规则
   - 定义配置项 key 命名规则
   - 提供示例

**验收标准**:
- 所有规范文档完整
- 文档包含清晰的示例
- 规范可执行且无歧义

### 阶段 3: 验证与测试

**目标**: 验证父 POM 和规范的可用性

**任务**:
1. 创建测试子模块验证父 POM 继承
2. 验证所有插件正常工作
3. 验证依赖版本管理生效
4. 验证规范文档的完整性
5. 创建快速开始指南（quickstart.md）

**验收标准**:
- 测试子模块成功继承父 POM 配置
- 所有插件执行正常
- 规范文档通过团队评审

## 技术上下文

### 已知信息
- 项目使用 Maven 作为构建工具
- Java 版本：21（LTS）
- Spring Boot 版本：4.0.1
- Spring Cloud 版本：2025.1.0
- Spring Cloud Alibaba 版本：2025.1.0
- 数据库：PostgreSQL
- 配置中心：Nacos
- 代码访问层：MyBatis-Plus

### 需要澄清的问题

**Q1: Spotless vs Checkstyle**
- **问题**: 选择 Spotless 还是 Checkstyle 进行代码格式化？
- **建议**: 
  - Spotless: 自动格式化，配置简单，支持多种格式化工具
  - Checkstyle: 更灵活，可自定义规则，但需要更多配置
- **推荐**: Spotless（更符合"约定优于配置"原则）

**Q2: 错误码段位分配**
- **问题**: 如何分配各模块的错误码段位？需要预留多少空间？
- **建议**: 
  - 使用 6 位数字：前 2 位模块码，中间 2 位类型码，后 2 位序号
  - 每个模块分配 1000 个错误码空间（如 010000-019999）
- **需要确认**: 模块数量和错误码需求

**Q3: 配置命名策略**
- **问题**: Nacos DataId 和 Group 的命名策略？
- **建议**:
  - DataId: `{application-name}-{profile}.{extension}` (如 `atlas-system-dev.yaml`)
  - Group: `DEFAULT_GROUP` 或按环境分组（如 `DEV_GROUP`、`PROD_GROUP`）
- **需要确认**: 是否支持多环境配置

## 风险评估

### 风险 1: 版本兼容性问题
- **描述**: Spring Boot 4.0.1 和 Spring Cloud 2025.1.0 可能存在兼容性问题
- **影响**: 高
- **应对**: 
  - 查阅官方兼容性矩阵
  - 创建测试项目验证
  - 准备降级方案

### 风险 2: 插件配置冲突
- **描述**: 多个插件可能产生配置冲突
- **影响**: 中
- **应对**:
  - 逐步添加插件并测试
  - 查阅插件文档避免冲突
  - 使用 Maven Enforcer 检测冲突

### 风险 3: 规范执行困难
- **描述**: 团队可能不遵循规范
- **影响**: 中
- **应对**:
  - 使用工具强制检查（Enforcer、Checkstyle）
  - 提供清晰的文档和示例
  - 代码审查时检查规范遵循情况

### 风险 4: 错误码段位不足
- **描述**: 错误码段位分配可能不够用
- **影响**: 低
- **应对**:
  - 预留足够的错误码空间
  - 设计可扩展的错误码格式
  - 建立错误码分配登记机制

## 验收标准

### 父 POM 验收标准
- ✅ 所有子模块可以继承父 POM 并成功构建
- ✅ Java 版本统一为 21
- ✅ Spring Boot/Cloud 版本统一且符合宪法要求
- ✅ 所有插件配置正确且可执行
- ✅ 依赖版本管理生效，无版本冲突

### 工程规范验收标准
- ✅ 包名规范文档完整，包含示例
- ✅ 日志格式规范文档完整，包含配置示例
- ✅ 错误码规范文档完整，包含段位分配表
- ✅ 配置命名规范文档完整，包含命名示例
- ✅ 所有规范通过团队评审

### 整体验收标准
- ✅ 新成员可在 30 分钟内理解并应用规范
- ✅ 版本升级效率提升 90% 以上（单点修改 vs 多点修改）
- ✅ 规范遵循率达到 100%（通过工具检查）

## 依赖关系

### 外部依赖
- 无（这是基础设施功能）

### 内部依赖
- 需要项目宪法文档作为约束参考
- 需要了解现有模块结构（如果有）

## 后续工作

1. 创建示例子模块验证父 POM
2. 集成到 CI/CD 流水线
3. 建立规范审查机制
4. 定期更新依赖版本
5. 扩展更多工程规范（如 API 文档规范、测试规范等）

