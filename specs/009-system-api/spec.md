# 功能规格说明

## 宪法检查

本规格说明必须符合项目宪法要求：

- ✅ **RESTful API**: 所有接口遵循 RESTful 设计规范
- ✅ **中文注释**: 所有代码使用中文注释
- ✅ **代码复用**: 识别并复用现有公共方法
- ✅ **模块化**: 功能归属正确的模块
- ✅ **包结构规范**: 技术模块按技术分层组织（`com.atlas.system.api.v1.*`）
- ✅ **接口兼容性**: 遵循接口兼容性规则，使用版本包管理

## 功能描述

实现 `atlas-system-api` 模块，定义系统域的 API 接口契约，包括用户查询和权限查询接口，供 `atlas-auth` 服务使用。

**核心目标**:
- 创建 `atlas-system-api` 模块，定义系统域的 Feign 接口契约
- 定义用户查询接口（供 auth 服务查询用户信息）
- 定义权限查询接口（供 auth 服务查询用户权限和角色）
- 定义相关的 DTO 对象和枚举常量
- 遵循接口兼容性规则，使用版本包（v1）管理
- 模块仅包含契约和 DTO，不引入 web/db/redis 等实现层依赖

**使用场景**:
- `atlas-auth` 服务需要查询用户基础信息进行认证
- `atlas-auth` 服务需要查询用户权限和角色进行授权
- 其他服务需要调用系统服务查询用户信息

## 用户场景

### 场景 1: Auth 服务查询用户信息

**角色**: Auth 服务

**前置条件**: Auth 服务已启动，System API 模块已创建

**流程**:
1. Auth 服务接收到用户登录请求
2. Auth 服务通过 Feign 接口调用 System 服务查询用户信息
3. System 服务返回用户基本信息（用户ID、用户名、状态等）
4. Auth 服务使用返回的用户信息进行认证

**预期结果**: Auth 服务能够成功查询到用户信息

### 场景 2: Auth 服务查询用户权限

**角色**: Auth 服务

**前置条件**: Auth 服务已启动，System API 模块已创建

**流程**:
1. Auth 服务需要验证用户权限
2. Auth 服务通过 Feign 接口调用 System 服务查询用户权限
3. System 服务返回用户角色列表和权限列表
4. Auth 服务使用返回的权限信息进行授权判断

**预期结果**: Auth 服务能够成功查询到用户权限和角色信息

### 场景 3: 其他服务查询用户信息

**角色**: 业务服务（如 Order 服务）

**前置条件**: 业务服务已启动，System API 模块已创建

**流程**:
1. 业务服务需要获取用户信息
2. 业务服务通过 Feign 接口调用 System 服务查询用户信息
3. System 服务返回用户基本信息
4. 业务服务使用返回的用户信息进行业务处理

**预期结果**: 业务服务能够成功查询到用户信息

## 功能需求

### FR1: 创建 atlas-system-api 模块

**需求描述**: 创建 `atlas-system-api` 模块，作为系统服务的 API 接口定义模块。

**功能要求**:
- 创建独立的 Maven 模块 `atlas-system-api`
- 模块仅包含接口契约、DTO 对象、枚举常量
- 模块不引入 web/db/redis 等实现层依赖
- 模块可以依赖 `atlas-common-feature-core`（用于 Result、错误码等）
- 模块遵循技术模块的包结构规范（按技术分层组织）

**验收标准**:
- ✅ 模块创建成功，目录结构符合规范
- ✅ `pom.xml` 中不包含 web/db/redis 依赖
- ✅ 包结构符合技术模块规范（`com.atlas.system.api.v1.*`）

### FR2: 定义用户查询接口

**需求描述**: 定义用户查询的 Feign 接口，供 auth 服务和其他服务查询用户信息。

**功能要求**:
- 定义 `UserQueryApi` Feign 接口
- 提供根据用户ID查询用户信息的接口
- 提供根据用户名查询用户信息的接口
- 接口返回用户基本信息 DTO
- 接口使用统一的 `Result<T>` 响应格式
- 接口遵循 RESTful 设计规范

**接口定义**:
- `GET /api/v1/users/{userId}` - 根据用户ID查询用户信息
- `GET /api/v1/users/by-username?username={username}` - 根据用户名查询用户信息

**验收标准**:
- ✅ Feign 接口定义完整，包含方法签名和注解
- ✅ 接口路径符合 RESTful 规范
- ✅ 接口返回类型使用 `Result<UserDTO>`
- ✅ 接口包含完整的中文注释

### FR3: 定义权限查询接口

**需求描述**: 定义权限查询的 Feign 接口，供 auth 服务查询用户权限和角色信息。

**功能要求**:
- 定义 `PermissionQueryApi` Feign 接口
- 提供根据用户ID查询用户角色列表的接口
- 提供根据用户ID查询用户权限列表的接口
- 提供根据用户ID查询用户完整权限信息（角色+权限）的接口
- 接口返回权限信息 DTO
- 接口使用统一的 `Result<T>` 响应格式
- 接口遵循 RESTful 设计规范

**接口定义**:
- `GET /api/v1/users/{userId}/roles` - 查询用户角色列表
- `GET /api/v1/users/{userId}/permissions` - 查询用户权限列表
- `GET /api/v1/users/{userId}/authorities` - 查询用户完整权限信息（角色+权限）

**验收标准**:
- ✅ Feign 接口定义完整，包含方法签名和注解
- ✅ 接口路径符合 RESTful 规范
- ✅ 接口返回类型使用 `Result<List<String>>` 或 `Result<UserAuthoritiesDTO>`
- ✅ 接口包含完整的中文注释

### FR4: 定义用户相关 DTO

**需求描述**: 定义用户查询相关的 DTO 对象，用于接口数据传输。

**功能要求**:
- 定义 `UserDTO` - 用户基本信息 DTO
  - 包含用户ID、用户名、状态等基本信息
  - 字段必须可序列化（支持 JSON）
  - 字段必须添加完整的中文注释
- 定义 `UserAuthoritiesDTO` - 用户权限信息 DTO
  - 包含用户角色列表和权限列表
  - 字段必须可序列化（支持 JSON）
  - 字段必须添加完整的中文注释
- DTO 字段必须遵循向后兼容规则（新增字段必须可空或提供默认值）

**验收标准**:
- ✅ DTO 类定义完整，包含所有必要字段
- ✅ DTO 字段包含完整的中文注释
- ✅ DTO 支持 JSON 序列化/反序列化
- ✅ DTO 遵循向后兼容规则

### FR5: 定义枚举常量

**需求描述**: 定义用户状态、角色类型等枚举常量，供接口和 DTO 使用。

**功能要求**:
- 定义 `UserStatus` 枚举 - 用户状态枚举
  - 包含：ACTIVE（激活）、INACTIVE（未激活）、LOCKED（锁定）、DELETED（已删除）等
- 定义 `RoleType` 枚举 - 角色类型枚举（如需要）
- 枚举值必须包含完整的中文注释
- 枚举值必须可序列化（支持 JSON）

**验收标准**:
- ✅ 枚举类定义完整，包含所有必要枚举值
- ✅ 枚举值包含完整的中文注释
- ✅ 枚举支持 JSON 序列化/反序列化

### FR6: 版本包管理

**需求描述**: 使用版本包管理接口，遵循接口兼容性规则。

**功能要求**:
- 使用包名版本管理：`com.atlas.system.api.v1.*`
- Feign 接口放在 `com.atlas.system.api.v1.feign` 包下
- DTO 放在 `com.atlas.system.api.v1.dto` 包下
- 枚举常量放在 `com.atlas.system.api.v1.enums` 包下
- 遵循接口兼容性规则：
  - 不允许破坏性变更（字段删除、字段改名、字段语义改变）
  - DTO 新增字段必须向后兼容（可空或提供默认值）
  - 破坏性变更必须通过新版本包（v2）实现

**验收标准**:
- ✅ 包结构符合版本管理规范（`com.atlas.system.api.v1.*`）
- ✅ 接口和 DTO 遵循兼容性规则
- ✅ 版本包结构清晰，便于后续扩展

## 数据模型

### UserDTO（用户基本信息 DTO）

**描述**: 用户基本信息数据传输对象，用于用户查询接口的响应。

**包名**: `com.atlas.system.api.v1.dto`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 | 示例 |
|--------|------|------|------|--------|------|
| userId | Long | 用户ID | 是 | - | 1 |
| username | String | 用户名 | 是 | - | "admin" |
| nickname | String | 昵称 | 否 | null | "管理员" |
| email | String | 邮箱 | 否 | null | "admin@example.com" |
| phone | String | 手机号 | 否 | null | "13800138000" |
| status | UserStatus | 用户状态 | 是 | - | UserStatus.ACTIVE |
| avatar | String | 头像URL | 否 | null | "https://example.com/avatar.jpg" |

**约束规则**:
- userId 不能为 null
- username 不能为 null 或空字符串
- status 不能为 null
- 其他字段可以为 null（向后兼容）

### UserAuthoritiesDTO（用户权限信息 DTO）

**描述**: 用户权限信息数据传输对象，包含用户角色和权限列表。

**包名**: `com.atlas.system.api.v1.dto`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 | 示例 |
|--------|------|------|------|--------|------|
| userId | Long | 用户ID | 是 | - | 1 |
| roles | List<String> | 角色列表 | 是 | [] | ["ADMIN", "USER"] |
| permissions | List<String> | 权限列表 | 是 | [] | ["user:read", "user:write"] |

**约束规则**:
- userId 不能为 null
- roles 不能为 null（可以为空列表）
- permissions 不能为 null（可以为空列表）

### UserStatus（用户状态枚举）

**描述**: 用户状态枚举，定义用户的各种状态。

**包名**: `com.atlas.system.api.v1.enums`

**枚举值**:

| 枚举值 | 说明 | 值 |
|--------|------|-----|
| ACTIVE | 激活状态 | "ACTIVE" |
| INACTIVE | 未激活状态 | "INACTIVE" |
| LOCKED | 锁定状态 | "LOCKED" |
| DELETED | 已删除状态 | "DELETED" |

## 接口设计

### 用户查询接口

**接口名称**: `UserQueryApi`

**包名**: `com.atlas.system.api.v1.feign`

**接口定义**:

| 方法 | 路径 | 描述 | 请求参数 | 响应体 |
|------|------|------|----------|--------|
| GET | `/api/v1/users/{userId}` | 根据用户ID查询用户信息 | `@PathVariable Long userId` | `Result<UserDTO>` |
| GET | `/api/v1/users/by-username` | 根据用户名查询用户信息 | `@RequestParam String username` | `Result<UserDTO>` |

**请求示例**:
```http
GET /api/v1/users/1
GET /api/v1/users/by-username?username=admin
```

**响应示例**:
```json
{
  "code": "000000",
  "message": "操作成功",
  "data": {
    "userId": 1,
    "username": "admin",
    "nickname": "管理员",
    "email": "admin@example.com",
    "phone": "13800138000",
    "status": "ACTIVE",
    "avatar": "https://example.com/avatar.jpg"
  },
  "timestamp": 1704067200000,
  "traceId": "abc123def456"
}
```

### 权限查询接口

**接口名称**: `PermissionQueryApi`

**包名**: `com.atlas.system.api.v1.feign`

**接口定义**:

| 方法 | 路径 | 描述 | 请求参数 | 响应体 |
|------|------|------|----------|--------|
| GET | `/api/v1/users/{userId}/roles` | 查询用户角色列表 | `@PathVariable Long userId` | `Result<List<String>>` |
| GET | `/api/v1/users/{userId}/permissions` | 查询用户权限列表 | `@PathVariable Long userId` | `Result<List<String>>` |
| GET | `/api/v1/users/{userId}/authorities` | 查询用户完整权限信息 | `@PathVariable Long userId` | `Result<UserAuthoritiesDTO>` |

**请求示例**:
```http
GET /api/v1/users/1/roles
GET /api/v1/users/1/permissions
GET /api/v1/users/1/authorities
```

**响应示例**:
```json
{
  "code": "000000",
  "message": "操作成功",
  "data": {
    "userId": 1,
    "roles": ["ADMIN", "USER"],
    "permissions": ["user:read", "user:write", "user:delete"]
  },
  "timestamp": 1704067200000,
  "traceId": "abc123def456"
}
```

## 业务逻辑

### 接口契约定义

**核心原则**:
- API 模块仅定义接口契约，不包含业务逻辑实现
- 接口契约通过 Feign 接口定义，使用 `@FeignClient` 注解
- 接口路径遵循 RESTful 设计规范
- 接口返回类型使用统一的 `Result<T>` 格式

### 版本管理策略

**版本包组织**:
- 使用包名版本管理：`com.atlas.system.api.v1.*`
- 当前版本为 v1，后续破坏性变更通过 v2 实现
- 同一版本内的变更必须保持向后兼容

**兼容性规则**:
- 不允许删除接口方法
- 不允许修改接口方法签名（参数类型、返回类型）
- DTO 新增字段必须可空或提供默认值
- 不允许修改 DTO 字段类型或语义

## 异常处理

### 接口异常定义

**异常场景**:
- 用户不存在：返回错误码，data 为 null
- 参数错误：返回参数错误码，data 为 null
- 服务不可用：返回服务错误码，data 为 null

**错误码规范**:
- 使用 `atlas-common-feature-core` 模块的 `CommonErrorCode` 常量
- 系统域错误码：03 开头（如 032001 表示系统域用户相关错误）

## 依赖约束

### 允许的依赖

- `atlas-common-feature-core` - 用于 Result、错误码、异常体系
- Spring Cloud OpenFeign - 用于 Feign 接口定义
- Lombok - 用于简化代码（可选）
- Jackson - 用于 JSON 序列化（Spring Boot 自带）

### 禁止的依赖

- `atlas-common-infra-web` - Web 实现层，API 模块不应依赖
- `atlas-common-infra-db` - 数据库实现层，API 模块不应依赖
- `atlas-common-infra-redis` - Redis 实现层，API 模块不应依赖
- Spring Web - Web 实现层，API 模块不应依赖
- MyBatis-Plus - 数据库实现层，API 模块不应依赖

**理由**: API 模块仅定义接口契约和 DTO，不包含实现逻辑，因此不应依赖实现层组件。

## 测试要求

### 单元测试

- DTO 类的序列化/反序列化测试
- 枚举类的序列化/反序列化测试
- Feign 接口定义的正确性验证（通过编译检查）

### 集成测试

- Feign 接口与 System 服务的集成测试（在 System 服务中测试）
- 接口兼容性测试（验证 DTO 向后兼容）

## 成功标准

成功标准用于衡量功能是否达到预期目标，这些标准是可测量的、技术无关的，从用户和业务角度描述结果。

### 功能完整性

1. **接口契约定义完整**: 所有必需的接口（用户查询、权限查询）都已定义，接口签名清晰明确
2. **DTO 定义完整**: 所有必需的 DTO 对象（UserDTO、UserAuthoritiesDTO）都已定义，字段完整
3. **枚举定义完整**: 所有必需的枚举常量（UserStatus）都已定义，枚举值完整
4. **版本管理规范**: 包结构符合版本管理规范，便于后续扩展和维护

### 依赖约束合规性

1. **无实现层依赖**: 模块不包含 web/db/redis 等实现层依赖，仅包含契约和 DTO
2. **依赖最小化**: 模块仅依赖必要的公共模块（atlas-common-feature-core），不引入不必要的依赖

### 接口可用性

1. **接口可调用**: Auth 服务能够通过 Feign 接口成功调用用户查询和权限查询接口
2. **数据格式统一**: 所有接口返回统一的数据格式（Result<T>），便于客户端处理
3. **向后兼容**: 接口和 DTO 遵循向后兼容规则，后续变更不会破坏现有客户端

### 开发效率

1. **接口定义清晰**: 接口定义包含完整的中文注释，便于开发人员理解和使用
2. **包结构清晰**: 包结构按技术分层组织，便于代码查找和维护
3. **版本管理清晰**: 版本包结构清晰，便于后续版本扩展

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

## 实现注意事项

- [ ] 确保模块不引入 web/db/redis 依赖
- [ ] 确保所有类和方法添加中文注释
- [ ] 遵循 RESTful 设计规范
- [ ] 遵循接口兼容性规则
- [ ] 使用版本包管理（v1）
- [ ] DTO 字段必须可序列化
- [ ] 新增字段必须向后兼容（可空或提供默认值）
