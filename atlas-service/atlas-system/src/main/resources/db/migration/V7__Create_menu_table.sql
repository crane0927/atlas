-- Flyway 迁移脚本 V7: 创建菜单表
-- 说明: 动态菜单与按钮权限

CREATE TABLE IF NOT EXISTS sys_menu (
    menu_id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT,
    name VARCHAR(100) NOT NULL,
    path VARCHAR(255),
    component VARCHAR(255),
    redirect VARCHAR(255),
    icon VARCHAR(100),
    type VARCHAR(20) NOT NULL,
    sort INTEGER NOT NULL DEFAULT 0,
    visible BOOLEAN NOT NULL DEFAULT true,
    keep_alive BOOLEAN NOT NULL DEFAULT false,
    external BOOLEAN NOT NULL DEFAULT false,
    permission_code VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deleted INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(64) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(64) NOT NULL DEFAULT 'system'
);

CREATE INDEX IF NOT EXISTS idx_menu_parent_id ON sys_menu(parent_id);
CREATE INDEX IF NOT EXISTS idx_menu_type ON sys_menu(type);
CREATE INDEX IF NOT EXISTS idx_menu_status ON sys_menu(status);
CREATE INDEX IF NOT EXISTS idx_menu_permission_code ON sys_menu(permission_code);

COMMENT ON TABLE sys_menu IS '菜单表';
COMMENT ON COLUMN sys_menu.menu_id IS '菜单ID';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID';
COMMENT ON COLUMN sys_menu.name IS '菜单名称';
COMMENT ON COLUMN sys_menu.path IS '路由路径';
COMMENT ON COLUMN sys_menu.component IS '前端组件标识';
COMMENT ON COLUMN sys_menu.redirect IS '路由重定向';
COMMENT ON COLUMN sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN sys_menu.type IS '菜单类型（DIR/MENU/BUTTON）';
COMMENT ON COLUMN sys_menu.sort IS '排序号';
COMMENT ON COLUMN sys_menu.visible IS '是否显示';
COMMENT ON COLUMN sys_menu.keep_alive IS '是否缓存';
COMMENT ON COLUMN sys_menu.external IS '是否外链';
COMMENT ON COLUMN sys_menu.permission_code IS '权限码';
COMMENT ON COLUMN sys_menu.status IS '状态（ACTIVE/INACTIVE/DELETED）';
COMMENT ON COLUMN sys_menu.deleted IS '逻辑删除标记（0未删除，1已删除）';
COMMENT ON COLUMN sys_menu.created_at IS '创建时间';
COMMENT ON COLUMN sys_menu.updated_at IS '更新时间';
COMMENT ON COLUMN sys_menu.created_by IS '创建人';
COMMENT ON COLUMN sys_menu.updated_by IS '更新人';
