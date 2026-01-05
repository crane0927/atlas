# atlas-test-module

## 模块简介

`atlas-test-module` 是 Atlas 项目的测试模块，用于验证父 POM 继承和插件配置的正确性。该模块主要用于：
- 验证父 POM 的依赖管理和版本控制
- 验证 Maven Enforcer 插件的版本检查功能
- 验证 Spotless 插件的代码格式化功能
- 验证 Spring Boot Maven Plugin 的打包功能
- 作为项目模板和示例参考

## 主要功能

### 1. 父 POM 继承验证

验证子模块是否正确继承父 POM，包括：
- Java 版本检查（JDK 21）
- 依赖版本管理
- 插件配置继承

### 2. Maven Enforcer 验证

验证 Maven Enforcer 插件是否正常工作：
- Java 版本强制检查
- 依赖版本一致性检查
- 仓库配置检查

### 3. Spotless 代码格式化验证

验证 Spotless 插件是否正常工作：
- 代码格式化检查
- 格式化规则应用

### 4. Spring Boot 应用示例

提供一个简单的 Spring Boot 应用示例：
- 基本的 Controller 实现
- RESTful API 示例
- 应用启动类

## 快速开始

### 运行应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/atlas-test-module-1.0.0.jar
```

### 测试 API

应用启动后，可以访问以下接口：

```bash
# 测试接口
curl http://localhost:8081/test
```

### 验证插件功能

```bash
# 验证 Enforcer 插件
mvn enforcer:enforce

# 验证 Spotless 插件
mvn spotless:check

# 格式化代码
mvn spotless:apply
```

## 模块结构

```
atlas-test-module/
├── pom.xml                    # 模块 POM 配置
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/atlas/test/
│   │   │       ├── TestApplication.java      # 应用启动类
│   │   │       └── controller/
│   │   │           └── TestController.java  # 测试 Controller
│   │   └── resources/
│   │       └── application.yaml              # 应用配置（YAML 格式）
│   └── test/
│       └── java/
│           └── com/atlas/test/
│               └── TestApplicationTest.java  # 应用测试类
└── README.md                  # 本文件
```

## 配置说明

### application.yaml

```yaml
# Spring Boot 应用配置
spring:
  application:
    name: atlas-test-module

# 服务器配置
server:
  port: 8081
```

## 相关文档

- [项目宪法](../.specify/memory/constitution.md) - 项目核心原则和规范
- [根工程版本治理](../specs/1-root-version-governance/quickstart.md) - 父 POM 配置说明
- [工程规范文档](../docs/engineering-standards/) - 包名、日志、错误码等规范

## 技术栈

- **Java**: JDK 21
- **Spring Boot**: 4.0.1
- **Maven**: 3.8+
- **Lombok**: 简化代码

## 注意事项

- 本模块仅用于测试和验证，不建议在生产环境中使用
- 可以作为创建新模块的参考模板
- 所有配置必须符合项目宪法要求

## 版本信息

- **当前版本**: 1.0.0
- **最后更新**: 2026-01-05

