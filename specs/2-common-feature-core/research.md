# 技术调研文档

## 调研目标

为"实现 atlas-common-feature-core 模块"功能提供技术决策依据，解决规划中的技术选型和设计问题。

## 决策记录

### 决策 1: Result 响应包装类设计

**问题**: 如何设计统一的 API 响应包装类？

**决策**: 采用泛型 Result<T> 类，包含 code、message、data、timestamp 字段

**理由**:
1. **泛型支持**: 使用泛型可以包装任意类型的数据，提供类型安全
2. **统一格式**: 所有 API 响应使用相同格式，便于前端统一处理
3. **错误处理**: 成功和失败都使用相同结构，简化错误处理逻辑
4. **时间戳**: 自动添加时间戳，便于问题追踪和日志分析

**字段设计**:
- `code`: String 类型，成功为 "000000"，失败为具体错误码
- `message`: String 类型，成功为 "操作成功"，失败为错误消息
- `data`: 泛型 T，响应数据（成功时包含，失败时为 null）
- `timestamp`: Long 类型，时间戳（毫秒）

**静态方法设计**:
- `Result.success(T data)`: 创建成功响应
- `Result.error(String code, String message)`: 创建失败响应
- `Result.error(String code, String message, T data)`: 创建失败响应（带数据）

**序列化配置**:
- 使用 `@JsonInclude(JsonInclude.Include.NON_NULL)` 忽略 null 值
- 使用 `@JsonProperty` 控制字段命名

**替代方案考虑**:
- **不使用包装类**: 不符合项目规范，前端处理复杂
- **使用枚举作为状态码**: 不够灵活，难以扩展

### 决策 2: 异常体系设计

**问题**: 如何设计统一的业务异常类体系？

**决策**: 采用继承层次结构，BusinessException 作为基类

**异常层次结构**:
```
BusinessException (基类)
├── ParameterException (参数异常)
├── PermissionException (权限异常)
└── DataException (数据异常)
```

**基类设计**:
- `errorCode`: String 类型，错误码（6位数字）
- `message`: String 类型，错误消息
- `cause`: Throwable 类型，异常原因（可选）

**理由**:
1. **类型区分**: 不同类型的异常便于分类处理
2. **错误码支持**: 所有异常都包含错误码，便于统一处理
3. **异常链**: 支持异常链，便于问题追踪
4. **序列化支持**: 异常信息可以序列化为 JSON

**替代方案考虑**:
- **单一异常类**: 不够灵活，难以区分异常类型
- **使用枚举**: 不符合 Java 异常处理规范

### 决策 3: 分页对象设计

**问题**: 如何设计统一的分页响应对象？

**决策**: 采用泛型 PageResult<T> 类，包含 list、total、page、size、pages 字段

**字段设计**:
- `list`: List<T> 类型，数据列表
- `total`: Long 类型，总记录数
- `page`: Integer 类型，当前页码（从 1 开始）
- `size`: Integer 类型，每页大小
- `pages`: Integer 类型，总页数（自动计算）

**计算逻辑**:
- `pages = (total + size - 1) / size`: 向上取整计算总页数
- `hasPrevious = page > 1`: 是否有上一页
- `hasNext = page < pages`: 是否有下一页

**构造方法**:
- `PageResult.of(List<T> list, Long total, Integer page, Integer size)`: 完整构造
- `PageResult.of(List<T> list, Long total)`: 使用默认 page=1, size=list.size()

**理由**:
1. **统一格式**: 所有分页查询使用相同格式
2. **自动计算**: 总页数自动计算，减少错误
3. **类型安全**: 使用泛型提供类型安全
4. **易于使用**: 提供便捷的静态工厂方法

**替代方案考虑**:
- **使用 MyBatis-Plus 的 Page**: 耦合度高，不符合模块职责
- **使用 Spring Data 的 Page**: 依赖 Spring Data，不符合项目技术栈

### 决策 4: 错误码分配方案

**问题**: 如何分配通用错误码？

**决策**: 使用模块码 05（atlas-common-feature），按错误类型分配类型码

**错误码分配**:
- **系统错误 (00-09)**: 050000-050999
- **参数错误 (10-19)**: 051000-051999
- **业务错误 (20-29)**: 052000-052999
- **权限错误 (30-39)**: 053000-053999
- **数据错误 (40-49)**: 054000-054999

**通用错误码定义**:
- `050000`: 系统内部错误
- `051000`: 参数错误
- `052000`: 业务处理失败
- `053000`: 权限不足
- `054000`: 数据错误

**理由**:
1. **符合规范**: 遵循项目错误码规范（6位数字：MMTTSS）
2. **预留空间**: 每个类型预留 1000 个错误码空间
3. **易于扩展**: 业务模块可以在此基础上扩展

**替代方案考虑**:
- **使用字符串错误码**: 不符合项目规范
- **不使用错误码**: 不符合项目规范

### 决策 5: 常量类设计

**问题**: 如何设计基础常量类？

**决策**: 创建多个常量类，按功能分类

**常量类设计**:
1. **HttpStatus**: HTTP 状态码常量
   - `OK = 200`
   - `BAD_REQUEST = 400`
   - `UNAUTHORIZED = 401`
   - `FORBIDDEN = 403`
   - `NOT_FOUND = 404`
   - `INTERNAL_SERVER_ERROR = 500`

2. **CommonConstants**: 通用常量
   - `EMPTY_STRING = ""`
   - `DEFAULT_PAGE = 1`
   - `DEFAULT_SIZE = 10`
   - `MAX_PAGE_SIZE = 1000`

**设计原则**:
- 使用私有构造函数防止实例化
- 所有常量都有中文注释
- 常量命名使用大写字母和下划线

**理由**:
1. **分类清晰**: 按功能分类，便于查找和使用
2. **防止实例化**: 使用私有构造函数，符合常量类设计规范
3. **易于维护**: 集中管理常量，便于修改和扩展

**替代方案考虑**:
- **使用枚举**: 不适合字符串和数字常量
- **使用配置文件**: 增加复杂度，不符合常量使用场景

## 最佳实践参考

### Result 响应包装类最佳实践

1. **使用泛型**: 提供类型安全，避免类型转换
2. **静态工厂方法**: 提供便捷的创建方式，隐藏实现细节
3. **不可变性**: 使用 final 字段和不可变对象，确保线程安全
4. **序列化优化**: 使用 `@JsonInclude` 忽略 null 值，减少 JSON 大小

### 异常处理最佳实践

1. **异常层次**: 使用继承层次结构，便于分类处理
2. **错误码**: 所有异常包含错误码，便于统一处理
3. **异常链**: 支持异常链，便于问题追踪
4. **序列化**: 异常信息可以序列化，便于日志记录

### 分页对象最佳实践

1. **自动计算**: 总页数自动计算，减少错误
2. **边界处理**: 处理空列表、总数为 0 等边界情况
3. **类型安全**: 使用泛型提供类型安全
4. **便捷方法**: 提供静态工厂方法，简化使用

## 参考资料

1. [Spring Boot RESTful API 最佳实践](https://spring.io/guides/tutorials/rest/)
2. [Jackson JSON 序列化文档](https://github.com/FasterXML/jackson-docs)
3. [Java 异常处理最佳实践](https://docs.oracle.com/javase/tutorial/essential/exceptions/)
4. [项目错误码规范](../../docs/engineering-standards/error-code.md)
5. [项目包名规范](../../docs/engineering-standards/package-naming.md)

## 待确认事项

无（所有技术决策已明确）

