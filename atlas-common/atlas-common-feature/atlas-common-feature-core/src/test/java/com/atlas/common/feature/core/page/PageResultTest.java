/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.feature.core.page;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

/** PageResult 类单元测试 */
class PageResultTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    MDC.clear();
  }

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  @Test
  void testOfWithFullParameters() {
    // Given
    List<String> list = Arrays.asList("item1", "item2", "item3");
    Long total = 100L;
    Integer page = 1;
    Integer size = 10;

    // When
    PageResult<String> result = PageResult.of(list, total, page, size);

    // Then
    assertNotNull(result);
    assertEquals(list, result.getList());
    assertEquals(total, result.getTotal());
    assertEquals(page, result.getPage());
    assertEquals(size, result.getSize());
    assertEquals(10, result.getPages()); // 100 / 10 = 10
  }

  @Test
  void testOfWithTotalOnly() {
    // Given
    List<String> list = Arrays.asList("item1", "item2");
    Long total = 2L;

    // When
    PageResult<String> result = PageResult.of(list, total);

    // Then
    assertNotNull(result);
    assertEquals(list, result.getList());
    assertEquals(total, result.getTotal());
    assertEquals(1, result.getPage());
    assertEquals(2, result.getSize()); // list.size()
    assertEquals(1, result.getPages());
  }

  @Test
  void testOfWithNullList() {
    // Given
    Long total = 100L;
    Integer page = 1;
    Integer size = 10;

    // When
    PageResult<String> result = PageResult.of(null, total, page, size);

    // Then
    assertNotNull(result);
    assertEquals(Collections.emptyList(), result.getList());
    assertEquals(total, result.getTotal());
  }

  @Test
  void testOfWithNullTotal() {
    // Given
    List<String> list = Arrays.asList("item1", "item2");
    Integer page = 1;
    Integer size = 10;

    // When
    PageResult<String> result = PageResult.of(list, null, page, size);

    // Then
    assertNotNull(result);
    assertEquals(0L, result.getTotal());
    assertEquals(0, result.getPages());
  }

  @Test
  void testOfWithInvalidPage() {
    // Given
    List<String> list = Arrays.asList("item1", "item2");
    Long total = 100L;
    Integer page = 0; // 无效页码
    Integer size = 10;

    // When
    PageResult<String> result = PageResult.of(list, total, page, size);

    // Then
    assertNotNull(result);
    assertEquals(1, result.getPage()); // 应该修正为 1
  }

  @Test
  void testOfWithInvalidSize() {
    // Given
    List<String> list = Arrays.asList("item1", "item2");
    Long total = 100L;
    Integer page = 1;
    Integer size = 0; // 无效大小

    // When
    PageResult<String> result = PageResult.of(list, total, page, size);

    // Then
    assertNotNull(result);
    assertEquals(2, result.getSize()); // 应该使用 list.size()
  }

  @Test
  void testPagesCalculation() {
    // Given & When
    PageResult<String> result1 = PageResult.of(Arrays.asList("1"), 100L, 1, 10);
    PageResult<String> result2 = PageResult.of(Arrays.asList("1"), 101L, 1, 10);
    PageResult<String> result3 = PageResult.of(Arrays.asList("1"), 99L, 1, 10);

    // Then
    assertEquals(10, result1.getPages()); // 100 / 10 = 10
    assertEquals(11, result2.getPages()); // (101 + 10 - 1) / 10 = 11
    assertEquals(10, result3.getPages()); // (99 + 10 - 1) / 10 = 10
  }

  @Test
  void testHasPrevious() {
    // Given & When
    PageResult<String> result1 = PageResult.of(Arrays.asList("1"), 100L, 1, 10);
    PageResult<String> result2 = PageResult.of(Arrays.asList("1"), 100L, 2, 10);

    // Then
    assertFalse(result1.hasPrevious());
    assertTrue(result2.hasPrevious());
  }

  @Test
  void testHasNext() {
    // Given & When
    PageResult<String> result1 = PageResult.of(Arrays.asList("1"), 100L, 1, 10);
    PageResult<String> result2 = PageResult.of(Arrays.asList("1"), 100L, 10, 10);
    PageResult<String> result3 = PageResult.of(Arrays.asList("1"), 100L, 11, 10);

    // Then
    assertTrue(result1.hasNext());
    assertFalse(result2.hasNext());
    assertFalse(result3.hasNext());
  }

  @Test
  void testIsFirst() {
    // Given & When
    PageResult<String> result1 = PageResult.of(Arrays.asList("1"), 100L, 1, 10);
    PageResult<String> result2 = PageResult.of(Arrays.asList("1"), 100L, 2, 10);

    // Then
    assertTrue(result1.isFirst());
    assertFalse(result2.isFirst());
  }

  @Test
  void testIsLast() {
    // Given & When
    PageResult<String> result1 = PageResult.of(Arrays.asList("1"), 100L, 1, 10);
    PageResult<String> result2 = PageResult.of(Arrays.asList("1"), 100L, 10, 10);
    PageResult<String> result3 = PageResult.of(Arrays.asList("1"), 0L, 1, 10);

    // Then
    assertFalse(result1.isLast());
    assertTrue(result2.isLast());
    assertTrue(result3.isLast()); // total = 0 时，pages = 0，isLast = true
  }

  @Test
  void testBoundaryCaseTotalZero() {
    // Given & When
    PageResult<String> result = PageResult.of(Collections.emptyList(), 0L, 1, 10);

    // Then
    assertNotNull(result);
    assertEquals(0L, result.getTotal());
    assertEquals(0, result.getPages());
    assertTrue(result.isLast());
    assertFalse(result.hasNext());
    assertFalse(result.hasPrevious());
  }

  @Test
  void testBoundaryCaseEmptyList() {
    // Given & When
    PageResult<String> result = PageResult.of(Collections.emptyList(), 100L, 1, 10);

    // Then
    assertNotNull(result);
    assertTrue(result.getList().isEmpty());
    assertEquals(100L, result.getTotal());
    assertEquals(10, result.getPages());
  }

  @Test
  void testBoundaryCasePageGreaterThanPages() {
    // Given
    List<String> list = Arrays.asList("item1");
    Long total = 10L;
    Integer page = 100; // 页码大于总页数
    Integer size = 10;

    // When
    PageResult<String> result = PageResult.of(list, total, page, size);

    // Then
    assertNotNull(result);
    assertEquals(100, result.getPage());
    assertEquals(1, result.getPages()); // total=10, size=10, pages=1
    // 当 page > pages 时，isLast() 返回 false（因为 page != pages）
    assertFalse(result.isLast());
    assertFalse(result.hasNext());
  }

  @Test
  void testTraceIdFromMDC() {
    // Given
    String traceId = "test-trace-id-12345";
    MDC.put("traceId", traceId);
    List<String> list = Arrays.asList("item1");
    Long total = 10L;

    // When
    PageResult<String> result = PageResult.of(list, total, 1, 10);

    // Then
    assertNotNull(result);
    assertEquals(traceId, result.getTraceId());
  }

  @Test
  void testTraceIdNullWhenMDCEmpty() {
    // Given - MDC is cleared
    List<String> list = Arrays.asList("item1");
    Long total = 10L;

    // When
    PageResult<String> result = PageResult.of(list, total, 1, 10);

    // Then
    assertNotNull(result);
    assertNull(result.getTraceId());
  }

  @Test
  void testJsonSerialization() throws Exception {
    // Given
    List<String> list = Arrays.asList("item1", "item2");
    PageResult<String> result = PageResult.of(list, 100L, 1, 10);

    // When
    String json = objectMapper.writeValueAsString(result);

    // Then
    assertNotNull(json);
    assertTrue(json.contains("\"list\""));
    assertTrue(json.contains("\"total\":100"));
    assertTrue(json.contains("\"page\":1"));
    assertTrue(json.contains("\"size\":10"));
    assertTrue(json.contains("\"pages\":10"));
  }

  @Test
  void testJsonSerializationWithTraceId() throws Exception {
    // Given
    String traceId = "test-trace-id-12345";
    MDC.put("traceId", traceId);
    List<String> list = Arrays.asList("item1");
    PageResult<String> result = PageResult.of(list, 10L, 1, 10);

    // When
    String json = objectMapper.writeValueAsString(result);

    // Then
    assertNotNull(json);
    assertTrue(json.contains("\"traceId\":\"test-trace-id-12345\""));
  }

  @Test
  void testJsonSerializationWithoutTraceId() throws Exception {
    // Given - MDC is cleared
    List<String> list = Arrays.asList("item1");
    PageResult<String> result = PageResult.of(list, 10L, 1, 10);

    // When
    String json = objectMapper.writeValueAsString(result);

    // Then
    assertNotNull(json);
    // PageResult 没有 @JsonInclude(NON_NULL)，所以 traceId 为 null 时也会序列化为 null
    assertTrue(json.contains("\"traceId\":null"));
  }
}
