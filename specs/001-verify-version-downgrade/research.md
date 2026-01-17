# 技术调研文档

## 调研目标

确定 Spring Boot 3.5.9、Spring Cloud 2025.0.1、Spring Cloud Alibaba 2025.0.0.0 版本降级后的兼容性问题和解决方案。

## 版本兼容性调研

### Spring Boot 3.5.9 兼容性

**调研内容**:
- Spring Boot 3.5.9 与 JDK 21 的兼容性
- Spring Boot 3.5.9 与 Spring Cloud 2025.0.1 的兼容性
- Spring Boot 3.5.9 与第三方依赖的兼容性

**决策**: 使用 Spring Boot 3.5.9 版本

**理由**:
1. Spring Boot 3.5.9 是 Spring Boot 3.x 系列的稳定版本
2. 与 JDK 21 完全兼容
3. 与 Spring Cloud 2025.0.1 兼容
4. 提供了稳定的框架特性和安全补丁

**已知兼容性问题**:
- 需要验证 MyBatis-Plus 3.5.8 与 Spring Boot 3.5.9 的兼容性
- 需要验证 PostgreSQL 驱动 42.7.4 与 Spring Boot 3.5.9 的兼容性
- 需要验证 Flyway 与 Spring Boot 3.5.9 的兼容性

### Spring Cloud 2025.0.1 兼容性

**调研内容**:
- Spring Cloud 2025.0.1 与 Spring Boot 3.5.9 的兼容性
- Spring Cloud 2025.0.1 各组件的版本兼容性

**决策**: 使用 Spring Cloud 2025.0.1 版本

**理由**:
1. Spring Cloud 2025.0.1 与 Spring Boot 3.5.9 兼容
2. 提供了完整的微服务解决方案
3. 版本统一确保微服务组件之间的兼容性

**已知兼容性问题**:
- 需要验证 Spring Cloud Gateway 与 Spring Boot 3.5.9 的兼容性
- 需要验证 OpenFeign 与 Spring Boot 3.5.9 的兼容性
- 需要验证 Spring Cloud LoadBalancer 与 Spring Boot 3.5.9 的兼容性

### Spring Cloud Alibaba 2025.0.0.0 兼容性

**调研内容**:
- Spring Cloud Alibaba 2025.0.0.0 与 Spring Boot 3.5.9 的兼容性
- Spring Cloud Alibaba 2025.0.0.0 与 Spring Cloud 2025.0.1 的兼容性
- Nacos 版本兼容性

**决策**: 使用 Spring Cloud Alibaba 2025.0.0.0 版本

**理由**:
1. Spring Cloud Alibaba 2025.0.0.0 与 Spring Boot 3.5.9 和 Spring Cloud 2025.0.1 兼容
2. 提供了 Nacos 配置中心和服务发现功能
3. 版本统一确保组件之间的兼容性

**已知兼容性问题**:
- 需要验证 Nacos Config 与 Spring Boot 3.5.9 的兼容性
- 需要验证 Nacos Discovery 与 Spring Boot 3.5.9 的兼容性
- 需要确认 Nacos 服务器版本要求

### 第三方依赖兼容性

**MyBatis-Plus 3.5.8**:
- **决策**: 继续使用 MyBatis-Plus 3.5.8
- **理由**: MyBatis-Plus 3.5.8 与 Spring Boot 3.5.9 兼容
- **验证**: 需要在实际构建中验证

**PostgreSQL 驱动 42.7.4**:
- **决策**: 继续使用 PostgreSQL 驱动 42.7.4
- **理由**: PostgreSQL 驱动 42.7.4 与 Spring Boot 3.5.9 兼容
- **验证**: 需要在实际构建中验证

**Flyway**:
- **决策**: 使用 Flyway（版本由 Spring Boot 管理）
- **理由**: Flyway 与 Spring Boot 3.5.9 兼容
- **验证**: 需要在实际构建中验证数据库迁移功能

**Lombok 1.18.34**:
- **决策**: 继续使用 Lombok 1.18.34
- **理由**: Lombok 1.18.34 与 Spring Boot 3.5.9 和 JDK 21 兼容
- **验证**: 需要在实际构建中验证

## API 变更调研

### Spring Boot 3.5.9 vs 4.0.1 API 变更

**调研内容**:
- 检查代码中是否使用了 Spring Boot 4.0.1 特有的 API
- 检查是否有已废弃的 API 需要更新

**决策**: 在验证过程中检查并修复 API 变更问题

**已知变更**:
- 需要检查配置属性是否有变更
- 需要检查自动配置类是否有变更
- 需要检查启动类是否有变更

**应对措施**:
- 运行构建，检查是否有编译错误
- 检查代码中是否使用了已废弃的 API
- 如有问题，更新为新的 API

## 配置文件变更调研

### Spring Boot 配置文件格式

**调研内容**:
- Spring Boot 3.5.9 对配置文件格式的要求
- YAML 配置文件语法是否有变更

**决策**: 继续使用 YAML 格式配置文件

**理由**:
1. Spring Boot 3.5.9 支持 YAML 格式配置文件
2. 项目已使用 YAML 格式，符合项目宪法要求
3. YAML 格式便于管理复杂配置结构

**已知变更**:
- 需要验证配置文件语法是否符合 Spring Boot 3.5.9 要求
- 需要检查配置属性名称是否有变更

**应对措施**:
- 验证配置文件能够正常加载
- 如有问题，更新配置文件格式或属性名称

## 数据库迁移工具兼容性

### Flyway 兼容性

**调研内容**:
- Flyway 版本与 Spring Boot 3.5.9 的兼容性
- Flyway 迁移脚本格式要求

**决策**: 继续使用 Flyway 进行数据库迁移

**理由**:
1. Flyway 与 Spring Boot 3.5.9 兼容
2. 项目已使用 Flyway 管理数据库迁移
3. Flyway 版本由 Spring Boot 管理，确保兼容性

**已知变更**:
- 需要验证 Flyway 迁移脚本能够正常执行
- 需要检查 Flyway 配置是否有变更

**应对措施**:
- 验证数据库迁移脚本能够正常执行
- 如有问题，更新 Flyway 配置或迁移脚本

## 服务注册与发现兼容性

### Nacos 兼容性

**调研内容**:
- Spring Cloud Alibaba 2025.0.0.0 与 Nacos 的兼容性
- Nacos 服务器版本要求

**决策**: 继续使用 Nacos 作为配置中心和服务发现

**理由**:
1. Spring Cloud Alibaba 2025.0.0.0 支持 Nacos
2. 项目已使用 Nacos，符合项目宪法要求
3. Nacos 是 Spring Cloud Alibaba 官方推荐的组件

**已知变更**:
- 需要验证服务能够正常注册到 Nacos
- 需要验证配置能够正常从 Nacos 读取

**应对措施**:
- 测试服务注册功能
- 测试配置读取功能
- 如有问题，检查 Nacos 服务器版本和配置

## 验证策略

### 构建验证策略

**方法**: 执行 `mvn clean install` 命令

**验证点**:
- 构建是否成功完成
- 是否有编译错误
- 是否有依赖冲突
- 构建产物是否正常生成

### 编译验证策略

**方法**: 对每个模块执行 `mvn clean compile` 命令

**验证点**:
- 每个模块是否能够成功编译
- 是否有依赖解析失败
- 是否有版本冲突

### 启动验证策略

**方法**: 启动各个服务，检查启动日志

**验证点**:
- 服务是否能够成功启动
- 启动日志中是否有异常
- 服务是否能够注册到 Nacos
- 健康检查是否正常

### 功能验证策略

**方法**: 通过 HTTP 请求测试各个功能

**验证点**:
- Gateway 路由转发是否正常
- Auth 登录功能是否正常
- System 用户查询是否正常
- 服务间调用是否正常

### 兼容性验证策略

**方法**: 检查依赖树和运行时错误

**验证点**:
- Maven 依赖树中是否有版本冲突
- 运行时是否有 ClassNotFoundException
- 运行时是否有 NoSuchMethodError
- 第三方依赖是否兼容

## 参考资料

- [Spring Boot 3.5.9 官方文档](https://spring.io/projects/spring-boot)
- [Spring Cloud 2025.0.1 官方文档](https://spring.io/projects/spring-cloud)
- [Spring Cloud Alibaba 2025.0.0.0 官方文档](https://github.com/alibaba/spring-cloud-alibaba)
- [MyBatis-Plus 3.5.8 官方文档](https://baomidou.com/)
- [PostgreSQL JDBC 驱动文档](https://jdbc.postgresql.org/)
- [Flyway 官方文档](https://flywaydb.org/)
