/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.db.handler;

import com.atlas.common.feature.security.context.SecurityContextHolder;
import com.atlas.common.feature.security.provider.CurrentUserProvider;
import com.atlas.common.feature.security.user.LoginUser;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * 审计字段自动填充处理器
 *
 * <p>实现 MyBatis-Plus 的 {@link MetaObjectHandler} 接口，用于自动填充审计字段。
 *
 * <p>填充的字段包括：
 *
 * <ul>
 *   <li>createTime：创建时间（插入时填充）
 *   <li>updateTime：更新时间（插入和更新时填充）
 *   <li>createBy：创建人（插入时填充，从安全上下文获取）
 *   <li>updateBy：更新人（更新时填充，从安全上下文获取）
 * </ul>
 *
 * <p>使用说明：
 *
 * <ul>
 *   <li>实体类需要使用 {@link com.baomidou.mybatisplus.annotation.TableField} 注解标记需要自动填充的字段
 *   <li>fill 属性设置为 {@link com.baomidou.mybatisplus.annotation.FieldFill#INSERT} 或 {@link
 *       com.baomidou.mybatisplus.annotation.FieldFill#INSERT_UPDATE}
 *   <li>如果安全上下文获取失败，创建人和更新人字段会填充默认值 "system"
 * </ul>
 *
 * <p>依赖说明：
 *
 * <ul>
 *   <li>优先使用注入的 {@link CurrentUserProvider}（若存在）获取当前用户名
 *   <li>否则回退到 {@link SecurityContextHolder} 获取当前用户信息
 *   <li>如果均未实现或获取失败，getCurrentUser() 方法会返回默认值 "system"
 * </ul>
 *
 * @author Atlas
 */
@Component
@Slf4j
public class AuditMetaObjectHandler implements MetaObjectHandler {

  private final Optional<CurrentUserProvider> currentUserProvider;

  public AuditMetaObjectHandler(ObjectProvider<CurrentUserProvider> currentUserProvider) {
    this.currentUserProvider = Optional.ofNullable(currentUserProvider.getIfAvailable());
  }

  /**
   * 插入时填充字段
   *
   * <p>填充创建时间、更新时间和创建人字段。
   *
   * @param metaObject 元对象
   */
  @Override
  public void insertFill(MetaObject metaObject) {
    LocalDateTime now = LocalDateTime.now();
    // 填充创建时间
    this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
    // 填充更新时间
    this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
    // 填充创建人和更新人
    String currentUser = getCurrentUser();
    this.strictInsertFill(metaObject, "createBy", String.class, currentUser);
    this.strictInsertFill(metaObject, "updateBy", String.class, currentUser);
  }

  /**
   * 更新时填充字段
   *
   * <p>填充更新时间和更新人字段。使用 setValue 强制覆盖，确保每次更新都写入当前时间和当前用户（strictUpdateFill 仅在字段为 null 时填充，实体从 DB
   * 加载后已有值则不会覆盖）。
   *
   * @param metaObject 元对象
   */
  @Override
  public void updateFill(MetaObject metaObject) {
    LocalDateTime now = LocalDateTime.now();
    String currentUser = getCurrentUser();
    if (metaObject.hasSetter("updateTime")) {
      metaObject.setValue("updateTime", now);
    }
    if (metaObject.hasSetter("updateBy")) {
      metaObject.setValue("updateBy", currentUser);
    }
  }

  /**
   * 获取当前用户信息
   *
   * <p>从安全上下文获取当前登录用户的用户名。如果获取失败（未登录、安全模块未实现等）， 返回默认值 "system"。
   *
   * <p>该方法不会抛出异常，确保审计字段填充功能不会阻塞主业务流程。
   *
   * @return 当前用户名，如果获取失败返回 "system"
   */
  private String getCurrentUser() {
    try {
      if (currentUserProvider.isPresent()) {
        String username = currentUserProvider.get().getCurrentUsername();
        if (username != null && !username.isBlank()) {
          return username;
        }
      }
      LoginUser loginUser = SecurityContextHolder.getLoginUser();
      if (loginUser != null && loginUser.getUsername() != null) {
        return loginUser.getUsername();
      }
    } catch (Exception e) {
      // 安全模块可能未实现，记录警告日志但不阻塞主流程
      log.warn("获取当前用户信息失败，使用默认值 'system'", e);
    }
    return "system";
  }
}
