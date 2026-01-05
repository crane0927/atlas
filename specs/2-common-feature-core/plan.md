# 功能规划文档

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 提取公共方法，避免重复代码
- ✅ **模块化**: 遵循分层架构原则
- ✅ **包名规范**: 遵循 `com.atlas.common.feature.core` 包名结构
- ✅ **配置文件格式**: 使用 YAML 格式

**宪法合规性评估**:
- ✅ 所有技术栈版本符合宪法要求
- ✅ 模块归属 `atlas-common-feature-core`，符合模块化设计原则
- ✅ 提供可复用的公共类和工具，符合代码复用原则
- ✅ 所有代码将使用中文注释
- ✅ 遵循包名规范和错误码规范

## 功能概述

实现 `atlas-common-feature-core` 模块，提供项目核心功能特性，为所有业务模块提供统一的响应格式、错误处理和分页支持。该模块是项目的基础设施核心，确保：

1. **统一性**: 所有 API 响应使用统一的 Result 格式
2. **可维护性**: 错误码和异常体系清晰，便于维护和扩展
3. **易用性**: 提供便捷的静态方法和工厂方法，降低使用成本
4. **规范性**: 遵循项目错误码规范和包名规范

## 技术方案

### 架构设计

**模块结构**:
```
atlas/
└── atlas-common/              # 公共模块
    └── atlas-common-feature/  # 功能特性模块
        └── atlas-common-feature-core/  # 核心功能特性模块
            ├── pom.xml                    # 模块 POM
            ├── src/
            │   ├── main/java/
            │   │   └── com/atlas/common/feature/core/
            │   │       ├── result/        # Result 响应包装类
            │   │       │   └── Result.java
            │   │       ├── exception/     # 异常体系
            │   │       │   ├── BusinessException.java
            │   │       │   ├── ParameterException.java
            │   │       │   ├── PermissionException.java
            │   │       │   └── DataException.java
            │   │       ├── page/          # 分页对象
            │   │       │   └── PageResult.java
            │   │       ├── constant/      # 常量类
            │   │       │   ├── CommonErrorCode.java
            │   │       │   ├── HttpStatus.java
            │   │       │   └── CommonConstants.java
            │   │       └── util/          # 工具类（如有需要）
            │   └── test/java/             # 测试代码
            │       └── com/atlas/common/feature/core/
            │           ├── result/
            │           ├── exception/
            │           ├── page/
            │           └── constant/
```

**核心组件**:

1. **Result<T>**: 统一响应包装类
   - 支持泛型，可包装任意类型数据
   - 包含 code、message、data、timestamp 字段
   - 提供静态工厂方法（success、error）

2. **异常体系**: 业务异常类层次结构
   - BusinessException: 基础业务异常
   - ParameterException: 参数异常
   - PermissionException: 权限异常
   - DataException: 数据异常

3. **PageResult<T>**: 分页响应对象
   - 支持泛型，可封装任意类型列表
   - 包含 list、total、page、size、pages 字段
   - 自动计算总页数

4. **错误码常量**: CommonErrorCode
   - 定义通用错误码（模块码 05）
   - 包含系统错误、参数错误、业务错误、权限错误、数据错误

5. **基础常量**: HttpStatus、CommonConstants
   - HTTP 状态码常量
   - 常用字符串和数字常量

### 技术选型

**构建工具**: Maven 3.8+
- **理由**: 项目使用 Maven，继承父 POM 配置

**核心依赖**:
- **Spring Boot Web**: 提供 JSON 序列化支持（通过父 POM 管理）
- **Lombok**: 简化代码，减少样板代码（通过父 POM 管理）
- **Jackson**: JSON 序列化（Spring Boot 内置）

**技术决策**:
- 使用 Lombok 的 `@Data`、`@Builder` 等注解简化代码
- 使用 Jackson 的 `@JsonInclude` 控制序列化行为
- 使用 Java 泛型提供类型安全
- 使用静态工厂方法提供便捷的创建方式

## 实施计划

### 阶段 0: 研究与设计

**目标**: 完成技术调研和设计决策

**任务**:
1. 研究 Result 响应包装类的最佳实践
2. 研究异常体系设计模式
3. 研究分页对象的设计方案
4. 确认错误码分配方案（模块码 05）
5. 设计类的 API 和静态方法

**输出**: `research.md`

### 阶段 1: 核心类实现

**目标**: 实现核心功能类

**任务**:
1. 创建 Result<T> 类
   - 定义字段（code、message、data、timestamp）
   - 实现静态工厂方法（success、error）
   - 添加 JSON 序列化注解
   - 添加中文注释

2. 创建异常体系
   - 实现 BusinessException 基类
   - 实现 ParameterException、PermissionException、DataException
   - 添加错误码和消息支持
   - 添加中文注释

3. 创建 PageResult<T> 类
   - 定义字段（list、total、page、size、pages）
   - 实现构造方法和静态工厂方法
   - 实现分页计算逻辑
   - 添加中文注释

4. 创建错误码常量类
   - 实现 CommonErrorCode 类
   - 定义通用错误码常量
   - 添加错误消息映射
   - 添加中文注释

5. 创建基础常量类
   - 实现 HttpStatus 类（HTTP 状态码常量）
   - 实现 CommonConstants 类（通用常量）
   - 添加中文注释

**输出**: 所有核心类实现完成

### 阶段 2: 单元测试

**目标**: 编写完整的单元测试

**任务**:
1. 测试 Result 类
   - 测试成功响应创建
   - 测试失败响应创建
   - 测试泛型支持
   - 测试 JSON 序列化

2. 测试异常类
   - 测试异常创建和抛出
   - 测试错误码和消息
   - 测试异常链

3. 测试 PageResult 类
   - 测试分页计算逻辑
   - 测试边界情况（空列表、总数为 0 等）
   - 测试 JSON 序列化

4. 测试错误码常量类
   - 测试错误码格式
   - 测试错误消息映射

**输出**: 单元测试覆盖率 ≥ 80%

### 阶段 3: 文档和示例

**目标**: 创建使用文档和示例代码

**任务**:
1. 创建 quickstart.md 快速开始指南
2. 创建使用示例代码
3. 更新项目 README（如有需要）

**输出**: 完整的文档和示例

## 风险评估

### 风险 1: 泛型类型擦除问题

- **描述**: Java 泛型在运行时类型擦除，可能影响序列化
- **影响**: 低
- **应对**: 使用 Jackson 的 `@JsonTypeInfo` 注解或显式指定类型

### 风险 2: 错误码冲突

- **描述**: 错误码可能与业务模块冲突
- **影响**: 中
- **应对**: 严格按照错误码规范分配，模块码使用 05，类型码按规范分配

### 风险 3: 序列化性能问题

- **描述**: Result 包装可能增加序列化开销
- **影响**: 低
- **应对**: 使用 Jackson 的优化配置，必要时使用 `@JsonInclude(JsonInclude.Include.NON_NULL)`

### 风险 4: 向后兼容性

- **描述**: 未来修改 Result 结构可能影响现有代码
- **影响**: 中
- **应对**: 设计时考虑扩展性，使用 `@JsonIgnoreProperties(ignoreUnknown = true)` 支持向后兼容

## 验收标准

### 功能验收标准

- ✅ Result 类可以包装任意类型的数据
- ✅ 异常类可以正确抛出和捕获
- ✅ 分页对象计算逻辑正确
- ✅ 错误码常量符合项目规范
- ✅ 所有类包含完整的中文注释

### 质量验收标准

- ✅ 单元测试覆盖率 ≥ 80%
- ✅ 所有代码通过 Spotless 格式化检查
- ✅ 所有代码通过 Maven Enforcer 检查
- ✅ 包名符合 `com.atlas.common.feature.core` 规范

### 文档验收标准

- ✅ 快速开始指南完整
- ✅ 使用示例清晰易懂
- ✅ API 文档完整

## 依赖关系

### 内部依赖

- 依赖父 POM（atlas）的版本管理
- 依赖错误码规范文档的定义

### 外部依赖

- Spring Boot Web（用于序列化支持）
- Lombok（用于简化代码）
- Jackson（用于 JSON 序列化，Spring Boot 内置）

## 后续工作

1. 在其他模块中使用 Result 和异常体系
2. 实现全局异常处理器（在其他模块中）
3. 扩展错误码常量（根据业务需要）
4. 优化序列化性能（如有需要）

