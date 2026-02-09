-- Flyway 迁移脚本 V5: 审计列重命名 create_by/update_by → created_by/updated_by，与实体 createdBy/updatedBy 默认映射一致

ALTER TABLE sys_user RENAME COLUMN create_by TO created_by;
ALTER TABLE sys_user RENAME COLUMN update_by TO updated_by;

ALTER TABLE sys_role RENAME COLUMN create_by TO created_by;
ALTER TABLE sys_role RENAME COLUMN update_by TO updated_by;

ALTER TABLE sys_permission RENAME COLUMN create_by TO created_by;
ALTER TABLE sys_permission RENAME COLUMN update_by TO updated_by;

ALTER TABLE sys_system_setting RENAME COLUMN create_by TO created_by;
ALTER TABLE sys_system_setting RENAME COLUMN update_by TO updated_by;
