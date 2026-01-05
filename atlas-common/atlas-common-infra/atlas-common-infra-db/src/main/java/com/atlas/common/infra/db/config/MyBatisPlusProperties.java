/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.db.config;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MyBatis-Plus 配置属性类
 *
 * <p>用于读取配置文件中的 MyBatis-Plus 相关配置，支持通过 application.yml 配置分页参数等。
 *
 * <p>配置示例：
 *
 * <pre>{@code
 * atlas:
 *   mybatis-plus:
 *     pagination:
 *       max-limit: 1000
 *       overflow: false
 *       db-type: POSTGRE_SQL
 * }</pre>
 *
 * @author Atlas
 */
@Data
@ConfigurationProperties(prefix = "atlas.mybatis-plus")
public class MyBatisPlusProperties {

  /** 分页配置 */
  private PaginationProperties pagination = new PaginationProperties();

  /**
   * 分页配置属性类
   *
   * <p>用于配置分页插件的参数，包括最大每页数量、溢出处理、数据库类型等。
   *
   * @author Atlas
   */
  @Data
  public static class PaginationProperties {

    /** 最大每页数量，默认值为 1000 */
    private Long maxLimit = 1000L;

    /** 溢出总页数后处理，默认值为 false */
    private Boolean overflow = false;

    /** 数据库类型，默认值为 POSTGRE_SQL */
    private DbType dbType = DbType.POSTGRE_SQL;
  }
}
