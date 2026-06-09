/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.db.incrementer;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Sequence;
import org.springframework.stereotype.Component;

/**
 * String 型雪花 ID 生成器
 *
 * <p>用于主键类型为 String 的实体，生成与 Long 雪花等价的数字字符串，便于与已有 BIGINT 迁移为 VARCHAR 的数据兼容。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Component
public class StringSnowflakeIdGenerator implements IdentifierGenerator {

  private final Sequence sequence = new Sequence(null);

  @Override
  public Number nextId(Object entity) {
    return sequence.nextId();
  }

  @Override
  public String nextUUID(Object entity) {
    return String.valueOf(sequence.nextId());
  }
}
