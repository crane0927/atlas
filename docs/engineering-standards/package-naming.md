# 包名规范

## 概述

本文档定义了 Atlas 项目的 Java 包名规范，确保所有模块的包名结构清晰、统一，便于代码组织和管理。

## 根包名

**根包名**: `com.atlas`

所有 Atlas 项目的 Java 类必须位于 `com.atlas` 包或其子包下。

## 业务模块包名结构

### 标准结构

业务模块的包名遵循以下结构：

```
com.atlas.{module-name}.{layer}
```

### 模块名称

模块名称必须与项目模块目录名称一致（去除 `atlas-` 前缀）：

- `atlas-system` → `com.atlas.system`
- `atlas-gateway` → `com.atlas.gateway`
- `atlas-auth` → `com.atlas.auth`

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

**atlas-system 模块包名示例**:

```java
// Controller 层
package com.atlas.system.controller;

// Service 层 - 接口
package com.atlas.system.service;

// Service 层 - 实现类
package com.atlas.system.service.impl;

// Mapper 层
package com.atlas.system.mapper;

// Model 层 - Entity（数据实体类）
package com.atlas.system.model.entity;

// Model 层 - DTO（数据传输对象）
package com.atlas.system.model.dto;

// Model 层 - VO（视图对象）
package com.atlas.system.model.vo;

// Config 层
package com.atlas.system.config;

// Util 层（模块内部工具类）
package com.atlas.system.util;
```

## 公共模块包名结构

### 基础设施模块（atlas-common-infra-*）

基础设施模块的包名结构：

```
com.atlas.common.infra.{module-name}
```

**示例**:

```java
// Web 相关工具
package com.atlas.common.infra.web;

// Redis 相关工具
package com.atlas.common.infra.redis;

// 数据库相关工具
package com.atlas.common.infra.db;

// 日志相关工具
package com.atlas.common.infra.logging;
```

### 功能特性模块（atlas-common-feature-*）

功能特性模块的包名结构：

```
com.atlas.common.feature.{module-name}
```

**示例**:

```java
// 核心工具类
package com.atlas.common.feature.core;

// 安全相关工具
package com.atlas.common.feature.security;
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

| 模块目录 | 根包名 | 说明 |
|---------|--------|------|
| `atlas-gateway` | `com.atlas.gateway` | API 网关模块 |
| `atlas-auth` | `com.atlas.auth` | 认证授权模块 |
| `atlas-system` | `com.atlas.system` | 系统管理模块 |
| `atlas-common-infra-web` | `com.atlas.common.infra.web` | Web 基础设施模块 |
| `atlas-common-infra-redis` | `com.atlas.common.infra.redis` | Redis 基础设施模块 |
| `atlas-common-infra-db` | `com.atlas.common.infra.db` | 数据库基础设施模块 |
| `atlas-common-infra-logging` | `com.atlas.common.infra.logging` | 日志基础设施模块 |
| `atlas-common-feature-core` | `com.atlas.common.feature.core` | 核心功能特性模块 |
| `atlas-common-feature-security` | `com.atlas.common.feature.security` | 安全功能特性模块 |

## 代码示例

### 完整的包结构示例

**atlas-system 模块完整包结构**:

```
com.atlas.system
├── controller
│   ├── UserController.java
│   └── RoleController.java
├── service
│   ├── UserService.java
│   ├── RoleService.java
│   └── impl
│       ├── UserServiceImpl.java
│       └── RoleServiceImpl.java
├── mapper
│   ├── UserMapper.java
│   └── RoleMapper.java
├── model
│   ├── entity
│   │   ├── User.java
│   │   └── Role.java
│   ├── dto
│   │   ├── UserDTO.java
│   │   └── RoleDTO.java
│   └── vo
│       ├── UserVO.java
│       └── RoleVO.java
├── config
│   └── SystemConfig.java
└── util
    └── SystemUtil.java
```

### 类定义示例

```java
/**
 * 用户控制器
 * 
 * @author Atlas Team
 * @date 2025-01-27
 */
package com.atlas.system.controller;

import com.atlas.system.model.dto.UserDTO;
import com.atlas.system.model.vo.UserVO;
import com.atlas.system.service.UserService;
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
package com.atlas.system.service;

import com.atlas.system.model.dto.UserDTO;
import com.atlas.system.model.vo.UserVO;

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
package com.atlas.system.service.impl;

import com.atlas.system.mapper.UserMapper;
import com.atlas.system.model.dto.UserDTO;
import com.atlas.system.model.vo.UserVO;
import com.atlas.system.service.UserService;
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
package com.atlas.system.model.entity;

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
package com.atlas.system.model.dto;

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
package com.atlas.system.model.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    // 其他字段
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
package com.atlas.system.controller.user;
package com.atlas.system.service.user;
```

### Q2: 工具类应该放在哪个包？

**A**: 
- 模块内部使用的工具类：`com.atlas.{module-name}.util`
- 跨模块使用的工具类：`com.atlas.common.feature.core.util`

### Q3: 配置类应该放在哪个包？

**A**: 配置类应放在 `com.atlas.{module-name}.config` 包下。

## 参考

- [Java 包命名规范](https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html)
- [项目宪法 - 模块化设计原则](.specify/memory/constitution.md)

