-- Flyway 迁移脚本 V6: 为 sys_user_role、sys_role_permission 增加审计与逻辑删除列，对齐 BaseEntity
-- 说明: 两表已有 created_at（V1），新增 deleted、updated_at、created_by、updated_by；命名与 V4/V5 一致

-- ============================================
-- 1. sys_user_role
-- ============================================
ALTER TABLE sys_user_role ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0;
ALTER TABLE sys_user_role ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sys_user_role ADD COLUMN created_by VARCHAR(64) NOT NULL DEFAULT 'system';
ALTER TABLE sys_user_role ADD COLUMN updated_by VARCHAR(64) NOT NULL DEFAULT 'system';

UPDATE sys_user_role SET updated_at = created_at;

COMMENT ON COLUMN sys_user_role.deleted IS '逻辑删除标记（0未删除，1已删除）';
COMMENT ON COLUMN sys_user_role.updated_at IS '更新时间';
COMMENT ON COLUMN sys_user_role.created_by IS '创建人';
COMMENT ON COLUMN sys_user_role.updated_by IS '更新人';

-- ============================================
-- 2. sys_role_permission
-- ============================================
ALTER TABLE sys_role_permission ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0;
ALTER TABLE sys_role_permission ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE sys_role_permission ADD COLUMN created_by VARCHAR(64) NOT NULL DEFAULT 'system';
ALTER TABLE sys_role_permission ADD COLUMN updated_by VARCHAR(64) NOT NULL DEFAULT 'system';

UPDATE sys_role_permission SET updated_at = created_at;

COMMENT ON COLUMN sys_role_permission.deleted IS '逻辑删除标记（0未删除，1已删除）';
COMMENT ON COLUMN sys_role_permission.updated_at IS '更新时间';
COMMENT ON COLUMN sys_role_permission.created_by IS '创建人';
COMMENT ON COLUMN sys_role_permission.updated_by IS '更新人';
