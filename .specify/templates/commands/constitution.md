# Constitution Command

## 描述

创建或更新项目宪法，确保所有依赖模板保持同步。

## 使用说明

使用此命令更新项目宪法时，系统会：

1. 加载现有宪法模板
2. 识别所有占位符
3. 收集/推导占位符值
4. 更新宪法内容
5. 同步更新相关模板文件

## 相关文件

- `.specify/memory/constitution.md` - 宪法主文件
- `.specify/templates/plan-template.md` - 规划模板
- `.specify/templates/spec-template.md` - 规格说明模板
- `.specify/templates/tasks-template.md` - 任务清单模板

## 注意事项

- 宪法版本更新遵循语义化版本规范
- 重大修订需要团队批准
- 更新宪法后必须同步更新相关模板

