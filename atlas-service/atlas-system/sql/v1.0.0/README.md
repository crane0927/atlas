# SQL 脚本版本 v1.0.0

## 版本信息

- **版本号**: v1.0.0
- **变更日期**: 2026-01-06
- **变更说明**: 初始版本，创建用户、角色、权限相关表结构

## 变更内容

本版本包含以下 SQL 变更：

1. **创建用户表** (`001_create_user_table.sql`)
   - 创建 `sys_user` 表
   - 包含用户基本信息字段（用户ID、用户名、密码、状态等）
   - 创建主键索引和唯一索引

2. **创建角色表** (`002_create_role_table.sql`)
   - 创建 `sys_role` 表
   - 包含角色基本信息字段（角色ID、角色代码、角色名称等）
   - 创建主键索引和唯一索引

3. **创建权限表** (`003_create_permission_table.sql`)
   - 创建 `sys_permission` 表
   - 包含权限基本信息字段（权限ID、权限代码、权限名称等）
   - 创建主键索引和唯一索引

4. **创建用户角色关联表** (`004_create_user_role_table.sql`)
   - 创建 `sys_user_role` 表
   - 建立用户与角色的多对多关联关系
   - 创建外键约束和唯一索引

5. **创建角色权限关联表** (`005_create_role_permission_table.sql`)
   - 创建 `sys_role_permission` 表
   - 建立角色与权限的多对多关联关系
   - 创建外键约束和唯一索引

## 执行顺序

SQL 文件必须按照以下顺序执行：

1. `001_create_user_table.sql` - 用户表（基础表）
2. `002_create_role_table.sql` - 角色表（基础表）
3. `003_create_permission_table.sql` - 权限表（基础表）
4. `004_create_user_role_table.sql` - 用户角色关联表（依赖用户表和角色表）
5. `005_create_role_permission_table.sql` - 角色权限关联表（依赖角色表和权限表）

**依赖关系**:
- 用户角色关联表依赖用户表和角色表
- 角色权限关联表依赖角色表和权限表
- 基础表（用户、角色、权限）可以并行创建，但建议按顺序执行

## 回滚说明

如需回滚本版本的变更，执行以下操作：

1. 删除关联表（先删除外键依赖）：
   ```sql
   DROP TABLE IF EXISTS sys_role_permission;
   DROP TABLE IF EXISTS sys_user_role;
   ```

2. 删除基础表：
   ```sql
   DROP TABLE IF EXISTS sys_permission;
   DROP TABLE IF EXISTS sys_role;
   DROP TABLE IF EXISTS sys_user;
   ```

**注意**: 回滚操作会删除所有数据，请谨慎操作。建议在生产环境回滚前先备份数据。

## 验证

执行完所有 SQL 脚本后，验证以下内容：

1. 所有表已创建成功
2. 所有索引已创建成功
3. 所有外键约束已创建成功
4. 表结构符合数据模型定义

## 相关文档

- [数据模型文档](../../../../specs/011-system-service/data-model.md)
- [规划文档](../../../../specs/011-system-service/plan.md)

