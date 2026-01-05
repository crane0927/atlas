# 快速开始指南

## 概述

本指南帮助开发人员快速了解和使用 Atlas 项目的根工程和版本治理体系。

## 前置要求

- JDK 21 已安装并配置
- Maven 3.8+ 已安装并配置
- 熟悉 Maven 项目结构

## 快速开始

### 1. 继承父 POM

在子模块的 `pom.xml` 中继承父 POM：

```xml
<parent>
    <groupId>com.atlas</groupId>
    <artifactId>atlas</artifactId>
    <version>1.0.0</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

### 2. 使用依赖（无需指定版本）

```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <!-- 版本由父 POM 管理，无需指定 -->
    </dependency>
    
    <!-- MyBatis-Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <!-- 版本由父 POM 管理，无需指定 -->
    </dependency>
</dependencies>
```

### 3. 遵循包名规范

**业务模块包名结构**:
```java
package com.atlas.system.controller;  // Controller 层
package com.atlas.system.service;     // Service 层
package com.atlas.system.mapper;      // Mapper 层
```

**公共模块包名结构**:
```java
package com.atlas.common.infra.web;        // 基础设施模块
package com.atlas.common.feature.core;    // 功能特性模块
```

### 4. 使用错误码

**错误码格式**: `MMTTSS`（6 位数字）

**示例**:
```java
// 系统管理模块（03）的业务错误（20）第 1 个错误（01）
public static final String ERROR_SYSTEM_USER_NOT_FOUND = "032001";

// 认证模块（02）的权限错误（30）第 1 个错误（01）
public static final String ERROR_AUTH_TOKEN_INVALID = "023001";
```

### 5. 配置 Nacos

**DataId 命名**: `{application-name}-{profile}.yaml`
- 示例: `atlas-system-dev.yaml`

**Group 命名**: 按环境分组
- 开发环境: `DEV_GROUP`
- 测试环境: `TEST_GROUP`
- 生产环境: `PROD_GROUP`

**配置项 Key**: `{module}.{category}.{key}`
- 示例: `atlas.system.database.url`

### 6. 日志配置

**日志格式**: 已通过父 POM 统一配置，包含 TraceId 输出

**使用示例**:
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    public void createUser(User user) {
        log.info("创建用户: {}", user.getUsername());
        // 日志会自动包含 TraceId
    }
}
```

## 常见问题

### Q1: 如何添加新的依赖？

**A**: 在父 POM 的 `<dependencyManagement>` 中添加依赖版本，子模块直接引用即可。

### Q2: 如何升级依赖版本？

**A**: 在父 POM 中修改版本号，所有子模块自动使用新版本。

### Q3: 如何分配新的错误码段位？

**A**: 查阅错误码规范文档，选择未使用的模块码，并在文档中登记。

### Q4: 如何验证规范遵循情况？

**A**: 
- 运行 `mvn clean install` 检查构建
- 使用 Maven Enforcer 插件检查依赖版本
- 使用 Spotless 检查代码格式
- 代码审查时检查规范遵循情况

### 7. 启用插件检查

在子模块的 `pom.xml` 中启用 Maven Enforcer 和 Spotless 插件：

```xml
<build>
    <plugins>
        <!-- Maven Enforcer Plugin - 启用版本检查 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-enforcer-plugin</artifactId>
            <executions>
                <execution>
                    <id>enforce</id>
                    <goals>
                        <goal>enforce</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

        <!-- Spotless Maven Plugin - 启用代码格式化 -->
        <plugin>
            <groupId>com.diffplug.spotless</groupId>
            <artifactId>spotless-maven-plugin</artifactId>
            <executions>
                <execution>
                    <id>spotless-check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 8. 验证父 POM 继承

**验证步骤**:

1. **检查依赖版本**: 运行 `mvn dependency:tree` 查看依赖树，确认版本由父 POM 管理
2. **检查 Enforcer**: 运行 `mvn enforcer:enforce` 验证版本检查规则
3. **检查 Spotless**: 运行 `mvn spotless:check` 验证代码格式
4. **构建测试**: 运行 `mvn clean compile` 验证构建成功

**测试模块示例**:

参考 `atlas-test-module` 模块，这是一个完整的测试模块示例，展示了如何：
- 继承父 POM
- 使用依赖（无需指定版本）
- 启用插件检查
- 遵循包名规范

## 常见问题

### Q1: 如何添加新的依赖？

**A**: 在父 POM 的 `<dependencyManagement>` 中添加依赖版本，子模块直接引用即可。

### Q2: 如何升级依赖版本？

**A**: 在父 POM 中修改版本号，所有子模块自动使用新版本。

### Q3: 如何分配新的错误码段位？

**A**: 查阅错误码规范文档，选择未使用的模块码，并在文档中登记。

### Q4: 如何验证规范遵循情况？

**A**: 
- 运行 `mvn clean install` 检查构建
- 使用 Maven Enforcer 插件检查依赖版本
- 使用 Spotless 检查代码格式
- 代码审查时检查规范遵循情况

### Q5: Spring Cloud Alibaba 2025.1.0 版本不可用怎么办？

**A**: 
- 该版本可能尚未发布到 Maven 中央仓库
- 在父 POM 中已添加注释说明
- 待版本发布后，取消注释即可使用
- 或根据实际可用版本调整配置

### Q6: Maven Enforcer 检查失败怎么办？

**A**: 
- 检查 Java 版本是否符合要求（JDK 21）
- 检查依赖版本是否有冲突
- 查看 Enforcer 错误信息，根据提示修复问题

## 下一步

1. 阅读完整的工程规范文档
2. 参考 `atlas-test-module` 创建第一个子模块
3. 验证父 POM 继承和插件配置
4. 配置 IDE 支持代码格式化（Spotless）
5. 参与代码审查，确保规范执行

## 相关文档

- [包名规范文档](../../docs/engineering-standards/package-naming.md)
- [日志格式规范文档](../../docs/engineering-standards/logging-format.md)
- [错误码规范文档](../../docs/engineering-standards/error-code.md)
- [配置命名规范文档](../../docs/engineering-standards/config-naming.md)
- [版本管理说明文档](../../docs/governance/version-management.md)

