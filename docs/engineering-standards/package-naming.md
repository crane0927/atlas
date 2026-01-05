# 包名规范

## 概述

本文档定义了 Atlas 项目的 Java 包名规范，确保所有模块的包名结构清晰、统一，便于代码组织和管理。

## 根包名

**根包名**: `com.atlas`

所有 Atlas 项目的 Java 类必须位于 `com.atlas` 包或其子包下。

## 业务模块包名结构

### 标准结构

业务模块的包名遵循以下结构（**按业务模块再按技术分层**）：

```
com.atlas.{module-name}.{business-module}.{layer}
```

**说明**: 业务模块优先按业务领域组织，同一业务模块的所有代码（controller、service、mapper）聚合在一起，便于业务功能开发和维护。

### 模块名称

模块名称必须与项目模块目录名称一致（去除 `atlas-` 前缀）：

- `atlas-system` → `com.atlas.system`
- `atlas-auth` → `com.atlas.auth`
- `atlas-order` → `com.atlas.order`

**注意**: `atlas-gateway` 属于技术模块，不在此列，详见"技术模块包名结构"部分。

### 分层结构

标准分层结构包括：

- **controller**: Controller 层，处理 HTTP 请求
- **service**: Service 层，业务逻辑实现
  - **impl**: Service 接口实现类
- **mapper**: Mapper 层，数据库访问（MyBatis-Plus）
- **model**: Model 层，数据模型（包含 entity、dto、vo）
  - **entity**: 数据实体类（对应数据库表）
  - **dto**: 数据传输对象（用于接口传输）
  - **vo**: 视图对象（用于前端展示）
- **config**: Config 层，配置类
- **util**: Util 层，工具类（模块内部使用）

### 示例

**atlas-system 模块包名示例**（按业务模块再按技术分层）:

```java
// 用户业务模块
package com.atlas.system.user.controller;
package com.atlas.system.user.service;
package com.atlas.system.user.service.impl;
package com.atlas.system.user.mapper;
package com.atlas.system.user.model.entity;
package com.atlas.system.user.model.dto;
package com.atlas.system.user.model.vo;

// 订单业务模块
package com.atlas.system.order.controller;
package com.atlas.system.order.service;
package com.atlas.system.order.mapper;

// 模块级配置和工具类（不属于具体业务模块）
package com.atlas.system.config;
package com.atlas.system.util;
```

## 技术模块包名结构

技术模块的包名遵循以下结构（**按技术分层再按业务模块**）：

### 独立技术模块（如 atlas-gateway）

独立技术模块的包名结构：

```
com.atlas.{module-name}.{layer}
```

**示例**（atlas-gateway 模块）:

```java
// 配置类
package com.atlas.gateway.config;

// 过滤器
package com.atlas.gateway.filter;

// 异常处理
package com.atlas.gateway.exception;
```

**说明**: 技术模块以技术层为主，相同技术层的代码集中在一起，便于统一管理和规范。

### 公共技术模块（atlas-common-infra-*）

公共基础设施模块的包名结构：

```
com.atlas.common.infra.{module-name}.{layer}
```

**示例**:

```java
// Web 基础设施模块
package com.atlas.common.infra.web.config;
package com.atlas.common.infra.web.filter;
package com.atlas.common.infra.web.exception;

// Redis 基础设施模块
package com.atlas.common.infra.redis.config;
package com.atlas.common.infra.redis.util;

// 数据库基础设施模块
package com.atlas.common.infra.db.config;
package com.atlas.common.infra.db.entity;

// 日志基础设施模块
package com.atlas.common.infra.logging.config;
package com.atlas.common.infra.logging.trace;
package com.atlas.common.infra.logging.desensitize;
```

### 功能特性模块（atlas-common-feature-*）

功能特性模块的包名结构：

```
com.atlas.common.feature.{module-name}.{layer}
```

**示例**:

```java
// 核心功能特性模块
package com.atlas.common.feature.core.result;
package com.atlas.common.feature.core.exception;
package com.atlas.common.feature.core.util;

// 安全功能特性模块
package com.atlas.common.feature.security.annotation;
package com.atlas.common.feature.security.context;
package com.atlas.common.feature.security.user;
```

## 包名规范规则

### 命名规则

1. **小写字母**: 包名必须全部使用小写字母
2. **点分隔**: 使用点（`.`）分隔包名层级
3. **有意义的名称**: 包名应具有明确的含义，反映其功能
4. **避免缩写**: 除非是广泛认知的缩写（如 `util`、`config`），否则避免使用缩写

### 禁止事项

1. **禁止使用 Java 保留关键字**: 如 `class`、`package`、`import` 等
2. **禁止使用数字开头**: 包名不能以数字开头
3. **禁止使用特殊字符**: 只能使用字母、数字、下划线（不推荐）和点
4. **禁止过深的嵌套**: 包名层级不应超过 6 层

### 验证规则

- 包名必须符合 Java 包名规范
- 包名必须与模块结构一致
- 禁止使用保留关键字

## 包名与模块对应关系

### 业务模块

| 模块目录 | 根包名 | 包结构组织方式 | 说明 |
|---------|--------|---------------|------|
| `atlas-system` | `com.atlas.system` | 按业务模块再按技术分层 | 系统管理模块 |
| `atlas-auth` | `com.atlas.auth` | 按业务模块再按技术分层 | 认证授权模块 |
| `atlas-order` | `com.atlas.order` | 按业务模块再按技术分层 | 订单管理模块 |

**示例**: `com.atlas.system.user.controller`、`com.atlas.system.order.service`

### 技术模块

| 模块目录 | 根包名 | 包结构组织方式 | 说明 |
|---------|--------|---------------|------|
| `atlas-gateway` | `com.atlas.gateway` | 按技术分层 | API 网关模块（独立技术模块） |
| `atlas-common-infra-web` | `com.atlas.common.infra.web` | 按技术分层 | Web 基础设施模块 |
| `atlas-common-infra-redis` | `com.atlas.common.infra.redis` | 按技术分层 | Redis 基础设施模块 |
| `atlas-common-infra-db` | `com.atlas.common.infra.db` | 按技术分层 | 数据库基础设施模块 |
| `atlas-common-infra-logging` | `com.atlas.common.infra.logging` | 按技术分层 | 日志基础设施模块 |
| `atlas-common-feature-core` | `com.atlas.common.feature.core` | 按技术分层 | 核心功能特性模块 |
| `atlas-common-feature-security` | `com.atlas.common.feature.security` | 按技术分层 | 安全功能特性模块 |

**示例**: `com.atlas.gateway.config`、`com.atlas.common.infra.web.filter`

## 代码示例

### 完整的包结构示例

**atlas-system 模块完整包结构**（按业务模块再按技术分层）:

```
com.atlas.system
├── user                          # 用户业务模块
│   ├── controller
│   │   └── UserController.java
│   ├── service
│   │   ├── UserService.java
│   │   └── impl
│   │       └── UserServiceImpl.java
│   ├── mapper
│   │   └── UserMapper.java
│   └── model
│       ├── entity
│       │   └── User.java
│       ├── dto
│       │   └── UserDTO.java
│       └── vo
│           └── UserVO.java
├── order                         # 订单业务模块
│   ├── controller
│   │   └── OrderController.java
│   ├── service
│   │   └── OrderService.java
│   └── mapper
│       └── OrderMapper.java
├── config                        # 模块级配置（不属于具体业务模块）
│   └── SystemConfig.java
└── util                          # 模块级工具类（不属于具体业务模块）
    └── SystemUtil.java
```

**atlas-gateway 模块完整包结构**（按技术分层，技术模块）:

```
com.atlas.gateway
├── config                        # 配置类
│   ├── GatewayConfig.java
│   ├── GatewayProperties.java
│   ├── CorsConfig.java
│   └── NacosConfigRefreshListener.java
├── filter                        # 过滤器
│   ├── TraceIdGatewayFilter.java
│   ├── AuthGatewayFilter.java
│   ├── TokenValidator.java
│   └── DefaultTokenValidator.java
└── exception                     # 异常处理
    └── GatewayExceptionHandler.java
```

### 类定义示例

**业务模块示例**（按业务模块再按技术分层）:

```java
/**
 * 用户控制器
 * 
 * @author Atlas Team
 * @date 2025-01-27
 */
package com.atlas.system.user.controller;

import com.atlas.system.user.model.dto.UserDTO;
import com.atlas.system.user.model.vo.UserVO;
import com.atlas.system.user.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;
    
    // 使用 DTO 接收请求参数
    public UserVO createUser(UserDTO userDTO) {
        // 实现代码
    }
}
```

```java
/**
 * 用户服务接口
 * 
 * @author Atlas Team
 * @date 2025-01-27
 */
package com.atlas.system.user.service;

import com.atlas.system.user.model.dto.UserDTO;
import com.atlas.system.user.model.vo.UserVO;

public interface UserService {
    UserVO createUser(UserDTO userDTO);
    UserVO getUserById(Long id);
}
```

```java
/**
 * 用户服务实现类
 * 
 * @author Atlas Team
 * @date 2025-01-27
 */
package com.atlas.system.user.service.impl;

import com.atlas.system.user.mapper.UserMapper;
import com.atlas.system.user.model.dto.UserDTO;
import com.atlas.system.user.model.vo.UserVO;
import com.atlas.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    
    @Override
    public UserVO createUser(UserDTO userDTO) {
        // 实现代码
        return null;
    }
    
    @Override
    public UserVO getUserById(Long id) {
        // 实现代码
        return null;
    }
}
```

```java
/**
 * 用户实体类
 * 
 * @author Atlas Team
 * @date 2025-01-27
 */
package com.atlas.system.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user")
public class User {
    private Long id;
    private String username;
    // 其他字段
}
```

```java
/**
 * 用户数据传输对象
 * 
 * @author Atlas Team
 * @date 2025-01-27
 */
package com.atlas.system.user.model.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String email;
    // 其他字段
}
```

```java
/**
 * 用户视图对象
 * 
 * @author Atlas Team
 * @date 2025-01-27
 */
package com.atlas.system.user.model.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    // 其他字段
}
```

**技术模块示例**（按技术分层）:

```java
/**
 * Gateway 配置类
 * 
 * @author Atlas Team
 * @date 2025-01-27
 */
package com.atlas.gateway.config;

import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    // 配置代码
}
```

```java
/**
 * TraceId Gateway 过滤器
 * 
 * @author Atlas Team
 * @date 2025-01-27
 */
package com.atlas.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;

@Component
public class TraceIdGatewayFilter implements GlobalFilter, Ordered {
    // 实现代码
}
```

## 迁移指南

如果现有代码不符合本规范，请按以下步骤迁移：

1. **识别不符合规范的包名**: 检查现有代码中的包名
2. **规划迁移方案**: 确定新的包名结构
3. **批量重命名**: 使用 IDE 的重构功能批量重命名包
4. **更新导入语句**: 更新所有相关的 import 语句
5. **验证构建**: 确保迁移后项目可以正常构建

## 工具支持

### IDE 配置

**IntelliJ IDEA**:
- 使用 `Refactor` → `Rename` 重命名包
- 使用 `Refactor` → `Move` 移动类到新包

**Eclipse**:
- 使用 `Refactor` → `Rename` 重命名包
- 使用 `Refactor` → `Move` 移动类到新包

### 代码检查工具

可以使用以下工具检查包名规范：

- **Checkstyle**: 配置包名检查规则
- **Spotless**: 代码格式化时检查包名
- **Maven Enforcer**: 强制包名规范

## 常见问题

### Q1: 子模块内部是否可以创建子包？

**A**: 可以。在标准分层（controller、service、mapper 等）下，可以创建子包进行进一步组织，例如：

```java
package com.atlas.system.user.controller;
package com.atlas.system.user.service;
```

### Q2: 工具类应该放在哪个包？

**A**: 
- 模块内部使用的工具类：`com.atlas.{module-name}.util`
- 跨模块使用的工具类：`com.atlas.common.feature.core.util`

### Q3: 配置类应该放在哪个包？

**A**: 
- **业务模块**: 配置类应放在 `com.atlas.{module-name}.config` 包下（模块级配置）
- **技术模块**: 配置类应放在 `com.atlas.{module-name}.config` 包下（如 `com.atlas.gateway.config`）

### Q4: Gateway 模块的包结构如何组织？

**A**: Gateway 属于技术模块，按技术分层组织：
- `com.atlas.gateway.config` - 配置类
- `com.atlas.gateway.filter` - 过滤器
- `com.atlas.gateway.exception` - 异常处理

详见"技术模块包名结构"部分。

## 参考

- [Java 包命名规范](https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html)
- [项目宪法 - 模块化设计原则](.specify/memory/constitution.md)

