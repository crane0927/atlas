/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * Long 类型自定义序列化器
 *
 * <p>将 Long 类型序列化为 String，避免前端 JavaScript 精度丢失。
 *
 * <p>JavaScript 的 Number 类型只能安全表示 2^53 以内的整数（即 -9007199254740991 到 9007199254740991），超过此范围的 Long
 * 值会丢失精度。通过将 Long 序列化为 String，前端可以安全处理大整数。
 *
 * <p>使用示例：
 *
 * <pre>
 * // 在 JacksonConfig 中注册
 * SimpleModule module = new SimpleModule();
 * module.addSerializer(Long.class, new LongToStringSerializer());
 * mapper.registerModule(module);
 * </pre>
 *
 * <p>序列化结果：
 *
 * <pre>
 * // 序列化前
 * {"id": 1234567890123456789}
 *
 * // 序列化后
 * {"id": "1234567890123456789"}
 * </pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public class LongToStringSerializer extends JsonSerializer<Long> {

  /**
   * 序列化 Long 为 String
   *
   * @param value Long 值
   * @param gen JSON 生成器
   * @param serializers 序列化提供者
   * @throws IOException 如果序列化失败
   */
  @Override
  public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    if (value == null) {
      // null 值由 Jackson 的 null 处理策略决定（通常是忽略或写入 null）
      gen.writeNull();
    } else {
      // 将 Long 转换为 String
      gen.writeString(String.valueOf(value));
    }
  }
}
