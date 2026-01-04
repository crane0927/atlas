# 技术调研文档

## 调研目标

为"完成根工程与版本治理"功能提供技术决策依据，解决规划中的技术选型和设计问题。

## 决策记录

### 决策 1: 代码格式化工具选择

**问题**: 选择 Spotless 还是 Checkstyle 进行代码格式化？

**决策**: 选择 **Spotless Maven Plugin**

**理由**:
1. **自动格式化**: Spotless 可以自动格式化代码，减少手动操作
2. **配置简单**: 相比 Checkstyle，Spotless 配置更简洁，符合"约定优于配置"原则
3. **多工具支持**: Spotless 支持 Google Java Format、Eclipse Formatter 等多种格式化工具
4. **集成友好**: 与 Maven 生命周期集成良好，可在构建时自动执行
5. **团队效率**: 自动格式化减少代码审查时的格式争议

**替代方案考虑**:
- **Checkstyle**: 更灵活，可自定义规则，但配置复杂，需要更多维护成本
- **Google Java Format**: 仅格式化，不检查代码质量，功能单一

**实施建议**:
- 使用 Google Java Format 作为格式化引擎（业界标准）
- 配置在 `validate` 阶段执行，确保代码提交前格式化
- 提供 IDE 插件配置指南，支持开发时格式化

### 决策 2: 错误码段位分配方案

**问题**: 如何分配各模块的错误码段位？需要预留多少空间？

**决策**: 采用 **6 位数字错误码格式**，每个模块分配 1000 个错误码空间

**错误码格式**: `MMTTSS`
- **MM (2位)**: 模块码（01-99，可支持 99 个模块）
- **TT (2位)**: 错误类型码（00-99）
  - 00-09: 系统错误
  - 10-19: 参数错误
  - 20-29: 业务错误
  - 30-39: 权限错误
  - 40-49: 数据错误
  - 50-99: 预留扩展
- **SS (2位)**: 序号（00-99，每个类型最多 100 个错误）

**模块错误码分配**:
- `01xxxx`: atlas-gateway
- `02xxxx`: atlas-auth
- `03xxxx`: atlas-system
- `04xxxx`: atlas-common-infra
- `05xxxx`: atlas-common-feature
- `06-99xxxx`: 预留业务模块

**理由**:
1. **可扩展性**: 6 位数字提供足够的错误码空间（99 个模块 × 100 个错误/类型）
2. **可读性**: 错误码结构清晰，便于识别模块和错误类型
3. **可维护性**: 模块码和类型码分离，便于管理和查找
4. **向后兼容**: 预留足够的扩展空间，避免未来冲突

**替代方案考虑**:
- **4 位数字**: 空间不足，难以支持多模块
- **8 位数字**: 过于冗长，不便于记忆和使用
- **字母数字混合**: 增加复杂度，不利于国际化

**实施建议**:
- 建立错误码分配登记表，记录已使用的错误码
- 提供错误码常量类模板，统一错误码定义方式
- 在文档中明确各模块的错误码段位

### 决策 3: Nacos 配置命名策略

**问题**: Nacos DataId 和 Group 的命名策略？

**决策**: 采用 **分层命名策略**

**DataId 命名规则**: `{application-name}-{profile}.{extension}`
- **示例**: 
  - `atlas-system-dev.yaml`
  - `atlas-gateway-prod.yaml`
  - `atlas-auth-test.yaml`

**Group 命名规则**: 按环境分组
- **DEFAULT_GROUP**: 默认分组（开发环境）
- **DEV_GROUP**: 开发环境
- **TEST_GROUP**: 测试环境
- **PROD_GROUP**: 生产环境

**配置项 Key 命名规则**: 使用点分隔的层级结构
- **格式**: `{module}.{category}.{key}`
- **示例**:
  - `atlas.system.database.url`
  - `atlas.gateway.rate-limit.enabled`
  - `atlas.auth.jwt.secret`

**理由**:
1. **清晰性**: 命名规则清晰，便于识别配置所属模块和环境
2. **可管理性**: 按环境分组便于配置管理和权限控制
3. **可扩展性**: 层级结构支持配置分类和扩展
4. **一致性**: 统一的命名规范避免配置混乱

**替代方案考虑**:
- **单一 Group**: 所有配置放在 DEFAULT_GROUP，不利于环境隔离
- **按模块分组**: 配置分散，不利于统一管理
- **扁平化 Key**: 配置项 key 不使用层级结构，可读性差

**实施建议**:
- 在配置命名规范文档中提供完整的命名示例
- 建立配置命名检查机制（通过工具或代码审查）
- 提供配置迁移指南，帮助现有配置迁移到新规范

### 决策 4: Maven 插件配置策略

**问题**: 如何配置 Maven 插件以确保最佳实践？

**决策**: 采用 **分层插件配置策略**

**核心插件配置**:
1. **Maven Enforcer Plugin**: 
   - 强制 Java 版本为 21
   - 强制依赖版本一致性
   - 禁止使用已废弃的依赖

2. **Maven Surefire Plugin**:
   - 配置测试执行策略
   - 设置测试超时时间
   - 配置测试报告输出

3. **Maven Failsafe Plugin**:
   - 配置集成测试执行
   - 与 Surefire 区分测试类型

4. **Spotless Maven Plugin**:
   - 使用 Google Java Format
   - 在 `validate` 阶段执行
   - 配置格式化规则

5. **Maven Compiler Plugin**:
   - 设置 Java 版本为 21
   - 设置源代码和目标版本
   - 配置编译参数

**理由**:
1. **强制规范**: Enforcer 插件确保所有模块遵循统一规范
2. **测试分离**: Surefire 和 Failsafe 分离单元测试和集成测试
3. **代码质量**: Spotless 确保代码格式统一
4. **构建一致性**: 统一的插件配置确保构建行为一致

**实施建议**:
- 在父 POM 中配置插件管理（pluginManagement）
- 子模块继承插件配置，无需重复配置
- 提供插件配置文档，说明各插件的作用和配置项

## 最佳实践参考

### Maven 父 POM 最佳实践

1. **使用 BOM 管理依赖版本**: 通过 Spring Cloud BOM 统一管理依赖版本
2. **插件管理而非插件执行**: 在父 POM 中使用 `<pluginManagement>`，子模块按需启用
3. **属性集中定义**: 在 `<properties>` 中集中定义版本号和配置属性
4. **版本号统一管理**: 所有版本号在父 POM 中定义，子模块引用属性

### Spring Boot/Cloud 版本管理

1. **使用官方 BOM**: 通过 `spring-cloud-dependencies` 和 `spring-cloud-alibaba-dependencies` 管理版本
2. **版本兼容性检查**: 确保 Spring Boot、Spring Cloud、Spring Cloud Alibaba 版本兼容
3. **版本升级策略**: 小版本升级可直接更新，大版本升级需要充分测试

### 工程规范最佳实践

1. **文档先行**: 规范文档应在实施前完成，确保团队理解
2. **工具强制**: 使用工具（如 Enforcer、Checkstyle）强制规范执行
3. **示例丰富**: 提供丰富的示例帮助团队理解和应用规范
4. **定期审查**: 定期审查规范执行情况，及时调整和完善

## 参考资料

1. [Maven POM Reference](https://maven.apache.org/pom.html)
2. [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
3. [Spotless Maven Plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven)
4. [Google Java Format](https://github.com/google/google-java-format)
5. [Nacos Configuration Management](https://nacos.io/docs/latest/guide/user/configuration/)

## 待确认事项

无（所有技术决策已明确）

