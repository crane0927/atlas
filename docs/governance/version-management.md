# 版本管理说明

## 概述

本文档说明 Atlas 项目的版本管理策略，包括依赖版本管理、版本升级流程和版本兼容性要求。

## 版本管理原则

### 1. 统一管理

所有依赖版本在父 POM (`atlas/pom.xml`) 中统一管理，子模块通过继承父 POM 自动获得版本管理。

**优势**:
- 版本升级只需在父 POM 中修改一处
- 避免子模块版本不一致导致的冲突
- 便于版本审计和依赖管理

### 2. 使用 BOM 管理

优先使用 BOM (Bill of Materials) 管理依赖版本，包括：

- **Spring Boot Parent**: `spring-boot-starter-parent:4.0.1`
- **Spring Cloud BOM**: `spring-cloud-dependencies:2025.1.0`
- **Spring Cloud Alibaba BOM**: `spring-cloud-alibaba-dependencies:2025.1.0`

**优势**:
- 自动管理 Spring 生态依赖版本
- 确保版本兼容性
- 减少版本冲突

### 3. 语义化版本

遵循语义化版本规范（Semantic Versioning）：
- **主版本号（MAJOR）**: 不兼容的 API 修改
- **次版本号（MINOR）**: 向后兼容的功能性新增
- **修订号（PATCH）**: 向后兼容的问题修正

## 版本管理结构

### 父 POM 版本管理

在父 POM 的 `<dependencyManagement>` 中定义所有依赖版本：

```xml
<dependencyManagement>
    <dependencies>
        <!-- Spring Cloud BOM -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        
        <!-- Spring Cloud Alibaba BOM -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>${spring-cloud-alibaba.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        
        <!-- 其他依赖版本管理 -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 属性定义

在 `<properties>` 中定义版本属性：

```xml
<properties>
    <!-- Spring Cloud 版本 -->
    <spring-cloud.version>2025.1.0</spring-cloud.version>
    <spring-cloud-alibaba.version>2025.1.0</spring-cloud-alibaba.version>
    
    <!-- 常用依赖版本 -->
    <mybatis-plus.version>3.5.8</mybatis-plus.version>
    <postgresql.version>42.7.4</postgresql.version>
    <lombok.version>1.18.34</lombok.version>
</properties>
```

### 子模块使用

子模块在 `<dependencies>` 中引用依赖，无需指定版本：

```xml
<dependencies>
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <!-- 版本由父 POM 管理，无需指定 -->
    </dependency>
</dependencies>
```

## 版本升级流程

### 1. 升级前准备

1. **查阅官方文档**: 了解新版本的变化和兼容性要求
2. **检查兼容性矩阵**: 确认 Spring Boot、Spring Cloud、Spring Cloud Alibaba 版本兼容
3. **评估影响范围**: 评估升级对现有代码的影响
4. **制定升级计划**: 制定详细的升级步骤和回滚方案

### 2. 升级步骤

1. **更新父 POM**: 在父 POM 中更新版本号
2. **本地测试**: 在本地环境测试构建和运行
3. **集成测试**: 运行完整的集成测试套件
4. **代码审查**: 提交代码审查，确保升级正确
5. **部署测试环境**: 在测试环境验证升级效果
6. **生产部署**: 确认无误后部署到生产环境

### 3. 升级示例

**升级 MyBatis-Plus 版本**:

```xml
<!-- 在父 POM 的 <properties> 中修改 -->
<mybatis-plus.version>3.5.9</mybatis-plus.version>
```

**升级 Spring Cloud 版本**:

```xml
<!-- 在父 POM 的 <properties> 中修改 -->
<spring-cloud.version>2025.2.0</spring-cloud.version>

<!-- 在 <dependencyManagement> 中更新 BOM -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-dependencies</artifactId>
    <version>${spring-cloud.version}</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

## 版本兼容性要求

### Spring Boot 与 Spring Cloud 兼容性

| Spring Boot 版本 | Spring Cloud 版本 | Spring Cloud Alibaba 版本 |
|-----------------|------------------|-------------------------|
| 4.0.1           | 2025.1.0         | 2025.1.0                |

**注意**: 
- 版本兼容性矩阵可能随版本更新而变化
- 升级前请查阅官方兼容性文档
- Spring Cloud Alibaba 2025.1.0 可能尚未发布，请检查实际可用版本

### Java 版本要求

- **JDK 版本**: 21 (LTS)
- **编译版本**: 21
- **源代码版本**: 21

**注意**: 
- 使用 Maven Enforcer Plugin 强制 Java 版本为 21
- 所有模块必须使用 Java 21

## 版本检查工具

### Maven Enforcer Plugin

使用 Maven Enforcer Plugin 检查版本一致性：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <executions>
        <execution>
            <id>enforce</id>
            <goals>
                <goal>enforce</goal>
            </goals>
            <configuration>
                <rules>
                    <!-- 强制 Java 版本为 21 -->
                    <requireJavaVersion>
                        <version>[21,22)</version>
                    </requireJavaVersion>
                    <!-- 禁止依赖版本冲突 -->
                    <banDuplicatePomDependencyVersions/>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 检查命令

```bash
# 检查依赖版本冲突
mvn dependency:tree

# 检查依赖更新
mvn versions:display-dependency-updates

# 检查插件更新
mvn versions:display-plugin-updates
```

## 版本管理最佳实践

### 1. 定期更新

- **安全更新**: 及时更新安全补丁版本
- **功能更新**: 根据项目需求评估功能更新
- **大版本升级**: 充分测试后再升级大版本

### 2. 版本锁定

- **生产环境**: 锁定依赖版本，避免意外升级
- **开发环境**: 可以尝试新版本，但需充分测试

### 3. 版本记录

- **变更日志**: 记录所有版本升级变更
- **兼容性说明**: 记录版本兼容性要求
- **升级指南**: 提供详细的升级步骤

### 4. 版本回滚

- **备份配置**: 升级前备份当前版本配置
- **回滚方案**: 制定详细的回滚步骤
- **测试验证**: 回滚后验证系统正常运行

## 常见问题

### Q1: 如何添加新的依赖？

**A**: 
1. 在父 POM 的 `<dependencyManagement>` 中添加依赖版本
2. 子模块在 `<dependencies>` 中引用，无需指定版本

### Q2: 如何覆盖父 POM 的版本？

**A**: 
- **不推荐**: 子模块应遵循父 POM 的版本管理
- **特殊情况**: 如需覆盖，在子模块中显式指定版本，并记录原因

### Q3: 如何检查依赖版本冲突？

**A**: 
- 运行 `mvn dependency:tree` 查看依赖树
- 使用 Maven Enforcer Plugin 检查版本冲突
- 使用 IDE 的依赖分析工具

### Q4: 如何升级 Spring Boot 版本？

**A**: 
1. 查阅 Spring Boot 官方升级指南
2. 检查 Spring Cloud 和 Spring Cloud Alibaba 兼容性
3. 在父 POM 中更新版本号
4. 运行完整测试套件
5. 修复不兼容的代码

### Q5: 如何处理版本冲突？

**A**: 
1. 使用 `mvn dependency:tree` 定位冲突
2. 在父 POM 中统一版本管理
3. 使用 `<exclusions>` 排除冲突依赖
4. 使用 Maven Enforcer Plugin 强制版本一致性

## 版本管理检查清单

在升级版本前，检查以下事项：

- [ ] 查阅官方升级指南和兼容性文档
- [ ] 检查 Spring Boot、Spring Cloud、Spring Cloud Alibaba 版本兼容性
- [ ] 评估升级对现有代码的影响
- [ ] 制定详细的升级步骤和回滚方案
- [ ] 在本地环境测试构建和运行
- [ ] 运行完整的集成测试套件
- [ ] 在测试环境验证升级效果
- [ ] 更新版本管理文档
- [ ] 记录版本升级变更日志

## 相关文档

- [父 POM 配置](../../pom.xml)
- [工程规范文档](../engineering-standards/)
- [项目宪法](../../.specify/memory/constitution.md)

