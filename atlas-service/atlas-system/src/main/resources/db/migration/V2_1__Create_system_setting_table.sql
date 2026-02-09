-- Flyway 迁移脚本 V2.1: 创建系统设置表（在 V3 之前创建，以便 V3 可对其主键执行 DROP DEFAULT）
-- 说明: 表结构与 V2 审计列命名一致（create_time、update_time、create_by、update_by），由 V4/V5 统一重命名

CREATE TABLE IF NOT EXISTS sys_system_setting (
    setting_id BIGSERIAL PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL,
    setting_value TEXT NOT NULL,
    setting_type VARCHAR(20) NOT NULL,
    deleted INTEGER NOT NULL DEFAULT 0,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(64) NOT NULL DEFAULT 'system',
    update_by VARCHAR(64) NOT NULL DEFAULT 'system'
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_system_setting_key ON sys_system_setting(setting_key);
CREATE INDEX IF NOT EXISTS idx_system_setting_type ON sys_system_setting(setting_type);

COMMENT ON TABLE sys_system_setting IS '系统默认设置表';
COMMENT ON COLUMN sys_system_setting.setting_id IS '设置项ID';
COMMENT ON COLUMN sys_system_setting.setting_key IS '设置项唯一标识';
COMMENT ON COLUMN sys_system_setting.setting_value IS '设置项值';
COMMENT ON COLUMN sys_system_setting.setting_type IS '设置项类型（SYSTEM/CUSTOM）';
COMMENT ON COLUMN sys_system_setting.deleted IS '逻辑删除标记（0未删除，1已删除）';
COMMENT ON COLUMN sys_system_setting.create_time IS '创建时间';
COMMENT ON COLUMN sys_system_setting.update_time IS '更新时间';
COMMENT ON COLUMN sys_system_setting.create_by IS '创建人';
COMMENT ON COLUMN sys_system_setting.update_by IS '更新人';

INSERT INTO sys_system_setting (setting_key, setting_value, setting_type)
VALUES
    ('system.name', 'Atlas', 'SYSTEM'),
    ('system.description', 'Atlas 系统', 'SYSTEM'),
    ('user.default.password', 'ChangeMe123', 'SYSTEM')
ON CONFLICT (setting_key) DO NOTHING;
