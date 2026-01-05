# 任务清单

## 功能概述

实现 `atlas-common-infra-web` 模块，提供 Web 基础设施功能，包括全局异常处理、参数校验返回、Jackson 配置、TraceId Filter 等。

## 用户故事

### US1: 全局异常处理
**优先级**: P1  
**描述**: 开发人员使用全局异常处理，所有异常都可以统一处理，返回标准的 `Result` 格式，包含错误码、错误消息和 TraceId。  
**验收标准**: 
- 所有业务异常可以统一处理并返回标准格式
- Spring MVC 异常可以统一处理并返回标准格式
- 系统异常可以统一处理并返回标准格式
- 异常响应格式符合 `Result` 规范
- 异常响应包含 TraceId（从 MDC 获取）
- 异常日志可以正确记录

### US2: 参数校验返回
**优先级**: P1  
**描述**: 系统自动处理参数校验错误，参数校验错误可以统一处理，返回标准的 `Result` 格式，包含字段错误列表和 TraceId。  
**验收标准**: 
- `@Valid` 注解的校验错误可以统一处理
- `@Validated` 注解的校验错误可以统一处理
- 校验错误信息可以正确提取和格式化
- 校验错误响应格式符合 `Result` 规范
- 校验错误响应包含字段错误列表
- 校验错误响应包含 TraceId

### US3: Jackson 配置
**优先级**: P2  
**描述**: 系统使用统一的 Jackson 配置，所有 JSON 响应都使用统一的格式（日期时间格式、空值处理、自定义序列化等）。  
**验收标准**: 
- 日期时间可以统一格式化
- 空值可以统一处理
- JSON 序列化特性符合项目规范
- JSON 反序列化特性符合项目规范
- 时区可以正确处理
- 自定义序列化器可以正常工作

### US4: TraceId Filter
**优先级**: P2  
**描述**: 系统自动处理 TraceId，所有请求都有 TraceId，TraceId 可以正确传递到日志和下游服务，便于问题排查和链路追踪。  
**验收标准**: 
- HTTP 请求可以自动获取或生成 TraceId
- TraceId 可以正确设置到 `TraceIdUtil`
- TraceId 可以正确输出到日志中（通过 MDC）
- TraceId 可以在响应头中返回（可选）
- Filter 执行顺序正确
- Filter 可以配置 URL 匹配模式

## 依赖关系

```
Phase 1 (Setup)
    ↓
Phase 2 (Foundational: ValidationError)
    ↓
Phase 3 (US1: 全局异常处理)
    ↓
Phase 4 (US2: 参数校验返回) - 依赖 US1
    ↓
Phase 5 (US3: Jackson 配置) - 独立
    ↓
Phase 6 (US4: TraceId Filter) - 独立
    ↓
Phase 7 (Polish: 文档和测试)
```

## 并行执行机会

- **Phase 3 和 Phase 5**: US1（全局异常处理）和 US3（Jackson 配置）可以并行实现
- **Phase 4 和 Phase 5**: US2（参数校验返回）和 US3（Jackson 配置）可以并行实现
- **Phase 5 和 Phase 6**: US3（Jackson 配置）和 US4（TraceId Filter）可以并行实现

## MVP 范围

**MVP**: Phase 1 + Phase 2 + Phase 3 + Phase 4（Setup + Foundational + US1 + US2）

MVP 提供核心的异常处理和参数校验功能，满足最基本的 Web 基础设施需求。

## 实施任务

### Phase 1: 项目初始化和基础配置

**目标**: 创建模块结构，配置依赖，搭建基础框架

**独立测试标准**: 模块可以成功编译，依赖配置正确

- [x] T001 Create module directory structure `atlas-common/atlas-common-infra/atlas-common-infra-web/`
- [x] T002 Create `pom.xml` with dependencies in `atlas-common/atlas-common-infra/atlas-common-infra-web/pom.xml`
- [x] T003 Create package structure `com/atlas/common/infra/web/exception/` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/`
- [x] T004 Create package structure `com/atlas/common/infra/web/config/` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/`
- [x] T005 Create package structure `com/atlas/common/infra/web/filter/` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/`
- [x] T006 Create package structure `com/atlas/common/infra/web/serializer/` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/`
- [x] T007 Create test package structure `com/atlas/common/infra/web/exception/` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/`
- [x] T008 Create test package structure `com/atlas/common/infra/web/config/` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/`
- [x] T009 Create test package structure `com/atlas/common/infra/web/filter/` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/`
- [x] T010 Create test package structure `com/atlas/common/infra/web/serializer/` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/`
- [x] T011 Create `README.md` template in `atlas-common/atlas-common-infra/atlas-common-infra-web/README.md`

### Phase 2: 基础组件实现

**目标**: 实现基础组件，为异常处理提供支持

**独立测试标准**: ValidationError 和 FieldError 类可以正确序列化和反序列化

- [x] T012 [P] Create `FieldError` class in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/FieldError.java`
- [x] T013 [P] Create `ValidationError` class in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/ValidationError.java`
- [x] T014 [P] Write unit tests for `FieldError` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/com/atlas/common/infra/web/exception/FieldErrorTest.java`
- [x] T015 [P] Write unit tests for `ValidationError` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/com/atlas/common/infra/web/exception/ValidationErrorTest.java`

### Phase 3: US1 - 全局异常处理

**目标**: 实现全局异常处理器，统一处理所有异常

**独立测试标准**: 所有异常类型都可以正确处理，异常响应格式符合 `Result` 规范，异常响应包含 TraceId

- [x] T016 [US1] Create `GlobalExceptionHandler` class in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`
- [x] T017 [US1] Implement `handleBusinessException` method in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`
- [x] T018 [US1] Implement `handleParameterException` method in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`
- [x] T019 [US1] Implement `handlePermissionException` method in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`
- [x] T020 [US1] Implement `handleDataException` method in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`
- [x] T021 [US1] Implement `handleHttpRequestMethodNotSupportedException` method in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`
- [x] T022 [US1] Implement `handleHttpMediaTypeNotSupportedException` method in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`
- [x] T023 [US1] Implement `handleMissingServletRequestParameterException` method in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`
- [x] T024 [US1] Implement `handleException` method for system exceptions in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`
- [x] T025 [US1] Implement exception logging functionality in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`
- [x] T026 [US1] Write unit tests for business exception handling in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/com/atlas/common/infra/web/exception/GlobalExceptionHandlerTest.java`
- [x] T027 [US1] Write unit tests for Spring MVC exception handling in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/com/atlas/common/infra/web/exception/GlobalExceptionHandlerTest.java`
- [x] T028 [US1] Write unit tests for system exception handling in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/com/atlas/common/infra/web/exception/GlobalExceptionHandlerTest.java`

### Phase 4: US2 - 参数校验返回

**目标**: 实现参数校验异常的统一处理

**独立测试标准**: `@Valid` 和 `@Validated` 注解的校验错误可以统一处理，校验错误响应格式符合 `Result` 规范，校验错误响应包含字段错误列表和 TraceId

- [x] T029 [US2] Implement `handleMethodArgumentNotValidException` method in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`
- [x] T030 [US2] Implement `handleConstraintViolationException` method in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`
- [x] T031 [US2] Implement field error extraction logic in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/exception/GlobalExceptionHandler.java`
- [x] T032 [US2] Write unit tests for `MethodArgumentNotValidException` handling in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/com/atlas/common/infra/web/exception/GlobalExceptionHandlerTest.java`
- [x] T033 [US2] Write unit tests for `ConstraintViolationException` handling in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/com/atlas/common/infra/web/exception/GlobalExceptionHandlerTest.java`

### Phase 5: US3 - Jackson 配置

**目标**: 实现统一的 Jackson JSON 序列化配置

**独立测试标准**: 日期时间可以统一格式化，空值可以统一处理，JSON 序列化特性符合项目规范，自定义序列化器可以正常工作

- [ ] T034 [P] [US3] Create `LongToStringSerializer` class in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/serializer/LongToStringSerializer.java`
- [ ] T035 [P] [US3] Create `JacksonConfig` class in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/config/JacksonConfig.java`
- [ ] T036 [US3] Configure `ObjectMapper` Bean in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/config/JacksonConfig.java`
- [ ] T037 [US3] Configure date format (ISO-8601) in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/config/JacksonConfig.java`
- [ ] T038 [US3] Configure null value handling in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/config/JacksonConfig.java`
- [ ] T039 [US3] Configure serialization features in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/config/JacksonConfig.java`
- [ ] T040 [US3] Configure deserialization features in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/config/JacksonConfig.java`
- [ ] T041 [US3] Configure timezone handling in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/config/JacksonConfig.java`
- [ ] T042 [US3] Register `LongToStringSerializer` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/config/JacksonConfig.java`
- [ ] T043 [P] [US3] Write unit tests for `LongToStringSerializer` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/com/atlas/common/infra/web/serializer/LongToStringSerializerTest.java`
- [ ] T044 [US3] Write unit tests for `JacksonConfig` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/com/atlas/common/infra/web/config/JacksonConfigTest.java`

### Phase 6: US4 - TraceId Filter

**目标**: 实现 TraceId Filter，在请求的最早阶段设置 TraceId

**独立测试标准**: HTTP 请求可以自动获取或生成 TraceId，TraceId 可以正确设置到 `TraceIdUtil`，TraceId 可以正确输出到日志中，Filter 执行顺序正确

- [ ] T045 [P] [US4] Create `TraceIdFilter` class in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/filter/TraceIdFilter.java`
- [ ] T046 [US4] Implement `doFilter` method to get TraceId from request header in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/filter/TraceIdFilter.java`
- [ ] T047 [US4] Implement TraceId generation logic if not present in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/filter/TraceIdFilter.java`
- [ ] T048 [US4] Implement TraceId setting to `TraceIdUtil` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/filter/TraceIdFilter.java`
- [ ] T049 [US4] Implement optional TraceId response header addition in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/filter/TraceIdFilter.java`
- [ ] T050 [US4] Implement TraceId cleanup in finally block in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/filter/TraceIdFilter.java`
- [ ] T051 [US4] Create `FilterRegistrationBean` configuration in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/config/WebConfig.java`
- [ ] T052 [US4] Configure Filter execution order in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/config/WebConfig.java`
- [ ] T053 [US4] Configure URL pattern matching in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/main/java/com/atlas/common/infra/web/config/WebConfig.java`
- [ ] T054 [P] [US4] Write unit tests for `TraceIdFilter` in `atlas-common/atlas-common-infra/atlas-common-infra-web/src/test/java/com/atlas/common/infra/web/filter/TraceIdFilterTest.java`

### Phase 7: 文档和测试完善

**目标**: 完善文档，提供使用示例，确保测试覆盖率

**独立测试标准**: 文档完整清晰，使用示例可以正常运行，单元测试覆盖率 ≥ 80%

- [ ] T055 Update `README.md` with module introduction in `atlas-common/atlas-common-infra/atlas-common-infra-web/README.md`
- [ ] T056 Update `README.md` with feature descriptions in `atlas-common/atlas-common-infra/atlas-common-infra-web/README.md`
- [ ] T057 Update `README.md` with quick start guide in `atlas-common/atlas-common-infra/atlas-common-infra-web/README.md`
- [ ] T058 Update `README.md` with usage examples in `atlas-common/atlas-common-infra/atlas-common-infra-web/README.md`
- [ ] T059 Update `README.md` with related documentation links in `atlas-common/atlas-common-infra/atlas-common-infra-web/README.md`
- [ ] T060 Run Spotless formatting check and fix any violations
- [ ] T061 Run Maven Enforcer check and verify compliance
- [ ] T062 Verify unit test coverage ≥ 80%
- [ ] T063 Run integration tests for main exception scenarios

## 实施策略

### MVP 优先

**MVP 范围**: Phase 1 + Phase 2 + Phase 3 + Phase 4

MVP 提供核心的异常处理和参数校验功能，满足最基本的 Web 基础设施需求。完成后可以立即在其他业务模块中使用。

### 增量交付

1. **迭代 1**: MVP（Phase 1-4）- 核心异常处理和参数校验
2. **迭代 2**: Jackson 配置（Phase 5）- 统一 JSON 序列化
3. **迭代 3**: TraceId Filter（Phase 6）- TraceId 管理
4. **迭代 4**: 文档和测试完善（Phase 7）- 质量保证

### 并行开发

- **Phase 3 和 Phase 5**: 全局异常处理和 Jackson 配置可以并行开发
- **Phase 4 和 Phase 5**: 参数校验返回和 Jackson 配置可以并行开发
- **Phase 5 和 Phase 6**: Jackson 配置和 TraceId Filter 可以并行开发

## 任务统计

- **总任务数**: 63
- **Setup 阶段**: 11 个任务
- **Foundational 阶段**: 4 个任务
- **US1 (全局异常处理)**: 13 个任务
- **US2 (参数校验返回)**: 5 个任务
- **US3 (Jackson 配置)**: 11 个任务
- **US4 (TraceId Filter)**: 10 个任务
- **Polish 阶段**: 9 个任务

## 并行机会

- **Phase 2**: 4 个任务可以并行（T012-T015）
- **Phase 3 和 Phase 5**: 可以并行开发（US1 和 US3）
- **Phase 4 和 Phase 5**: 可以并行开发（US2 和 US3）
- **Phase 5 和 Phase 6**: 可以并行开发（US3 和 US4）
- **Phase 5**: 2 个任务可以并行（T034, T043）
- **Phase 6**: 1 个任务可以并行（T045, T054）

