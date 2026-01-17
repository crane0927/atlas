# 快速开始指南

## 概述

本指南介绍如何执行版本降级验证，确保项目在 Spring Boot 3.5.9、Spring Cloud 2025.0.1、Spring Cloud Alibaba 2025.0.0.0 版本下能够正常运行。

## 前置条件

- JDK 21
- Maven 3.8+
- PostgreSQL 数据库（用于 System 服务验证）
- Nacos 服务（用于配置中心和服务发现验证）

## 验证步骤

### 步骤 1: 环境准备

1. **确认 Java 版本**:
   ```bash
   java -version
   # 应显示 JDK 21
   ```

2. **确认 Maven 版本**:
   ```bash
   mvn -version
   # 应显示 Maven 3.8 或更高版本
   ```

3. **启动 PostgreSQL 数据库**:
   ```bash
   # 确保 PostgreSQL 服务已启动
   # 创建数据库（如需要）
   createdb atlas_system
   ```

4. **启动 Nacos 服务**:
   ```bash
   # 确保 Nacos 服务已启动
   # 默认地址: http://localhost:8848
   ```

### 步骤 2: 项目构建验证

1. **清理并构建项目**:
   ```bash
   cd /Users/liuhuan/workspace/coding/java/backend/atlas
   mvn clean install
   ```

2. **验证构建结果**:
   - 检查构建日志，确认无错误
   - 确认所有模块编译成功
   - 确认构建产物（JAR 文件）正常生成

3. **如果构建失败**:
   - 检查构建日志中的错误信息
   - 检查依赖版本是否兼容
   - 检查代码中是否有使用已废弃的 API

### 步骤 3: 模块编译验证

1. **验证公共模块编译**:
   ```bash
   # 验证 atlas-common-feature-core
   mvn clean compile -pl atlas-common/atlas-common-feature/atlas-common-feature-core
   
   # 验证 atlas-common-feature-security
   mvn clean compile -pl atlas-common/atlas-common-feature/atlas-common-feature-security
   
   # 验证 atlas-common-infra-web
   mvn clean compile -pl atlas-common/atlas-common-infra/atlas-common-infra-web
   
   # 验证 atlas-common-infra-redis
   mvn clean compile -pl atlas-common/atlas-common-infra/atlas-common-infra-redis
   
   # 验证 atlas-common-infra-db
   mvn clean compile -pl atlas-common/atlas-common-infra/atlas-common-infra-db
   
   # 验证 atlas-common-infra-logging
   mvn clean compile -pl atlas-common/atlas-common-infra/atlas-common-infra-logging
   ```

2. **验证服务模块编译**:
   ```bash
   # 验证 atlas-gateway
   mvn clean compile -pl atlas-gateway
   
   # 验证 atlas-auth
   mvn clean compile -pl atlas-auth
   
   # 验证 atlas-system
   mvn clean compile -pl atlas-service/atlas-system
   ```

3. **验证 API 模块编译**:
   ```bash
   # 验证 atlas-system-api
   mvn clean compile -pl atlas-service-api/atlas-system-api
   ```

### 步骤 4: 服务启动验证

1. **启动 Gateway 服务**:
   ```bash
   cd atlas-gateway
   mvn spring-boot:run
   # 或
   java -jar target/atlas-gateway-1.0.0.jar
   ```

2. **验证 Gateway 启动**:
   - 检查启动日志，确认无异常
   - 访问健康检查接口: `curl http://localhost:8080/actuator/health`
   - 检查 Nacos 控制台，确认服务已注册

3. **启动 Auth 服务**:
   ```bash
   cd atlas-auth
   mvn spring-boot:run
   # 或
   java -jar target/atlas-auth-1.0.0.jar
   ```

4. **验证 Auth 启动**:
   - 检查启动日志，确认无异常
   - 访问健康检查接口: `curl http://localhost:8081/actuator/health`
   - 检查 Nacos 控制台，确认服务已注册

5. **启动 System 服务**:
   ```bash
   cd atlas-service/atlas-system
   mvn spring-boot:run
   # 或
   java -jar target/atlas-system-1.0.0.jar
   ```

6. **验证 System 启动**:
   - 检查启动日志，确认无异常
   - 访问健康检查接口: `curl http://localhost:8082/actuator/health`
   - 检查 Nacos 控制台，确认服务已注册
   - 确认数据库迁移脚本已执行

### 步骤 5: 功能验证

1. **验证 Gateway 路由转发**:
   ```bash
   # 通过 Gateway 访问后端服务
   curl http://localhost:8080/api/v1/auth/public-key
   ```

2. **验证 Auth 服务登录功能**:
   ```bash
   curl -X POST http://localhost:8081/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "username": "admin",
       "password": "password123"
     }'
   ```

3. **验证 System 服务用户查询**:
   ```bash
   # 查询用户信息
   curl http://localhost:8082/api/v1/users/1
   
   # 查询用户权限
   curl http://localhost:8082/api/v1/users/1/authorities
   ```

4. **验证服务间 Feign 调用**:
   - 通过 Auth 服务调用 System 服务查询用户信息
   - 验证 Feign 接口调用是否正常

### 步骤 6: 依赖兼容性验证

1. **检查依赖树**:
   ```bash
   mvn dependency:tree > dependency-tree.txt
   ```

2. **分析依赖冲突**:
   ```bash
   mvn dependency:analyze
   ```

3. **检查运行时错误**:
   - 启动服务，检查日志中是否有 ClassNotFoundException
   - 检查日志中是否有 NoSuchMethodError
   - 检查日志中是否有其他兼容性错误

## 验证检查清单

### 构建验证检查清单

- [ ] 执行 `mvn clean install` 成功
- [ ] 所有模块编译成功
- [ ] 所有测试通过（如果存在）
- [ ] 构建产物正常生成
- [ ] 构建日志中无错误

### 编译验证检查清单

- [ ] 所有公共模块编译成功
- [ ] 所有服务模块编译成功
- [ ] 所有 API 模块编译成功
- [ ] 无依赖解析失败
- [ ] 无版本冲突

### 启动验证检查清单

- [ ] Gateway 服务启动成功
- [ ] Auth 服务启动成功
- [ ] System 服务启动成功
- [ ] 服务注册到 Nacos
- [ ] 健康检查通过

### 功能验证检查清单

- [ ] Gateway 路由转发正常
- [ ] Auth 登录功能正常
- [ ] System 用户查询正常
- [ ] 服务间调用正常
- [ ] 数据库操作正常

### 兼容性验证检查清单

- [ ] 所有依赖版本兼容
- [ ] 无版本冲突
- [ ] 无运行时兼容性错误

## 常见问题

### 问题 1: 构建失败，提示版本不兼容

**解决方案**:
1. 检查父 POM 中的版本配置是否正确
2. 检查子模块是否显式指定了版本号
3. 检查依赖版本是否与 Spring Boot 3.5.9 兼容

### 问题 2: 服务启动失败，提示配置错误

**解决方案**:
1. 检查配置文件格式是否正确
2. 检查配置属性名称是否有变更
3. 检查 Nacos 配置是否正确

### 问题 3: 数据库迁移失败

**解决方案**:
1. 检查 Flyway 版本是否兼容
2. 检查数据库连接配置是否正确
3. 检查迁移脚本格式是否正确

### 问题 4: 服务无法注册到 Nacos

**解决方案**:
1. 检查 Nacos 服务是否启动
2. 检查 Nacos 配置是否正确
3. 检查 Spring Cloud Alibaba 版本是否兼容

## 验证报告

验证完成后，应生成验证报告，包含：

1. **构建验证结果**: 构建是否成功，各模块编译结果
2. **服务启动结果**: 各服务启动是否成功，启动时间
3. **功能测试结果**: 各功能测试是否通过
4. **兼容性验证结果**: 依赖兼容性检查结果
5. **问题列表**: 发现的问题和建议

## 相关文档

- [规范文档](../spec.md)
- [技术规划文档](../plan.md)
- [技术调研文档](../research.md)
- [数据模型文档](../data-model.md)
