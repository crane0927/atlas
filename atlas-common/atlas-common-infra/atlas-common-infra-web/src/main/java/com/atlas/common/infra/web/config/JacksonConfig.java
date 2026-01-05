/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.config;

import com.atlas.common.infra.web.serializer.LongToStringSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson JSON 序列化配置类
 *
 * <p>提供统一的 {@link ObjectMapper} Bean，配置所有模块使用一致的 JSON 序列化格式。
 *
 * <p>配置项：
 *
 * <ul>
 *   <li>日期时间格式：ISO-8601 格式（yyyy-MM-dd'T'HH:mm:ss.SSSZ）
 *   <li>空值处理：忽略 null 值（NON_NULL）
 *   <li>序列化特性：日期不序列化为时间戳，使用字符串格式
 *   <li>反序列化特性：忽略未知属性，不失败
 *   <li>时区处理：使用系统默认时区
 *   <li>自定义序列化器：Long 类型序列化为 String，避免前端精度丢失
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>
 * // 自动注入 ObjectMapper
 * @Autowired
 * private ObjectMapper objectMapper;
 *
 * // 序列化对象
 * String json = objectMapper.writeValueAsString(object);
 * </pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Configuration
public class JacksonConfig {

  /** ISO-8601 日期时间格式 */
  private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  /**
   * 配置 ObjectMapper Bean
   *
   * <p>使用 {@code @Primary} 注解，确保此配置优先于 Spring Boot 的默认配置。
   *
   * @param builder Jackson2ObjectMapperBuilder
   * @return 配置后的 ObjectMapper
   */
  @Bean
  @Primary
  public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
    ObjectMapper mapper = builder.createXmlMapper(false).build();

    // 配置日期时间格式（ISO-8601）
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addSerializer(
        LocalDateTime.class,
        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));
    mapper.registerModule(javaTimeModule);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // 配置空值处理（忽略 null 值）
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    // 配置序列化特性
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.configure(SerializationFeature.INDENT_OUTPUT, false);

    // 配置反序列化特性
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

    // 配置时区处理（使用系统默认时区）
    mapper.setTimeZone(java.util.TimeZone.getDefault());

    // 注册自定义序列化器（Long 类型序列化为 String）
    SimpleModule longModule = new SimpleModule();
    longModule.addSerializer(Long.class, new LongToStringSerializer());
    longModule.addSerializer(Long.TYPE, new LongToStringSerializer());
    mapper.registerModule(longModule);

    return mapper;
  }
}

