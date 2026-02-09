-- Flyway 迁移脚本 V3: 主键改为应用层雪花算法生成，取消数据库默认值
-- 说明: 对主键列执行 DROP DEFAULT，插入时由应用传入主键（MyBatis-Plus IdType.ASSIGN_ID）

ALTER TABLE sys_user ALTER COLUMN user_id DROP DEFAULT;
ALTER TABLE sys_role ALTER COLUMN role_id DROP DEFAULT;
ALTER TABLE sys_permission ALTER COLUMN permission_id DROP DEFAULT;
ALTER TABLE sys_user_role ALTER COLUMN id DROP DEFAULT;
ALTER TABLE sys_role_permission ALTER COLUMN id DROP DEFAULT;
ALTER TABLE sys_system_setting ALTER COLUMN setting_id DROP DEFAULT;
