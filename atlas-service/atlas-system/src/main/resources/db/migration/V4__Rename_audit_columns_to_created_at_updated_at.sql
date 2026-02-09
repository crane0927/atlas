-- Flyway 迁移脚本 V4: 审计列重命名 create_time/update_time → created_at/updated_at，与实体 createdAt/updatedAt 默认映射一致

ALTER TABLE sys_user RENAME COLUMN create_time TO created_at;
ALTER TABLE sys_user RENAME COLUMN update_time TO updated_at;

ALTER TABLE sys_role RENAME COLUMN create_time TO created_at;
ALTER TABLE sys_role RENAME COLUMN update_time TO updated_at;

ALTER TABLE sys_permission RENAME COLUMN create_time TO created_at;
ALTER TABLE sys_permission RENAME COLUMN update_time TO updated_at;

ALTER TABLE sys_system_setting RENAME COLUMN create_time TO created_at;
ALTER TABLE sys_system_setting RENAME COLUMN update_time TO updated_at;
