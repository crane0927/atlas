# 任务清单

## 宪法合规检查清单

在开始实施前，确认以下事项：

- [ ] 技术栈版本符合宪法要求（JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0）
- [ ] 代码注释使用中文
- [ ] 包名规范遵循 `com.atlas.common.feature.core`
- [ ] 错误码格式符合项目规范（6位数字：MMTTSS）
- [ ] 配置文件使用 YAML 格式

## 任务概览

**总任务数**: 42  
**阶段数**: 4  
**可并行任务**: 15

## 依赖关系

### 阶段完成顺序

1. **Phase 1: 项目初始化** → 必须先完成
2. **Phase 2: 核心类实现** → 依赖 Phase 1
3. **Phase 3: 单元测试** → 依赖 Phase 2
4. **Phase 4: 文档和示例** → 依赖 Phase 2

### 功能需求映射

- **FR1（Result<T>）**: Phase 2 实现，Phase 3 测试
- **FR2（错误码常量）**: Phase 2 实现，Phase 3 测试
- **FR3（异常体系）**: Phase 2 实现，Phase 3 测试
- **FR4（分页对象）**: Phase 2 实现，Phase 3 测试
- **FR5（基础常量）**: Phase 2 实现，Phase 3 测试

## 实施策略

**MVP 范围**: Phase 1 + Phase 2（核心类实现）  
**增量交付**: 
1. 先完成项目初始化和核心类实现
2. 再完成单元测试，确保质量
3. 最后完善文档和示例

## Phase 1: 项目初始化

**目标**: 创建模块基础结构和配置

**独立测试标准**: 模块目录结构创建完成，POM 配置正确，可以开始实现核心类

- [x] T001 创建模块目录结构 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core`
- [x] T002 创建源代码目录 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core`
- [x] T003 创建测试代码目录 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core`
- [x] T004 创建模块 POM 文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/pom.xml`
- [x] T005 [FR1-FR5] 配置模块基本信息（groupId、artifactId、version、packaging）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/pom.xml`
- [x] T006 [FR1-FR5] 配置继承父 POM 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/pom.xml`
- [x] T007 [FR1-FR5] 添加 Spring Boot Web 依赖在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/pom.xml`
- [x] T008 [FR1-FR5] 添加 Lombok 依赖在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/pom.xml`
- [x] T009 将模块添加到父 POM 的 modules 列表在 `/Users/liuhuan/workspace/project/java/backend/atlas/pom.xml`

## Phase 2: 核心类实现

**目标**: 实现所有核心功能类

**独立测试标准**: 所有核心类实现完成，可以编译通过，包含完整的中文注释

### FR1: Result<T> 响应包装类

- [ ] T010 [FR1] 创建 Result 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/result/Result.java`
- [ ] T011 [FR1] 定义 Result 类泛型声明和字段（code、message、data、timestamp）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/result/Result.java`
- [ ] T012 [FR1] 添加 Lombok 注解（@Data、@Builder）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/result/Result.java`
- [ ] T013 [FR1] 添加 Jackson 序列化注解（@JsonInclude）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/result/Result.java`
- [ ] T014 [FR1] 实现静态工厂方法 success(T data) 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/result/Result.java`
- [ ] T015 [FR1] 实现静态工厂方法 success(String message, T data) 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/result/Result.java`
- [ ] T016 [FR1] 实现静态工厂方法 error(String code, String message) 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/result/Result.java`
- [ ] T017 [FR1] 实现静态工厂方法 error(String code, String message, T data) 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/result/Result.java`
- [ ] T018 [FR1] 实现 isSuccess() 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/result/Result.java`
- [ ] T019 [FR1] 添加完整的中文注释（类、方法、字段）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/result/Result.java`

### FR2: 错误码常量类

- [ ] T020 [P] [FR2] 创建 CommonErrorCode 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/CommonErrorCode.java`
- [ ] T021 [FR2] 定义系统错误码常量（050000-050999）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/CommonErrorCode.java`
- [ ] T022 [FR2] 定义参数错误码常量（051000-051999）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/CommonErrorCode.java`
- [ ] T023 [FR2] 定义业务错误码常量（052000-052999）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/CommonErrorCode.java`
- [ ] T024 [FR2] 定义权限错误码常量（053000-053999）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/CommonErrorCode.java`
- [ ] T025 [FR2] 定义数据错误码常量（054000-054999）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/CommonErrorCode.java`
- [ ] T026 [FR2] 添加私有构造函数防止实例化在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/CommonErrorCode.java`
- [ ] T027 [FR2] 添加完整的中文注释（类、常量）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/CommonErrorCode.java`

### FR3: 异常体系

- [ ] T028 [P] [FR3] 创建 BusinessException 基类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/exception/BusinessException.java`
- [ ] T029 [FR3] 定义 BusinessException 类字段（errorCode、message、cause）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/exception/BusinessException.java`
- [ ] T030 [FR3] 实现 BusinessException 构造函数（errorCode, message）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/exception/BusinessException.java`
- [ ] T031 [FR3] 实现 BusinessException 构造函数（errorCode, message, cause）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/exception/BusinessException.java`
- [ ] T032 [FR3] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/exception/BusinessException.java`
- [ ] T033 [P] [FR3] 创建 ParameterException 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/exception/ParameterException.java`
- [ ] T034 [FR3] 实现 ParameterException 类继承 BusinessException 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/exception/ParameterException.java`
- [ ] T035 [P] [FR3] 创建 PermissionException 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/exception/PermissionException.java`
- [ ] T036 [FR3] 实现 PermissionException 类继承 BusinessException 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/exception/PermissionException.java`
- [ ] T037 [P] [FR3] 创建 DataException 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/exception/DataException.java`
- [ ] T038 [FR3] 实现 DataException 类继承 BusinessException 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/exception/DataException.java`
- [ ] T039 [FR3] 为所有异常类添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/exception/`

### FR4: 分页对象

- [ ] T040 [P] [FR4] 创建 PageResult 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/page/PageResult.java`
- [ ] T041 [FR4] 定义 PageResult 类泛型声明和字段（list、total、page、size、pages）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/page/PageResult.java`
- [ ] T042 [FR4] 添加 Lombok 注解（@Data、@Builder）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/page/PageResult.java`
- [ ] T043 [FR4] 实现静态工厂方法 of(List<T> list, Long total, Integer page, Integer size) 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/page/PageResult.java`
- [ ] T044 [FR4] 实现静态工厂方法 of(List<T> list, Long total) 在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/page/PageResult.java`
- [ ] T045 [FR4] 实现分页计算逻辑（pages、hasPrevious、hasNext、isFirst、isLast）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/page/PageResult.java`
- [ ] T046 [FR4] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/page/PageResult.java`

### FR5: 基础常量类

- [ ] T047 [P] [FR5] 创建 HttpStatus 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/HttpStatus.java`
- [ ] T048 [FR5] 定义 HTTP 状态码常量（OK、CREATED、BAD_REQUEST、UNAUTHORIZED、FORBIDDEN、NOT_FOUND、INTERNAL_SERVER_ERROR）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/HttpStatus.java`
- [ ] T049 [FR5] 添加私有构造函数防止实例化在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/HttpStatus.java`
- [ ] T050 [FR5] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/HttpStatus.java`
- [ ] T051 [P] [FR5] 创建 CommonConstants 类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/CommonConstants.java`
- [ ] T052 [FR5] 定义通用常量（EMPTY_STRING、DEFAULT_PAGE、DEFAULT_SIZE、MAX_PAGE_SIZE、SUCCESS_CODE、SUCCESS_MESSAGE）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/CommonConstants.java`
- [ ] T053 [FR5] 添加私有构造函数防止实例化在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/CommonConstants.java`
- [ ] T054 [FR5] 添加完整的中文注释在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/main/java/com/atlas/common/feature/core/constant/CommonConstants.java`

## Phase 3: 单元测试

**目标**: 编写完整的单元测试，确保代码质量

**独立测试标准**: 单元测试覆盖率 ≥ 80%，所有测试用例通过

### Result 类测试

- [ ] T055 [FR1] 创建 ResultTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/result/ResultTest.java`
- [ ] T056 [FR1] 测试 success(T data) 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/result/ResultTest.java`
- [ ] T057 [FR1] 测试 success(String message, T data) 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/result/ResultTest.java`
- [ ] T058 [FR1] 测试 error(String code, String message) 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/result/ResultTest.java`
- [ ] T059 [FR1] 测试 error(String code, String message, T data) 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/result/ResultTest.java`
- [ ] T060 [FR1] 测试 isSuccess() 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/result/ResultTest.java`
- [ ] T061 [FR1] 测试 JSON 序列化在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/result/ResultTest.java`

### 异常类测试

- [ ] T062 [P] [FR3] 创建 BusinessExceptionTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/exception/BusinessExceptionTest.java`
- [ ] T063 [FR3] 测试 BusinessException 创建和抛出在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/exception/BusinessExceptionTest.java`
- [ ] T064 [FR3] 测试 BusinessException 错误码和消息在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/exception/BusinessExceptionTest.java`
- [ ] T065 [FR3] 测试 BusinessException 异常链在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/exception/BusinessExceptionTest.java`
- [ ] T066 [P] [FR3] 测试 ParameterException、PermissionException、DataException 继承关系在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/exception/`

### PageResult 类测试

- [ ] T067 [P] [FR4] 创建 PageResultTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/page/PageResultTest.java`
- [ ] T068 [FR4] 测试 of(List<T> list, Long total, Integer page, Integer size) 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/page/PageResultTest.java`
- [ ] T069 [FR4] 测试 of(List<T> list, Long total) 方法在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/page/PageResultTest.java`
- [ ] T070 [FR4] 测试分页计算逻辑（pages、hasPrevious、hasNext、isFirst、isLast）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/page/PageResultTest.java`
- [ ] T071 [FR4] 测试边界情况（total=0、空列表、page>pages）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/page/PageResultTest.java`
- [ ] T072 [FR4] 测试 JSON 序列化在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/page/PageResultTest.java`

### 错误码常量类测试

- [ ] T073 [P] [FR2] 创建 CommonErrorCodeTest 测试类文件 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/constant/CommonErrorCodeTest.java`
- [ ] T074 [FR2] 测试错误码格式（6位数字）在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/constant/CommonErrorCodeTest.java`
- [ ] T075 [FR2] 测试错误码常量值在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/constant/CommonErrorCodeTest.java`

### 常量类测试

- [ ] T076 [P] [FR5] 测试 HttpStatus 常量值在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/constant/HttpStatusTest.java`
- [ ] T077 [FR5] 测试 CommonConstants 常量值在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/constant/CommonConstantsTest.java`
- [ ] T078 [FR5] 测试常量类不能被实例化在 `/Users/liuhuan/workspace/project/java/backend/atlas/atlas-common/atlas-common-feature/atlas-common-feature-core/src/test/java/com/atlas/common/feature/core/constant/`

## Phase 4: 文档和示例

**目标**: 创建使用文档和示例代码

**独立测试标准**: 文档完整，示例代码可以运行

- [ ] T079 验证 quickstart.md 文档完整性 `/Users/liuhuan/workspace/project/java/backend/atlas/specs/2-common-feature-core/quickstart.md`
- [ ] T080 创建 Result 使用示例代码在文档中
- [ ] T081 创建异常使用示例代码在文档中
- [ ] T082 创建分页对象使用示例代码在文档中
- [ ] T083 验证所有代码通过 Spotless 格式化检查
- [ ] T084 验证所有代码通过 Maven Enforcer 检查
- [ ] T085 验证单元测试覆盖率 ≥ 80%

## 并行执行示例

### Phase 2 可并行任务

以下任务可以并行执行（处理不同的类文件）：

- T020, T028, T033, T035, T037, T040, T047, T051 可以并行（创建不同的类文件）
- T021-T027 可以在 T020 后并行（定义不同的错误码常量）
- T034, T036, T038 可以在 T028-T032 后并行（实现不同的异常子类）

### Phase 3 可并行任务

以下任务可以并行执行（测试不同的类）：

- T055, T062, T067, T073, T076 可以并行（创建不同的测试类）
- T056-T061, T063-T065, T068-T072, T074-T075, T077-T078 可以在各自测试类创建后并行

## 代码审查检查点

- [ ] 所有类和方法添加中文注释
- [ ] 包名符合 `com.atlas.common.feature.core` 规范
- [ ] 错误码格式符合 6 位数字规范（MMTTSS）
- [ ] Result 类支持泛型，可以包装任意类型
- [ ] 异常类可以正确抛出和捕获
- [ ] 分页对象计算逻辑正确
- [ ] 常量类不能被实例化
- [ ] 单元测试覆盖率 ≥ 80%
- [ ] 所有代码通过 Spotless 格式化检查
- [ ] 所有代码通过 Maven Enforcer 检查

## 实施注意事项

- [ ] 确保 Result 类支持泛型，可以包装任意类型
- [ ] 确保所有类和方法添加中文注释
- [ ] 确保错误码常量符合项目规范（模块码 05）
- [ ] 确保异常类可以正确序列化
- [ ] 确保分页对象计算逻辑正确（pages = (total + size - 1) / size）
- [ ] 确保常量类不能被实例化
- [ ] 遵循包名规范：`com.atlas.common.feature.core`
- [ ] 使用 Lombok 简化代码
- [ ] 使用 Jackson 注解控制序列化行为

