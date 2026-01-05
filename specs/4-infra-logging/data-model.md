# 数据模型

## 概述

本文档定义了 `atlas-common-infra-logging` 模块涉及的所有数据实体、配置参数和工具类。

## 核心实体

### TraceIdUtil（工具类）

**描述**: TraceId 管理工具类，提供 TraceId 的设置、获取和清理方法。

**包名**: `com.atlas.common.infra.logging.trace`

**方法定义**:

| 方法名 | 返回类型 | 说明 | 必填 |
|--------|----------|------|------|
| setTraceId | void | 设置当前线程的 TraceId | 是 |
| getTraceId | String | 获取当前线程的 TraceId | 是 |
| clear | void | 清除当前线程的 TraceId | 是 |
| generate | String | 生成新的 TraceId（UUID） | 是 |

**约束规则**:
- setTraceId() 会同时设置 ThreadLocal 和 MDC
- getTraceId() 优先从 ThreadLocal 获取，如果为空则从 MDC 获取
- clear() 会同时清理 ThreadLocal 和 MDC
- generate() 生成 32 位 UUID（去除连字符）

### TraceIdGenerator（工具类）

**描述**: TraceId 生成器，支持 UUID 和雪花算法生成 TraceId。

**包名**: `com.atlas.common.infra.logging.trace`

**方法定义**:

| 方法名 | 返回类型 | 说明 | 必填 |
|--------|----------|------|------|
| generateUUID | String | 使用 UUID 生成 TraceId | 是 |
| generateSnowflake | String | 使用雪花算法生成 TraceId | 是 |

**约束规则**:
- generateUUID() 返回 32 位字符串（去除连字符）
- generateSnowflake() 需要配置数据中心ID和工作机器ID
- TraceId 长度建议 16-32 个字符

### DesensitizeUtil（工具类）

**描述**: 敏感信息脱敏工具类，提供常见敏感字段的脱敏方法。

**包名**: `com.atlas.common.infra.logging.desensitize`

**方法定义**:

| 方法名 | 返回类型 | 说明 | 必填 |
|--------|----------|------|------|
| maskPhone | String | 脱敏手机号（138****5678） | 是 |
| maskIdCard | String | 脱敏身份证号（前6后4） | 是 |
| maskBankCard | String | 脱敏银行卡号（后4位） | 是 |
| maskEmail | String | 脱敏邮箱（ab****@example.com） | 是 |
| maskPassword | String | 脱敏密码（******） | 是 |
| mask | String | 通用脱敏方法（自定义规则） | 是 |

**约束规则**:
- 手机号：长度必须 ≥ 7，保留前 3 位和后 4 位
- 身份证号：长度必须 ≥ 10，保留前 6 位和后 4 位
- 银行卡号：长度必须 ≥ 4，保留后 4 位
- 邮箱：必须包含 @，保留用户名前 2 位和域名
- 密码：全部替换为 `******`
- 输入为 null 或空字符串时，返回原值

### DesensitizeRule（配置类）

**描述**: 脱敏规则配置类，定义脱敏规则的匹配模式和替换规则。

**包名**: `com.atlas.common.infra.logging.desensitize`

**字段定义**:

| 字段名 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| fieldType | String | 字段类型（phone、idCard、bankCard、email、password） | 是 | - |
| pattern | Pattern | 匹配模式（编译后的正则表达式） | 是 | - |
| prefixLength | Integer | 保留前缀长度 | 否 | 0 |
| suffixLength | Integer | 保留后缀长度 | 否 | 0 |
| replacement | String | 替换字符串 | 否 | "****" |

**约束规则**:
- pattern 必须是可以编译的正则表达式
- prefixLength + suffixLength 必须小于字段总长度
- replacement 不能为 null

### @Sensitive（注解）

**描述**: 敏感字段注解，用于标记对象字段需要自动脱敏。

**包名**: `com.atlas.common.infra.logging.desensitize.annotation`

**属性定义**:

| 属性名 | 类型 | 说明 | 必填 | 默认值 |
|--------|------|------|------|--------|
| type | SensitiveType | 敏感字段类型 | 是 | - |
| prefixLength | int | 保留前缀长度 | 否 | 根据类型自动计算 |
| suffixLength | int | 保留后缀长度 | 否 | 根据类型自动计算 |

**使用示例**:
```java
public class User {
    @Sensitive(type = SensitiveType.PHONE)
    private String phone;
    
    @Sensitive(type = SensitiveType.ID_CARD)
    private String idCard;
}
```

### SensitiveType（枚举）

**描述**: 敏感字段类型枚举。

**包名**: `com.atlas.common.infra.logging.desensitize.annotation`

**枚举值**:

| 枚举值 | 说明 |
|--------|------|
| PHONE | 手机号 |
| ID_CARD | 身份证号 |
| BANK_CARD | 银行卡号 |
| EMAIL | 邮箱 |
| PASSWORD | 密码 |
| CUSTOM | 自定义类型 |

## 配置参数

### 日志配置参数

**配置文件**: `logback-spring.xml`

| 参数名 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| LOG_PATTERN | String | 日志格式模板 | `%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} [TraceId: %X{traceId}] - %msg%n` |
| LOG_LEVEL | String | 日志级别 | INFO |
| LOG_PATH | String | 日志文件路径 | logs/ |
| MAX_FILE_SIZE | String | 单个日志文件最大大小 | 100MB |
| MAX_HISTORY | Integer | 日志文件保留天数 | 30 |
| ERROR_MAX_HISTORY | Integer | 错误日志保留天数 | 90 |

### TraceId 配置参数

**配置方式**: 通过代码配置或配置文件

| 参数名 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| TRACE_ID_HEADER | String | TraceId 请求头名称 | X-Trace-Id |
| TRACE_ID_MDC_KEY | String | MDC 键名 | traceId |
| TRACE_ID_GENERATOR | String | TraceId 生成器类型（UUID/SNOWFLAKE） | UUID |

### 脱敏配置参数

**配置方式**: 通过代码配置或配置文件

| 参数名 | 类型 | 说明 | 默认值 |
|--------|------|------|--------|
| ENABLE_AUTO_DESENSITIZE | Boolean | 是否启用自动脱敏 | true |
| DESENSITIZE_PATTERNS | List<DesensitizeRule> | 自定义脱敏规则列表 | 空列表 |

## 实体关系

### TraceId 管理关系

```
HTTP Request
  ├── TraceIdInterceptor (拦截器)
  │   ├── 获取或生成 TraceId
  │   ├── TraceIdUtil.setTraceId()
  │   │   ├── ThreadLocal 存储
  │   │   └── MDC.put("traceId", traceId)
  │   └── 请求结束后清理
  │
  └── Feign Call
      └── TraceIdFeignInterceptor (拦截器)
          └── TraceIdUtil.getTraceId()
              └── 添加到请求头 X-Trace-Id
```

### 脱敏处理关系

```
日志消息
  ├── DesensitizeInterceptor (拦截器)
  │   ├── 检测敏感信息模式
  │   ├── DesensitizeUtil.mask() (应用脱敏规则)
  │   └── 输出脱敏后的日志
  │
  └── 对象字段
      ├── @Sensitive 注解标记
      └── 反射获取字段值并脱敏
```

## 验证规则

### TraceId 验证

- TraceId 不能为 null 或空字符串
- TraceId 长度应在 16-32 个字符之间
- TraceId 在请求结束后必须清理

### 脱敏规则验证

- 脱敏规则的正则表达式必须可以编译
- prefixLength + suffixLength 必须小于字段总长度
- 脱敏后的字符串长度应合理（不能过长）

### 日志配置验证

- 日志格式必须包含 TraceId（%X{traceId}）
- 日志文件路径必须有效
- 日志文件大小限制必须合理（建议 10MB-500MB）

## 性能考虑

1. **TraceId 生成**: UUID 生成性能好，雪花算法需要配置但性能也可接受
2. **ThreadLocal 访问**: 线程本地存储，性能开销小
3. **MDC 操作**: Logback 原生支持，性能好
4. **正则表达式**: 编译后缓存 Pattern 对象，避免重复编译
5. **脱敏拦截器**: 考虑性能影响，可以配置是否启用自动脱敏

## 扩展性设计

### TraceId 生成器扩展

可以通过实现 TraceIdGenerator 接口添加自定义生成策略：

```java
public interface TraceIdGenerator {
    String generate();
}
```

### 脱敏规则扩展

可以通过配置自定义脱敏规则：

```java
DesensitizeRule customRule = DesensitizeRule.builder()
    .fieldType("custom")
    .pattern(Pattern.compile("\\d{4}-\\d{4}-\\d{4}"))
    .prefixLength(4)
    .suffixLength(4)
    .build();
```

