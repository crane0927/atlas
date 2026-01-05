# 功能规划文档

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0
- ✅ **API 设计**: 遵循 RESTful 风格
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 提取公共方法，避免重复代码
- ✅ **模块化**: 遵循分层架构原则
- ✅ **包名规范**: 遵循 `com.atlas.common.infra.web` 包名结构
- ✅ **配置文件格式**: 使用 YAML 格式

**宪法合规性评估**:
- ✅ 所有技术栈版本符合宪法要求
- ✅ 模块归属 `atlas-common-infra-web`，符合模块化设计原则
- ✅ 提供可复用的 Web 基础设施组件，符合代码复用原则
- ✅ 所有代码将使用中文注释
- ✅ 遵循包名规范
- ✅ 复用 `atlas-common-feature-core` 模块的 `Result` 类和异常类
- ✅ 复用 `atlas-common-infra-logging` 模块的 `TraceIdUtil` 工具类

## 功能概述

实现 `atlas-common-infra-web` 模块，提供 Web 基础设施功能，为所有业务模块提供统一的异常处理、参数校验、JSON 序列化和 TraceId 管理能力。该模块确保：

1. **统一性**: 所有异常响应使用统一的 `Result` 格式
2. **完整性**: 所有异常类型都可以正确处理
3. **易用性**: 开发人员无需在每个 Controller 中编写异常处理代码
4. **可维护性**: 异常处理逻辑集中管理
5. **可追溯性**: 所有异常响应包含 TraceId

## 技术方案

### 架构设计

**模块结构**:
```
atlas/
└── atlas-common/              # 公共模块
    └── atlas-common-infra/    # 基础设施模块
        └── atlas-common-infra-web/  # Web 基础设施模块
            ├── pom.xml                    # 模块 POM
            ├── README.md                  # 模块文档
            ├── src/
            │   ├── main/java/
            │   │   └── com/atlas/common/infra/web/
            │   │       ├── exception/     # 全局异常处理
            │   │       │   ├── GlobalExceptionHandler.java
            │   │       │   └── ValidationError.java
            │   │       ├── config/        # Jackson 配置
            │   │       │   └── JacksonConfig.java
            │   │       ├── filter/        # TraceId Filter
            │   │       │   └── TraceIdFilter.java
            │   │       └── serializer/   # 自定义序列化器
            │   │           └── LongToStringSerializer.java
            │   └── test/java/             # 测试代码
            │       └── com/atlas/common/infra/web/
            │           ├── exception/
            │           ├── config/
            │           ├── filter/
            │           └── serializer/
```

**核心组件**:

1. **全局异常处理器**: `GlobalExceptionHandler`
   - 使用 `@RestControllerAdvice` 注解
   - 处理业务异常、参数校验异常、Spring MVC 异常、系统异常
   - 统一返回 `Result` 格式响应
   - 自动注入 TraceId
   - 记录异常日志

2. **参数校验错误处理**: `ValidationError`
   - 封装字段错误信息（字段名、错误消息）
   - 格式化校验错误响应

3. **Jackson 配置**: `JacksonConfig`
   - 配置 `ObjectMapper` Bean
   - 统一日期时间格式（ISO-8601）
   - 配置空值处理策略
   - 配置序列化和反序列化特性
   - 配置时区处理
   - 注册自定义序列化器

4. **TraceId Filter**: `TraceIdFilter`
   - 实现 `Filter` 接口
   - 从请求头获取或生成 TraceId
   - 设置 TraceId 到 `TraceIdUtil`
   - 可选地在响应头中添加 TraceId
   - 配置 Filter 执行顺序

5. **自定义序列化器**: `LongToStringSerializer`
   - 将 Long 类型序列化为 String，避免精度丢失
   - 用于前端 JavaScript 处理大整数

### 技术选型

**构建工具**: Maven 3.8+
- **理由**: 项目使用 Maven，继承父 POM 配置

**核心依赖**:
- **spring-boot-starter-web**: Web 相关功能（Spring Boot 内置）
- **spring-boot-starter-validation**: 参数校验功能（Spring Boot 内置）
- **jackson-databind**: Jackson JSON 序列化（Spring Boot 内置）
- **atlas-common-feature-core**: 依赖 `Result` 类和异常类
- **atlas-common-infra-logging**: 依赖 `TraceIdUtil` 工具类（可选）

**技术决策**:

1. **全局异常处理**: 使用 Spring Boot 的 `@RestControllerAdvice` 注解
   - **理由**: Spring Boot 官方推荐方式，简单易用，无需额外配置

2. **参数校验**: 使用 Spring Validation（Bean Validation）
   - **理由**: Spring Boot 内置支持，与 `@Valid`、`@Validated` 注解集成良好

3. **Jackson 配置**: 使用 `@Configuration` 配置类
   - **理由**: Spring Boot 自动配置机制，可以覆盖默认配置

4. **TraceId Filter**: 使用 Servlet `Filter` 接口
   - **理由**: Filter 在请求的最早阶段执行，确保 TraceId 在所有组件之前设置
   - **注意**: 与 `atlas-common-infra-logging` 模块的 `TraceIdInterceptor` 功能重复，但 Filter 执行更早，可以确保 TraceId 在所有场景下都能正确设置

## 实施计划

### 阶段 1: 项目初始化和基础配置

**目标**: 创建模块结构，配置依赖，搭建基础框架

**任务**:
1. 创建 `atlas-common-infra-web` 模块目录结构
2. 创建 `pom.xml`，配置依赖（spring-boot-starter-web、spring-boot-starter-validation、atlas-common-feature-core、atlas-common-infra-logging）
3. 创建包结构（exception、config、filter、serializer）
4. 创建 `README.md` 文档

**验收标准**:
- 模块结构创建完成
- 依赖配置正确
- 包结构符合规范

### 阶段 2: 全局异常处理器实现

**目标**: 实现全局异常处理器，统一处理所有异常

**任务**:
1. 创建 `GlobalExceptionHandler` 类
2. 实现业务异常处理方法（BusinessException、ParameterException、PermissionException、DataException）
3. 实现参数校验异常处理方法（MethodArgumentNotValidException、ConstraintViolationException）
4. 实现 Spring MVC 异常处理方法（HttpRequestMethodNotSupportedException、HttpMediaTypeNotSupportedException、MissingServletRequestParameterException 等）
5. 实现系统异常处理方法（Exception、RuntimeException）
6. 实现异常日志记录功能
7. 创建 `ValidationError` 类，封装字段错误信息
8. 编写单元测试

**验收标准**:
- 所有异常类型都可以正确处理
- 异常响应格式符合 `Result` 规范
- 异常响应包含 TraceId
- 异常日志可以正确记录
- 单元测试覆盖率 ≥ 80%

### 阶段 3: Jackson 配置实现

**目标**: 实现统一的 Jackson JSON 序列化配置

**任务**:
1. 创建 `JacksonConfig` 配置类
2. 配置 `ObjectMapper` Bean
3. 配置日期时间格式（ISO-8601）
4. 配置空值处理策略（忽略 null 值）
5. 配置序列化特性（忽略空值、格式化输出等）
6. 配置反序列化特性（忽略未知属性、大小写不敏感等）
7. 配置时区处理（使用系统默认时区）
8. 创建 `LongToStringSerializer` 自定义序列化器
9. 注册自定义序列化器
10. 编写单元测试

**验收标准**:
- 日期时间可以统一格式化
- 空值可以统一处理
- JSON 序列化特性符合项目规范
- JSON 反序列化特性符合项目规范
- 时区可以正确处理
- 自定义序列化器可以正常工作
- 单元测试覆盖率 ≥ 80%

### 阶段 4: TraceId Filter 实现

**目标**: 实现 TraceId Filter，在请求的最早阶段设置 TraceId

**任务**:
1. 创建 `TraceIdFilter` 类，实现 `Filter` 接口
2. 实现 `doFilter` 方法，从请求头获取或生成 TraceId
3. 调用 `TraceIdUtil.setTraceId()` 设置 TraceId
4. 可选地在响应头中添加 TraceId
5. 创建 `FilterRegistrationBean` 配置类，配置 Filter 执行顺序和 URL 匹配模式
6. 编写单元测试

**验收标准**:
- HTTP 请求可以自动获取或生成 TraceId
- TraceId 可以正确设置到 `TraceIdUtil`
- TraceId 可以正确输出到日志中（通过 MDC）
- TraceId 可以在响应头中返回（可选）
- Filter 执行顺序正确
- Filter 可以配置 URL 匹配模式
- 单元测试覆盖率 ≥ 80%

### 阶段 5: 文档和示例

**目标**: 完善文档，提供使用示例

**任务**:
1. 完善 `README.md`，添加使用说明和示例
2. 创建 `quickstart.md`，提供快速开始指南
3. 创建 `data-model.md`，定义数据模型
4. 创建 `contracts/README.md`，定义 API 合约
5. 创建 `research.md`，记录技术决策

**验收标准**:
- 文档完整清晰
- 使用示例可以正常运行
- 数据模型定义完整
- API 合约定义完整

## 风险评估

### 风险 1: TraceId Filter 与 TraceIdInterceptor 功能重复

- **描述**: `atlas-common-infra-logging` 模块已经提供了 `TraceIdInterceptor`，本模块的 `TraceIdFilter` 功能重复
- **影响**: 中
- **应对**: 
  - Filter 执行顺序早于 Interceptor，可以确保 TraceId 在所有场景下都能正确设置
  - 如果业务模块同时使用 Filter 和 Interceptor，需要确保它们不会冲突
  - 在文档中说明使用建议：优先使用 Filter，Interceptor 作为备选方案

### 风险 2: 全局异常处理器可能影响现有代码

- **描述**: 全局异常处理器可能改变现有异常处理行为
- **影响**: 中
- **应对**: 
  - 确保异常处理逻辑与现有代码兼容
  - 提供配置选项，允许业务模块自定义异常处理行为
  - 在文档中说明异常处理优先级和覆盖规则

### 风险 3: Jackson 配置可能影响现有序列化行为

- **描述**: 统一的 Jackson 配置可能改变现有序列化行为
- **影响**: 低
- **应对**: 
  - 确保 Jackson 配置与现有代码兼容
  - 提供配置选项，允许业务模块覆盖默认配置
  - 在文档中说明配置优先级和覆盖规则

### 风险 4: 参数校验错误格式可能与前端不兼容

- **描述**: 参数校验错误响应格式可能与前端期望的格式不一致
- **影响**: 低
- **应对**: 
  - 参考前端框架的常见错误格式
  - 提供配置选项，允许自定义错误格式
  - 在文档中说明错误格式规范

## 验收标准

### 功能验收标准

- ✅ 全局异常处理器可以统一处理所有异常类型
- ✅ 参数校验异常可以统一处理并返回标准格式
- ✅ Jackson 配置可以统一 JSON 序列化格式
- ✅ TraceId Filter 可以自动设置 TraceId
- ✅ 所有组件包含完整的中文注释

### 质量验收标准

- ✅ 单元测试覆盖率 ≥ 80%
- ✅ 所有代码通过 Spotless 格式化检查
- ✅ 所有代码通过 Maven Enforcer 检查
- ✅ 包名符合 `com.atlas.common.infra.web` 规范

### 文档验收标准

- ✅ README.md 完整清晰
- ✅ 快速开始指南完整
- ✅ 使用示例清晰易懂
- ✅ 数据模型定义完整
- ✅ API 合约定义完整

## 依赖关系

### 内部依赖

- **atlas-common-feature-core**: 依赖 `Result` 类和异常类（BusinessException、ParameterException、PermissionException、DataException）
- **atlas-common-infra-logging**: 依赖 `TraceIdUtil` 工具类（可选，如果使用 TraceId Filter）

### 外部依赖

- **spring-boot-starter-web**: Web 相关功能（Spring Boot 内置）
- **spring-boot-starter-validation**: 参数校验功能（Spring Boot 内置）
- **jackson-databind**: Jackson JSON 序列化（Spring Boot 内置）

## 后续工作

1. 在其他业务模块中使用全局异常处理器
2. 根据业务需要扩展异常处理逻辑
3. 优化 Jackson 配置性能（如有需要）
4. 扩展自定义序列化器（如有需要）
