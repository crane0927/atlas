/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.db.config;

import com.atlas.common.infra.db.handler.AuditMetaObjectHandler;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * MyBatis-Plus 自动配置
 *
 * <p>为引入 atlas-common-infra-db 模块的服务自动注册 MyBatis-Plus 相关配置：
 *
 * <ul>
 *   <li>{@link MyBatisPlusConfig} - 分页插件等配置
 *   <li>{@link AuditMetaObjectHandler} - 审计字段自动填充处理器
 * </ul>
 *
 * <p>条件：当类路径中存在 {@link MetaObjectHandler} 时生效。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(MetaObjectHandler.class)
@Import({MyBatisPlusConfig.class, AuditMetaObjectHandler.class})
public class MyBatisPlusAutoConfiguration {}
