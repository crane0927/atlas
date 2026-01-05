# 功能规划文档

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 提取公共方法，避免重复代码
- ✅ **模块化**: 遵循分层架构原则
- ✅ **包名规范**: 遵循 `com.atlas.common.feature.security` 包名结构
- ✅ **配置文件格式**: 使用 YAML 格式
- ✅ **抽象设计**: 先定义抽象接口，不绑定具体实现

**宪法合规性评估**:
- ✅ 所有技术栈版本符合宪法要求
- ✅ 模块归属 `atlas-common-feature-security`，符合模块化设计原则
- ✅ 提供可复用的安全抽象接口和注解，符合代码复用原则
- ✅ 所有代码将使用中文注释
- ✅ 遵循包名规范
- ✅ 采用抽象设计，不绑定具体实现，符合抽象设计原则

## 功能概述

实现 `atlas-common-feature-security` 模块，提供项目安全功能特性的抽象层，为所有业务模块提供统一的安全功能抽象。该模块采用抽象设计，定义接口和注解，不绑定具体实现，确保：

1. **抽象性**: 所有接口和注解定义清晰，不包含实现细节
2. **可扩展性**: 支持业务模块扩展和多种安全实现方案
3. **易用性**: 提供清晰的 API 设计，降低使用成本
4. **灵活性**: 支持不同安全框架的集成（Spring Security、Shiro 等）

## 技术方案

### 架构设计

**模块结构**:
```
atlas/
└── atlas-common/              # 公共模块
    └── atlas-common-feature/  # 功能特性模块
        └── atlas-common-feature-security/  # 安全功能特性模块
            ├── pom.xml                    # 模块 POM
            ├── src/
            │   ├── main/java/
            │   │   └── com/atlas/common/feature/security/
            │   │       ├── user/          # 登录用户模型
            │   │       │   └── LoginUser.java
            │   │       ├── annotation/    # 权限注解
            │   │       │   ├── RequiresPermission.java
            │   │       │   ├── RequiresRole.java
            │   │       │   └── Logical.java
            │   │       ├── context/       # 安全上下文接口
            │   │       │   ├── SecurityContext.java
            │   │       │   └── SecurityContextHolder.java
            │   │       └── exception/     # 安全异常（可选）
            │   │           ├── AuthenticationException.java
            │   │           └── AuthorizationException.java
            │   └── test/java/             # 测试代码
            │       └── com/atlas/common/feature/security/
            │           ├── user/
            │           ├── annotation/
            │           └── context/
```

**核心组件**:

1. **LoginUser 接口**: 登录用户信息模型
   - 定义用户基本信息获取方法（getUserId、getUsername）
   - 定义权限和角色获取方法（getRoles、getPermissions）
   - 定义权限判断方法（hasRole、hasPermission）
   - 支持业务模块扩展

2. **权限注解**: 权限声明注解
   - `@RequiresPermission`: 权限检查注解
   - `@RequiresRole`: 角色检查注解
   - `Logical`: 逻辑关系枚举（AND/OR）
   - 支持类级别和方法级别

3. **SecurityContext 接口**: 安全上下文接口
   - 提供获取当前登录用户的方法
   - 提供认证状态判断方法
   - 不包含设置方法（由实现类负责）

4. **SecurityContextHolder**: 安全上下文持有者
   - 提供静态方法获取安全上下文
   - 支持多种实现策略（ThreadLocal、Request Scope 等）
   - 不包含具体实现逻辑

### 技术选型

**构建工具**: Maven 3.8+
- **理由**: 项目使用 Maven，继承父 POM 配置

**核心依赖**:
- **无强制外部依赖**: 保持抽象层的纯净性
- **可选依赖**: 具体实现可以引入所需的安全框架

**技术决策**:
- 使用 Java 接口定义抽象层，不包含实现
- 使用 Java 注解定义权限元数据
- 使用枚举定义逻辑关系（AND/OR）
- 不引入任何具体安全框架依赖
- 保持接口设计的简洁性和可扩展性

## 实施计划

### 阶段 0: 研究与设计

**目标**: 完成技术调研和设计决策

**任务**:
1. 研究 Java 接口设计最佳实践
2. 研究权限注解设计模式
3. 研究安全上下文管理模式（ThreadLocal、Request Scope 等）
4. 研究 Spring Security、Shiro 等框架的抽象层设计
5. 设计接口 API 和方法签名
6. 设计注解属性和元数据

**输出**: `research.md`

### 阶段 1: 核心接口实现

**目标**: 实现核心抽象接口和注解

**任务**:
1. 创建 LoginUser 接口
   - 定义用户信息获取方法
   - 定义权限判断方法
   - 添加中文注释
   - 编写接口文档

2. 创建权限注解
   - 实现 @RequiresPermission 注解
   - 实现 @RequiresRole 注解
   - 实现 Logical 枚举
   - 添加中文注释
   - 编写使用文档

3. 创建 SecurityContext 接口
   - 定义获取登录用户方法
   - 定义认证状态判断方法
   - 添加中文注释

4. 创建 SecurityContextHolder 抽象类
   - 定义静态方法签名
   - 不包含具体实现
   - 添加中文注释

5. 创建安全异常接口（可选）
   - 定义 AuthenticationException 接口
   - 定义 AuthorizationException 接口
   - 添加中文注释

**输出**: 所有核心接口和注解实现完成

### 阶段 2: 单元测试

**目标**: 编写接口和注解的单元测试

**任务**:
1. 测试 LoginUser 接口
   - 创建测试实现类
   - 测试所有方法
   - 测试扩展性

2. 测试权限注解
   - 测试注解属性
   - 测试注解元数据
   - 测试注解组合

3. 测试 SecurityContext 接口
   - 创建测试实现类
   - 测试所有方法

4. 测试 SecurityContextHolder
   - 测试静态方法签名
   - 测试方法调用（使用 Mock）

**输出**: 单元测试覆盖率 ≥ 80%

### 阶段 3: 文档和示例

**目标**: 创建使用文档和示例代码

**任务**:
1. 创建 quickstart.md 快速开始指南
2. 创建接口使用示例代码
3. 创建扩展示例代码
4. 更新项目 README（如有需要）

**输出**: 完整的文档和示例

## 风险评估

### 风险 1: 接口设计不够灵活

- **描述**: 接口设计可能无法满足所有实现场景
- **影响**: 中
- **应对**: 参考 Spring Security、Shiro 等成熟框架的设计，确保接口设计的通用性

### 风险 2: 注解元数据不足

- **描述**: 注解可能缺少必要的元数据，影响后续实现
- **影响**: 中
- **应对**: 研究现有权限框架的注解设计，确保元数据完整

### 风险 3: 扩展性不足

- **描述**: LoginUser 接口可能无法满足业务模块的扩展需求
- **影响**: 中
- **应对**: 设计时考虑扩展性，提供扩展点或支持接口继承

### 风险 4: 与现有框架集成困难

- **描述**: 抽象接口可能与 Spring Security 等框架集成困难
- **影响**: 低
- **应对**: 设计时参考现有框架的抽象层，确保兼容性

## 验收标准

### 功能验收标准

- ✅ LoginUser 接口可以表示完整的用户身份信息
- ✅ 权限注解可以正确声明权限要求
- ✅ SecurityContext 接口可以获取当前登录用户
- ✅ SecurityContextHolder 提供便捷的静态方法
- ✅ 所有接口和注解包含完整的中文注释

### 质量验收标准

- ✅ 单元测试覆盖率 ≥ 80%
- ✅ 所有代码通过 Spotless 格式化检查
- ✅ 所有代码通过 Maven Enforcer 检查
- ✅ 包名符合 `com.atlas.common.feature.security` 规范
- ✅ 无具体实现代码，保持抽象层纯净

### 文档验收标准

- ✅ 快速开始指南完整
- ✅ 使用示例清晰易懂
- ✅ 接口文档完整
- ✅ 扩展指南清晰

## 依赖关系

### 内部依赖

- 依赖父 POM（atlas）的版本管理
- 依赖 `atlas-common-feature-core` 模块的异常体系（可选）

### 外部依赖

- 无强制外部依赖（保持抽象层的纯净性）
- 具体实现可以引入所需的安全框架（如 Spring Security、Shiro 等）

## 后续工作

1. 在其他模块中实现 SecurityContext 的具体实现
2. 实现权限检查框架（基于这些抽象接口）
3. 集成 Spring Security 或其他安全框架
4. 扩展 LoginUser 接口（根据业务需要）

