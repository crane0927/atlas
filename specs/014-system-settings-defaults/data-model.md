# 数据模型

## 实体: SystemSetting（设置项）

**说明**: 对应系统默认设置表，实体类必须继承 `BaseEntity`。

### 字段

- id: 主键
- key: 配置项唯一标识（唯一）
- value: 配置值
- type: 设置类型（SYSTEM / CUSTOM）
- deleted: 逻辑删除标记（来自 `BaseEntity`）
- createTime / updateTime: 审计时间（来自 `BaseEntity`）
- createBy / updateBy: 审计人（来自 `BaseEntity`）

### 约束与校验

- key 全局唯一
- type 为 SYSTEM 时禁止删除，仅允许修改 value
- value 不允许为空

## 关系

- 设置项与其他实体无外键关联
