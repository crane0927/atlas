-- Flyway 迁移脚本 V2: 为 sys_user、sys_role、sys_permission 增加审计与逻辑删除列，对齐 BaseEntity
-- 说明: 新增 create_time、update_time、create_by、update_by、deleted；数据回填后删除 created_at、updated_at

-- ============================================
-- 1. sys_user
-- ============================================
ALTER TABLE sys_user ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0;
ALTER TABLE sys_user ADD COLUMN create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sys_user ADD COLUMN update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sys_user ADD COLUMN create_by VARCHAR(64) NOT NULL DEFAULT 'system';
ALTER TABLE sys_user ADD COLUMN update_by VARCHAR(64) NOT NULL DEFAULT 'system';

UPDATE sys_user SET create_time = created_at, update_time = updated_at;

ALTER TABLE sys_user DROP COLUMN created_at;
ALTER TABLE sys_user DROP COLUMN updated_at;

COMMENT ON COLUMN sys_user.deleted IS '逻辑删除标记（0未删除，1已删除）';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_time IS '更新时间';
COMMENT ON COLUMN sys_user.create_by IS '创建人';
COMMENT ON COLUMN sys_user.update_by IS '更新人';

-- ============================================
-- 2. sys_role
-- ============================================
ALTER TABLE sys_role ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0;
ALTER TABLE sys_role ADD COLUMN create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sys_role ADD COLUMN update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sys_role ADD COLUMN create_by VARCHAR(64) NOT NULL DEFAULT 'system';
ALTER TABLE sys_role ADD COLUMN update_by VARCHAR(64) NOT NULL DEFAULT 'system';

UPDATE sys_role SET create_time = created_at, update_time = updated_at;

ALTER TABLE sys_role DROP COLUMN created_at;
ALTER TABLE sys_role DROP COLUMN updated_at;

COMMENT ON COLUMN sys_role.deleted IS '逻辑删除标记（0未删除，1已删除）';
COMMENT ON COLUMN sys_role.create_time IS '创建时间';
COMMENT ON COLUMN sys_role.update_time IS '更新时间';
COMMENT ON COLUMN sys_role.create_by IS '创建人';
COMMENT ON COLUMN sys_role.update_by IS '更新人';

-- ============================================
-- 3. sys_permission
-- ============================================
ALTER TABLE sys_permission ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0;
ALTER TABLE sys_permission ADD COLUMN create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sys_permission ADD COLUMN update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sys_permission ADD COLUMN create_by VARCHAR(64) NOT NULL DEFAULT 'system';
ALTER TABLE sys_permission ADD COLUMN update_by VARCHAR(64) NOT NULL DEFAULT 'system';

UPDATE sys_permission SET create_time = created_at, update_time = updated_at;

ALTER TABLE sys_permission DROP COLUMN created_at;
ALTER TABLE sys_permission DROP COLUMN updated_at;

COMMENT ON COLUMN sys_permission.deleted IS '逻辑删除标记（0未删除，1已删除）';
COMMENT ON COLUMN sys_permission.create_time IS '创建时间';
COMMENT ON COLUMN sys_permission.update_time IS '更新时间';
COMMENT ON COLUMN sys_permission.create_by IS '创建人';
COMMENT ON COLUMN sys_permission.update_by IS '更新人';
