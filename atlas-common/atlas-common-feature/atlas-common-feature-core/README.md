# atlas-common-feature-core

## 模块简介

`atlas-common-feature-core` 是 Atlas 项目的核心功能特性模块，提供统一的响应包装类、异常体系、分页对象、错误码常量等核心功能。该模块是项目的基础设施，为所有业务模块提供统一的响应格式、错误处理和分页支持。

## 主要功能

### 1. 统一响应包装类 Result<T>

提供统一的 API 响应包装类，封装所有 HTTP 接口的响应数据：
- 支持泛型，可以包装任意类型的数据
- 包含状态码（code）、消息（message）、数据（data）、时间戳（timestamp）、追踪ID（traceId）等字段
- 提供成功和失败的静态工厂方法
- 自动从 MDC 中获取 traceId，支持分布式追踪

### 2. 异常体系

提供完整的业务异常体系，包括：
- `BusinessException`: 基础业务异常类
- `ParameterException`: 参数异常类
- `PermissionException`: 权限异常类
- `DataException`: 数据异常类

### 3. 分页对象 PageResult<T>

提供统一的分页响应对象：
- 包含列表数据（list）、总数（total）、页码（page）、每页大小（size）、总页数（pages）、追踪ID（traceId）等字段
- 提供便捷的分页判断方法（hasNext、hasPrevious、isFirst、isLast）
- 自动从 MDC 中获取 traceId

### 4. 错误码常量

提供项目通用的错误码常量：
- `CommonErrorCode`: 通用错误码常量类，定义系统错误、参数错误、业务错误、权限错误、数据错误等
- 错误码格式：6位数字（MMTTSS），模块码为 05

### 5. 基础常量

提供项目通用的常量定义：
- `HttpStatus`: HTTP 状态码常量
- `CommonConstants`: 通用常量（如默认分页大小等）

## 快速开始

### 添加依赖

在子模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.atlas</groupId>
    <artifactId>atlas-common-feature-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

#### 1. 使用 Result 包装 API 响应

```java
import com.atlas.common.feature.core.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    public Result<UserVO> getUser(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        return Result.success(user);
    }
}
```

#### 2. 使用异常体系

```java
import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.constant.CommonErrorCode;

@Service
public class UserServiceImpl {
    
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(CommonErrorCode.DATA_NOT_FOUND, "用户不存在");
        }
        return user;
    }
}
```

#### 3. 使用分页对象

```java
import com.atlas.common.feature.core.page.PageResult;

@RestController
public class UserController {
    
    @GetMapping("/users")
    public Result<PageResult<UserVO>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<UserVO> userList = userService.getUsers(page, size);
        Long total = userService.countUsers();
        PageResult<UserVO> pageResult = PageResult.of(userList, total, page, size);
        return Result.success(pageResult);
    }
}
```

## 相关文档

- [快速开始指南](../../../specs/2-common-feature-core/quickstart.md) - 详细的使用指南和示例代码
- [功能规格说明](../../../specs/2-common-feature-core/spec.md) - 完整的功能需求说明
- [实施计划](../../../specs/2-common-feature-core/plan.md) - 技术实现方案
- [数据模型](../../../specs/2-common-feature-core/data-model.md) - 数据模型定义
- [API 合约](../../../specs/2-common-feature-core/contracts/README.md) - API 接口合约说明

## 技术栈

- **Java**: JDK 21
- **Spring Boot**: 4.0.1
- **Lombok**: 简化代码
- **Jackson**: JSON 序列化支持

## 版本信息

- **当前版本**: 1.0.0
- **最后更新**: 2026-01-05

