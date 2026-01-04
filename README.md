# Atlas 项目

## 项目简介

Atlas 是一个基于 Spring Boot 和 Spring Cloud 的企业级快速开发框架，旨在提供高效、规范、可维护的微服务开发解决方案。

## 技术栈

- **Java**: JDK 21 (LTS)
- **Spring Boot**: 4.0.1
- **Spring Cloud**: 2025.1.0
- **Spring Cloud Alibaba**: 2025.1.0
- **数据库**: PostgreSQL
- **ORM**: MyBatis-Plus
- **配置中心**: Nacos
- **构建工具**: Maven 3.8+

## 项目结构

```
atlas/
├── atlas-gateway/        # API 网关
├── atlas-auth/           # 认证授权服务
├── atlas-common/         # 公共模块
│   ├── atlas-common-infra/     # 基础设施模块
│   │   ├── atlas-common-infra-web/      # Web 相关工具
│   │   ├── atlas-common-infra-redis/    # Redis 相关工具
│   │   ├── atlas-common-infra-db/       # 数据库相关工具
│   │   └── atlas-common-infra-logging/  # 日志相关
│   └── atlas-common-feature/   # 功能特性模块
│       ├── atlas-common-feature-core/      # 核心工具类
│       └── atlas-common-feature-security/   # 安全相关
├── atlas-service/        # 服务模块
│   ├── atlas-system/     # 系统管理服务
│   └── ... 
└── atlas-service-api/        # API 接口定义
    ├── atlas-system-api/     # 系统管理服务 API
    └── ...
```

## 快速开始

### 前置要求

- JDK 21
- Maven 3.8+
- PostgreSQL（如需要）

### 构建项目

```bash
mvn clean install
```

### 子模块继承父 POM

在子模块的 `pom.xml` 中继承父 POM：

```xml
<parent>
    <groupId>com.atlas</groupId>
    <artifactId>atlas</artifactId>
    <version>1.0.0</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

## 工程规范

详细的工程规范文档请参考：

- [包名规范](docs/engineering-standards/package-naming.md)
- [日志格式规范](docs/engineering-standards/logging-format.md)
- [错误码规范](docs/engineering-standards/error-code.md)
- [配置命名规范](docs/engineering-standards/config-naming.md)

## 项目宪法

项目遵循统一的开发规范和治理规则，详见：[项目宪法](.specify/memory/constitution.md)

## 开发指南

1. 阅读[项目宪法](.specify/memory/constitution.md)了解核心原则
2. 查阅[工程规范文档](docs/engineering-standards/)了解开发规范
3. 参考[快速开始指南](specs/1-root-version-governance/quickstart.md)开始开发

## 贡献指南

1. 遵循项目宪法和工程规范
2. 代码注释使用中文
3. 提交前运行 `mvn clean install` 确保构建通过
4. 代码审查时检查规范遵循情况

## 许可证

[待定]

