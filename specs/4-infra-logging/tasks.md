# 任务清单

## 宪法合规检查清单

在开始实施前，确认以下事项：

- [ ] 技术栈版本符合宪法要求（JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0）
- [ ] 代码注释使用中文
- [ ] 包名规范遵循 `com.atlas.common.infra.logging`
- [ ] 日志配置使用 XML 格式（符合 Logback 规范）
- [ ] 配置文件使用 YAML 格式（应用配置）

## 任务概览

**总任务数**: 78  
**阶段数**: 6  
**可并行任务**: 28

## 依赖关系

### 阶段完成顺序

1. **Phase 1: 项目初始化** → 必须先完成
2. **Phase 2: FR1 - Logback 日志配置规范** → 依赖 Phase 1
3. **Phase 3: FR2 - TraceId 自动注入和管理** → 依赖 Phase 1
4. **Phase 4: FR3 - 敏感信息脱敏工具** → 依赖 Phase 1
5. **Phase 5: 单元测试** → 依赖 Phase 2, 3, 4
6. **Phase 6: 文档和示例** → 依赖 Phase 2, 3, 4

### 功能需求映射

- **FR1（Logback 日志配置规范）**: Phase 2 实现，Phase 5 测试
- **FR2（TraceId 自动注入和管理）**: Phase 3 实现，Phase 5 测试
- **FR3（敏感信息脱敏工具）**: Phase 4 实现，Phase 5 测试

## 实施策略

**MVP 范围**: Phase 1 + Phase 2 + Phase 3（项目初始化、日志配置、TraceId 管理）  
**增量交付**: 
1. 先完成项目初始化和日志配置模板
2. 再完成 TraceId 管理功能，支持链路追踪
3. 然后完成脱敏工具，保护敏感信息
4. 最后完成单元测试和文档

## Phase 1: 项目初始化

**目标**: 创建模块基础结构和配置

**独立测试标准**: 模块目录结构创建完成，POM 配置正确，可以开始实现核心功能

- [x] T001 创建模块目录结构 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging`
- [x] T002 创建源代码目录 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging`
- [x] T003 创建测试代码目录 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging`
- [x] T004 创建资源目录 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/resources/com/atlas/common/infra/logging/config`
- [x] T005 创建模块 POM 文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/pom.xml`
- [x] T006 [FR1-FR3] 配置模块基本信息（groupId、artifactId、version、packaging）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/pom.xml`
- [x] T007 [FR1-FR3] 配置继承父 POM 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/pom.xml`
- [x] T008 [FR2] 添加 Spring Boot Web 依赖（用于 HTTP 拦截器）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/pom.xml`
- [x] T009 [FR2] 添加 Spring Cloud OpenFeign 依赖（可选，用于 Feign 拦截器）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/pom.xml`
- [x] T010 将模块添加到父 POM 的 modules 列表在 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`

## Phase 2: FR1 - Logback 日志配置规范

**目标**: 实现标准的 Logback 日志配置模板

**独立测试标准**: 日志配置模板创建完成，包含统一日志格式、控制台和文件输出、日志轮转、环境特定配置，配置包含完整的中文注释

- [x] T011 [FR1] 创建 logback-default.xml 配置模板文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/resources/com/atlas/common/infra/logging/config/logback-default.xml`
- [x] T012 [FR1] 定义日志格式属性 LOG_PATTERN（包含时间戳、线程、级别、Logger、TraceId、消息）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/resources/com/atlas/common/infra/logging/config/logback-default.xml`
- [x] T013 [FR1] 配置控制台输出 appender（CONSOLE）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/resources/com/atlas/common/infra/logging/config/logback-default.xml`
- [x] T014 [FR1] 配置文件输出 appender（FILE，支持按时间和大小轮转）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/resources/com/atlas/common/infra/logging/config/logback-default.xml`
- [x] T015 [FR1] 配置错误日志单独输出 appender（ERROR_FILE）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/resources/com/atlas/common/infra/logging/config/logback-default.xml`
- [x] T016 [FR1] 配置日志轮转策略（按时间：每天，按大小：100MB）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/resources/com/atlas/common/infra/logging/config/logback-default.xml`
- [x] T017 [FR1] 配置日志文件保留策略（普通日志30天，错误日志90天）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/resources/com/atlas/common/infra/logging/config/logback-default.xml`
- [x] T018 [FR1] 配置环境特定日志级别（dev、test、prod）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/resources/com/atlas/common/infra/logging/config/logback-default.xml`
- [x] T019 [FR1] 添加完整的中文注释（配置说明、参数说明）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/resources/com/atlas/common/infra/logging/config/logback-default.xml`

## Phase 3: FR2 - TraceId 自动注入和管理

**目标**: 实现 TraceId 的生成、存储、传递和管理功能

**独立测试标准**: TraceId 工具类、生成器、拦截器实现完成，可以自动生成、传递和清理 TraceId，支持 HTTP、Feign 和异步任务

### TraceId 工具类和生成器

- [x] T020 [P] [FR2] 创建 TraceIdGenerator 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdGenerator.java`
- [x] T021 [FR2] 实现 generateUUID() 方法（生成32位UUID，去除连字符）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdGenerator.java`
- [x] T022 [FR2] 实现 generateSnowflake() 方法（使用雪花算法生成TraceId）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdGenerator.java`
- [x] T023 [FR2] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdGenerator.java`
- [x] T024 [P] [FR2] 创建 TraceIdUtil 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdUtil.java`
- [x] T025 [FR2] 定义 ThreadLocal 存储字段在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdUtil.java`
- [x] T026 [FR2] 实现 setTraceId(String traceId) 方法（同时设置 ThreadLocal 和 MDC）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdUtil.java`
- [x] T027 [FR2] 实现 getTraceId() 方法（优先从 ThreadLocal 获取，如果为空则从 MDC 获取）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdUtil.java`
- [x] T028 [FR2] 实现 clear() 方法（同时清理 ThreadLocal 和 MDC）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdUtil.java`
- [x] T029 [FR2] 实现 generate() 方法（生成新的 TraceId）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdUtil.java`
- [x] T030 [FR2] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdUtil.java`

### TraceId 拦截器

- [x] T031 [P] [FR2] 创建 TraceIdInterceptor 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdInterceptor.java`
- [x] T032 [FR2] 实现 HandlerInterceptor 接口在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdInterceptor.java`
- [x] T033 [FR2] 实现 preHandle() 方法（从请求头获取或生成 TraceId，设置到 ThreadLocal 和 MDC）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdInterceptor.java`
- [x] T034 [FR2] 实现 afterCompletion() 方法（清理 TraceId）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdInterceptor.java`
- [x] T035 [FR2] 配置 TraceId 请求头名称（默认 X-Trace-Id）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdInterceptor.java`
- [x] T036 [FR2] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdInterceptor.java`
- [x] T037 [P] [FR2] 创建 TraceIdFeignInterceptor 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdFeignInterceptor.java`
- [x] T038 [FR2] 实现 RequestInterceptor 接口在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdFeignInterceptor.java`
- [x] T039 [FR2] 实现 apply() 方法（从 TraceIdUtil 获取 TraceId，添加到请求头）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdFeignInterceptor.java`
- [x] T040 [FR2] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/trace/TraceIdFeignInterceptor.java`
- [x] T041 [P] [FR2] 创建 TraceIdTaskDecorator 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/async/TraceIdTaskDecorator.java`
- [x] T042 [FR2] 实现 TaskDecorator 接口在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/async/TraceIdTaskDecorator.java`
- [x] T043 [FR2] 实现 decorate() 方法（继承父线程的 TraceId，在新线程中设置）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/async/TraceIdTaskDecorator.java`
- [x] T044 [FR2] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/async/TraceIdTaskDecorator.java`

## Phase 4: FR3 - 敏感信息脱敏工具

**目标**: 实现敏感信息脱敏工具类和拦截器

**独立测试标准**: 脱敏工具类实现完成，支持常见敏感字段脱敏，支持自定义脱敏规则，日志拦截器可以自动脱敏

### 脱敏工具类

- [x] T045 [P] [FR3] 创建 DesensitizeUtil 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtil.java`
- [x] T046 [FR3] 实现 maskPhone(String phone) 方法（保留前3位和后4位）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtil.java`
- [x] T047 [FR3] 实现 maskIdCard(String idCard) 方法（保留前6位和后4位）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtil.java`
- [x] T048 [FR3] 实现 maskBankCard(String bankCard) 方法（保留后4位）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtil.java`
- [x] T049 [FR3] 实现 maskEmail(String email) 方法（保留用户名前2位和域名）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtil.java`
- [x] T050 [FR3] 实现 maskPassword(String password) 方法（全部替换为******）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtil.java`
- [x] T051 [FR3] 实现 mask(String value, DesensitizeRule rule) 通用脱敏方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtil.java`
- [x] T052 [FR3] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtil.java`
- [x] T053 [P] [FR3] 创建 DesensitizeRule 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeRule.java`
- [x] T054 [FR3] 定义 DesensitizeRule 字段（fieldType、pattern、prefixLength、suffixLength、replacement）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeRule.java`
- [x] T055 [FR3] 添加 Lombok 注解（@Data、@Builder）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeRule.java`
- [x] T056 [FR3] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeRule.java`
- [x] T057 [P] [FR3] 创建 @Sensitive 注解文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/annotation/Sensitive.java`
- [x] T058 [FR3] 定义 @Sensitive 注解的 type 属性（SensitiveType）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/annotation/Sensitive.java`
- [x] T059 [FR3] 定义 @Sensitive 注解的 prefixLength 和 suffixLength 属性在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/annotation/Sensitive.java`
- [x] T060 [FR3] 配置 @Sensitive 注解的 @Target（FIELD）和 @Retention（RUNTIME）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/annotation/Sensitive.java`
- [x] T061 [FR3] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/annotation/Sensitive.java`
- [x] T062 [P] [FR3] 创建 SensitiveType 枚举文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/annotation/SensitiveType.java`
- [x] T063 [FR3] 定义 SensitiveType 枚举值（PHONE、ID_CARD、BANK_CARD、EMAIL、PASSWORD、CUSTOM）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/annotation/SensitiveType.java`
- [x] T064 [FR3] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/annotation/SensitiveType.java`

### 脱敏拦截器

- [x] T065 [P] [FR3] 创建 DesensitizeInterceptor 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeInterceptor.java`
- [x] T066 [FR3] 实现日志消息脱敏逻辑（检测敏感信息模式，应用脱敏规则）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeInterceptor.java`
- [x] T067 [FR3] 实现对象字段自动脱敏逻辑（通过反射和 @Sensitive 注解）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeInterceptor.java`
- [x] T068 [FR3] 支持自定义脱敏规则配置在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeInterceptor.java`
- [x] T069 [FR3] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/main/java/com/atlas/common/infra/logging/desensitize/DesensitizeInterceptor.java`

## Phase 5: 单元测试

**目标**: 编写完整的单元测试，确保代码质量

**独立测试标准**: 单元测试覆盖率 ≥ 80%，所有测试用例通过

### TraceId 工具类测试

- [ ] T070 [P] [FR2] 创建 TraceIdGeneratorTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdGeneratorTest.java`
- [ ] T071 [FR2] 测试 generateUUID() 方法（生成32位UUID，去除连字符）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdGeneratorTest.java`
- [ ] T072 [FR2] 测试 generateSnowflake() 方法（生成雪花算法ID）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdGeneratorTest.java`
- [ ] T073 [P] [FR2] 创建 TraceIdUtilTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdUtilTest.java`
- [ ] T074 [FR2] 测试 setTraceId() 方法（设置 ThreadLocal 和 MDC）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdUtilTest.java`
- [ ] T075 [FR2] 测试 getTraceId() 方法（优先从 ThreadLocal 获取）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdUtilTest.java`
- [ ] T076 [FR2] 测试 clear() 方法（清理 ThreadLocal 和 MDC）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdUtilTest.java`
- [ ] T077 [FR2] 测试 generate() 方法（生成新的 TraceId）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdUtilTest.java`
- [ ] T078 [FR2] 测试线程隔离性（不同线程的 TraceId 互不影响）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdUtilTest.java`

### TraceId 拦截器测试

- [ ] T079 [P] [FR2] 创建 TraceIdInterceptorTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdInterceptorTest.java`
- [ ] T080 [FR2] 测试 preHandle() 方法（从请求头获取 TraceId）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdInterceptorTest.java`
- [ ] T081 [FR2] 测试 preHandle() 方法（请求头无 TraceId 时自动生成）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdInterceptorTest.java`
- [ ] T082 [FR2] 测试 afterCompletion() 方法（清理 TraceId）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdInterceptorTest.java`
- [ ] T083 [P] [FR2] 创建 TraceIdFeignInterceptorTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdFeignInterceptorTest.java`
- [ ] T084 [FR2] 测试 apply() 方法（将 TraceId 添加到 Feign 请求头）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/trace/TraceIdFeignInterceptorTest.java`
- [ ] T085 [P] [FR2] 创建 TraceIdTaskDecoratorTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/async/TraceIdTaskDecoratorTest.java`
- [ ] T086 [FR2] 测试 decorate() 方法（异步任务继承父线程的 TraceId）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/async/TraceIdTaskDecoratorTest.java`

### 脱敏工具测试

- [ ] T087 [P] [FR3] 创建 DesensitizeUtilTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtilTest.java`
- [ ] T088 [FR3] 测试 maskPhone() 方法（手机号脱敏）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtilTest.java`
- [ ] T089 [FR3] 测试 maskIdCard() 方法（身份证号脱敏）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtilTest.java`
- [ ] T090 [FR3] 测试 maskBankCard() 方法（银行卡号脱敏）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtilTest.java`
- [ ] T091 [FR3] 测试 maskEmail() 方法（邮箱脱敏）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtilTest.java`
- [ ] T092 [FR3] 测试 maskPassword() 方法（密码脱敏）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtilTest.java`
- [ ] T093 [FR3] 测试 mask() 方法（自定义脱敏规则）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtilTest.java`
- [ ] T094 [FR3] 测试边界情况（null、空字符串、长度不足）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/desensitize/DesensitizeUtilTest.java`
- [ ] T095 [P] [FR3] 创建 DesensitizeRuleTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/desensitize/DesensitizeRuleTest.java`
- [ ] T096 [FR3] 测试 DesensitizeRule 构建和验证在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/desensitize/DesensitizeRuleTest.java`
- [ ] T097 [P] [FR3] 创建 DesensitizeInterceptorTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/desensitize/DesensitizeInterceptorTest.java`
- [ ] T098 [FR3] 测试日志消息自动脱敏在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/desensitize/DesensitizeInterceptorTest.java`
- [ ] T099 [FR3] 测试对象字段自动脱敏（通过 @Sensitive 注解）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/desensitize/DesensitizeInterceptorTest.java`

### 日志配置测试

- [ ] T100 [P] [FR1] 创建日志配置测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/config/LogbackConfigTest.java`
- [ ] T101 [FR1] 测试日志格式输出（包含 TraceId）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/config/LogbackConfigTest.java`
- [ ] T102 [FR1] 测试日志文件轮转在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/config/LogbackConfigTest.java`
- [ ] T103 [FR1] 测试环境特定配置（dev、test、prod）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/src/test/java/com/atlas/common/infra/logging/config/LogbackConfigTest.java`

## Phase 6: 文档和示例

**目标**: 创建使用文档和示例代码

**独立测试标准**: 文档完整，示例代码清晰易懂，README.md 符合项目宪法要求

- [ ] T104 验证 quickstart.md 文档完整性 `/Users/liuhuan/workspace/project/java/backend/atlas/specs/4-infra-logging/quickstart.md`
- [ ] T105 创建日志配置使用示例代码在文档中
- [ ] T106 创建 TraceId 使用示例代码在文档中
- [ ] T107 创建脱敏工具使用示例代码在文档中
- [ ] T108 验证模块 README.md 完整性 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-infra/atlas-common-infra-logging/README.md`
- [ ] T109 验证所有代码通过 Spotless 格式化检查
- [ ] T110 验证所有代码通过 Maven Enforcer 检查
- [ ] T111 验证单元测试覆盖率 ≥ 80%

## 并行执行示例

### Phase 2 可并行任务

以下任务可以并行执行（处理不同的配置部分）：

- T012-T018 可以在 T011 后并行（配置不同的 appender 和策略）

### Phase 3 可并行任务

以下任务可以并行执行（处理不同的类文件）：

- T020, T024, T031, T037, T041 可以并行（创建不同的类文件）
- T021-T023 需要在 T020 后顺序执行（TraceIdGenerator 方法实现）
- T025-T030 需要在 T024 后顺序执行（TraceIdUtil 方法实现）
- T032-T036 需要在 T031 后顺序执行（TraceIdInterceptor 方法实现）
- T038-T040 需要在 T037 后顺序执行（TraceIdFeignInterceptor 方法实现）
- T042-T044 需要在 T041 后顺序执行（TraceIdTaskDecorator 方法实现）

### Phase 4 可并行任务

以下任务可以并行执行（处理不同的类文件）：

- T045, T053, T057, T062, T065 可以并行（创建不同的类文件）
- T046-T052 需要在 T045 后顺序执行（DesensitizeUtil 方法实现）
- T054-T056 需要在 T053 后顺序执行（DesensitizeRule 类实现）
- T058-T061 需要在 T057 后顺序执行（@Sensitive 注解实现）
- T063-T064 需要在 T062 后顺序执行（SensitiveType 枚举实现）
- T066-T069 需要在 T065 后顺序执行（DesensitizeInterceptor 方法实现）

### Phase 5 可并行任务

以下任务可以并行执行（测试不同的类）：

- T070, T073, T079, T083, T085, T087, T095, T097, T100 可以并行（创建不同的测试类）
- T071-T072 需要在 T070 后顺序执行（TraceIdGenerator 测试）
- T074-T078 需要在 T073 后顺序执行（TraceIdUtil 测试）
- T080-T082 需要在 T079 后顺序执行（TraceIdInterceptor 测试）
- T084 需要在 T083 后顺序执行（TraceIdFeignInterceptor 测试）
- T086 需要在 T085 后顺序执行（TraceIdTaskDecorator 测试）
- T088-T094 需要在 T087 后顺序执行（DesensitizeUtil 测试）
- T096 需要在 T095 后顺序执行（DesensitizeRule 测试）
- T098-T099 需要在 T097 后顺序执行（DesensitizeInterceptor 测试）
- T101-T103 需要在 T100 后顺序执行（日志配置测试）

## 代码审查检查点

- [ ] 所有类和方法添加中文注释
- [ ] 包名符合 `com.atlas.common.infra.logging` 规范
- [ ] 日志配置模板包含 TraceId 格式（%X{traceId}）
- [ ] TraceId 在请求结束后正确清理，避免内存泄漏
- [ ] 脱敏规则准确，不会误脱敏
- [ ] 脱敏工具性能可接受（正则表达式编译后缓存）
- [ ] 单元测试覆盖率 ≥ 80%
- [ ] 所有代码通过 Spotless 格式化检查
- [ ] 所有代码通过 Maven Enforcer 检查
- [ ] README.md 文档完整，符合项目宪法要求

## 实施注意事项

- [ ] 确保日志配置模板符合项目日志格式规范
- [ ] 确保 TraceId 的线程安全性（使用 ThreadLocal）
- [ ] 确保 TraceId 在请求结束后正确清理，避免内存泄漏
- [ ] 确保脱敏规则的准确性和性能（编译正则表达式并缓存）
- [ ] 确保所有类和方法添加中文注释
- [ ] 遵循包名规范：`com.atlas.common.infra.logging`
- [ ] 日志配置支持环境变量和配置文件动态配置
- [ ] TraceId 生成器支持 UUID 和雪花算法两种方式
- [ ] 脱敏工具支持常见敏感字段和自定义规则
- [ ] 日志拦截器脱敏性能可接受，考虑异步处理或采样

