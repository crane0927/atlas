# 功能规划文档

## 宪法检查

在开始规划前，请确认以下宪法原则：

- ✅ **技术栈**: JDK 21, Spring Boot 4.0.1, Spring Cloud 2025.1.0, Spring Cloud Alibaba 2025.1.0
- ✅ **API 设计**: 遵循 RESTful 风格
- ✅ **代码注释**: 使用中文注释
- ✅ **代码复用**: 提取公共方法，避免重复代码
- ✅ **模块化**: 功能归属正确的模块
- ✅ **包结构规范**: 技术模块按技术分层组织（`com.atlas.system.api.v1.*`）
- ✅ **接口兼容性**: 遵循接口兼容性规则，使用版本包管理
- ✅ **依赖约束**: API 模块不引入 web/db/redis 等实现层依赖

## 功能概述

实现 `atlas-system-api` 模块，定义系统域的 API 接口契约，包括用户查询和权限查询接口，供 `atlas-auth` 服务使用。

**核心目标**:
- 创建 `atlas-system-api` 模块，定义系统域的 Feign 接口契约
- 定义用户查询接口（供 auth 服务查询用户信息）
- 定义权限查询接口（供 auth 服务查询用户权限和角色）
- 定义相关的 DTO 对象和枚举常量
- 遵循接口兼容性规则，使用版本包（v1）管理
- 模块仅包含契约和 DTO，不引入 web/db/redis 等实现层依赖

**业务价值**:
- 为 `atlas-auth` 服务提供标准化的用户和权限查询接口
- 为其他业务服务提供统一的用户信息查询能力
- 通过版本包管理确保接口兼容性，支持平滑升级

## 技术方案

### 架构设计

**模块定位**:
- `atlas-system-api` 是一个纯契约定义模块，不包含任何业务逻辑实现
- 模块遵循技术模块的包结构规范（按技术分层组织）
- 模块使用版本包管理（v1），便于后续版本扩展

**模块结构**:
```
atlas-system-api/
├── src/main/java/com/atlas/system/api/v1/
│   ├── feign/          # Feign 接口定义
│   │   ├── UserQueryApi.java
│   │   └── PermissionQueryApi.java
│   ├── dto/            # DTO 对象定义
│   │   ├── UserDTO.java
│   │   └── UserAuthoritiesDTO.java
│   └── enums/          # 枚举常量定义
│       └── UserStatus.java
└── pom.xml
```

**依赖关系**:
- 依赖 `atlas-common-feature-core`（用于 Result、错误码、异常体系）
- 依赖 Spring Cloud OpenFeign（用于 Feign 接口定义）
- 可选依赖 Lombok（用于简化代码）
- 禁止依赖 web/db/redis 等实现层组件

### 技术选型

**Feign 接口定义**:
- 使用 Spring Cloud OpenFeign 定义服务间调用接口
- 使用 `@FeignClient` 注解标记接口
- 接口路径遵循 RESTful 设计规范
- 接口返回类型使用统一的 `Result<T>` 格式

**版本管理**:
- 使用包名版本管理：`com.atlas.system.api.v1.*`
- 当前版本为 v1，后续破坏性变更通过 v2 实现
- 同一版本内的变更必须保持向后兼容

**DTO 设计**:
- 使用 Lombok 简化代码（可选）
- 字段必须可序列化（支持 JSON）
- 新增字段必须可空或提供默认值（向后兼容）

**枚举设计**:
- 枚举值必须可序列化（支持 JSON）
- 枚举值包含完整的中文注释

## 实施计划

### 阶段 0: 研究与设计

**目标**: 完成技术调研和设计文档

**任务**:
1. 研究 Spring Cloud OpenFeign 最佳实践
2. 研究接口版本管理策略
3. 研究 DTO 向后兼容性设计
4. 生成 `research.md` 文档

**输出**:
- `research.md` - 技术调研文档

### 阶段 1: 数据模型与契约定义

**目标**: 完成数据模型和 API 契约定义

**任务**:
1. 定义数据模型（UserDTO、UserAuthoritiesDTO、UserStatus）
2. 生成 `data-model.md` 文档
3. 定义 Feign 接口契约（UserQueryApi、PermissionQueryApi）
4. 生成 `contracts/README.md` 文档
5. 生成 `quickstart.md` 快速开始指南

**输出**:
- `data-model.md` - 数据模型定义
- `contracts/README.md` - API 契约定义
- `quickstart.md` - 快速开始指南

### 阶段 2: 模块实现

**目标**: 实现 `atlas-system-api` 模块

**任务**:
1. 创建 Maven 模块 `atlas-system-api`
2. 配置 `pom.xml`（依赖管理、版本约束）
3. 实现 Feign 接口（UserQueryApi、PermissionQueryApi）
4. 实现 DTO 对象（UserDTO、UserAuthoritiesDTO）
5. 实现枚举常量（UserStatus）
6. 添加完整的中文注释
7. 编写单元测试

**输出**:
- `atlas-system-api` 模块代码
- 单元测试代码

### 阶段 3: 文档与验收

**目标**: 完成文档编写和验收测试

**任务**:
1. 编写模块 README.md
2. 验证依赖约束（确保不引入 web/db/redis）
3. 验证包结构符合规范
4. 验证接口兼容性规则
5. 验收测试

**输出**:
- `README.md` - 模块文档
- 验收测试报告

## 风险评估

### 技术风险

1. **接口兼容性风险**
   - **风险**: 接口变更可能破坏现有客户端
   - **应对**: 严格遵循接口兼容性规则，破坏性变更必须通过新版本包实现

2. **依赖冲突风险**
   - **风险**: 依赖版本冲突可能导致编译或运行时错误
   - **应对**: 使用 Spring Cloud BOM 管理依赖版本，确保版本一致性

3. **DTO 序列化风险**
   - **风险**: DTO 字段序列化/反序列化可能失败
   - **应对**: 使用 Jackson 注解明确字段映射，编写单元测试验证序列化

### 业务风险

1. **接口设计不合理**
   - **风险**: 接口设计不符合业务需求，需要频繁变更
   - **应对**: 充分调研业务需求，与业务团队确认接口设计

2. **版本管理混乱**
   - **风险**: 版本管理不规范，导致接口版本混乱
   - **应对**: 严格遵循版本包管理规范，建立版本管理流程

## 验收标准

### 功能验收

1. **模块创建**: `atlas-system-api` 模块创建成功，目录结构符合规范
2. **依赖约束**: 模块不包含 web/db/redis 等实现层依赖
3. **接口定义**: Feign 接口定义完整，包含用户查询和权限查询接口
4. **DTO 定义**: DTO 对象定义完整，包含所有必要字段
5. **枚举定义**: 枚举常量定义完整，包含所有必要枚举值
6. **版本管理**: 包结构符合版本管理规范（`com.atlas.system.api.v1.*`）
7. **兼容性规则**: 接口和 DTO 遵循兼容性规则

### 技术验收

1. **包结构**: 包结构符合技术模块规范（按技术分层组织）
2. **代码注释**: 所有类、方法、字段包含完整的中文注释
3. **RESTful 规范**: 接口路径遵循 RESTful 设计规范
4. **统一响应格式**: 接口返回类型使用统一的 `Result<T>` 格式
5. **单元测试**: DTO 和枚举的序列化/反序列化测试通过
