# API 合约定义

## 概述

`atlas-common-infra-db` 模块是基础设施模块，不涉及外部 API 接口。本模块主要提供：

1. **MyBatis-Plus 基础配置**: 内部配置类，不涉及 API 接口
2. **分页插件**: 内部插件配置，不涉及 API 接口
3. **审计字段填充**: 内部处理器，不涉及 API 接口

## 内部接口规范

### MyBatisPlusConfig

**类型**: Spring Configuration Bean
**说明**: 提供 MyBatis-Plus 配置，供其他模块使用

### MyBatisPlusProperties

**类型**: Spring Configuration Properties Bean
**说明**: 提供配置属性绑定，供配置类使用

### AuditMetaObjectHandler

**类型**: Spring Component Bean（实现 MetaObjectHandler 接口）
**说明**: 提供审计字段自动填充功能，供 MyBatis-Plus 使用

### BaseEntity

**类型**: 基础实体类（可选）
**说明**: 提供审计字段定义，供业务实体类继承使用

## 使用说明

本模块不定义外部 API 接口，所有功能通过配置类和处理器提供给业务模块使用。

