/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.db.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/**
 * BaseEntity 单元测试
 *
 * @author Atlas
 */
class BaseEntityTest {

  @Test
  void testBaseEntityCreation() {
    BaseEntity entity = new BaseEntity();
    assertNotNull(entity);
  }

  @Test
  void testDeletedField() throws NoSuchFieldException {
    Field deletedField = BaseEntity.class.getDeclaredField("deleted");
    assertNotNull(deletedField);
    TableLogic tableLogic = deletedField.getAnnotation(TableLogic.class);
    assertNotNull(tableLogic);
    assertEquals("0", tableLogic.value());
    assertEquals("1", tableLogic.delVal());
  }

  @Test
  void testCreateTimeField() throws NoSuchFieldException {
    Field createTimeField = BaseEntity.class.getDeclaredField("createTime");
    assertNotNull(createTimeField);
    TableField tableField = createTimeField.getAnnotation(TableField.class);
    assertNotNull(tableField);
    assert (tableField.fill() == FieldFill.INSERT);
  }

  @Test
  void testUpdateTimeField() throws NoSuchFieldException {
    Field updateTimeField = BaseEntity.class.getDeclaredField("updateTime");
    assertNotNull(updateTimeField);
    TableField tableField = updateTimeField.getAnnotation(TableField.class);
    assertNotNull(tableField);
    assert (tableField.fill() == FieldFill.INSERT_UPDATE);
  }

  @Test
  void testCreateByField() throws NoSuchFieldException {
    Field createByField = BaseEntity.class.getDeclaredField("createBy");
    assertNotNull(createByField);
    TableField tableField = createByField.getAnnotation(TableField.class);
    assertNotNull(tableField);
    assert (tableField.fill() == FieldFill.INSERT);
  }

  @Test
  void testUpdateByField() throws NoSuchFieldException {
    Field updateByField = BaseEntity.class.getDeclaredField("updateBy");
    assertNotNull(updateByField);
    TableField tableField = updateByField.getAnnotation(TableField.class);
    assertNotNull(tableField);
    assert (tableField.fill() == FieldFill.INSERT_UPDATE);
  }

  @Test
  void testFieldTypes() {
    BaseEntity entity = new BaseEntity();
    // 验证字段类型
    assertNull(entity.getDeleted());
    assertNull(entity.getCreateTime());
    assertNull(entity.getUpdateTime());
    assertNull(entity.getCreateBy());
    assertNull(entity.getUpdateBy());
  }

  @Test
  void testSettersAndGetters() {
    BaseEntity entity = new BaseEntity();
    Integer deleted = 0;
    LocalDateTime now = LocalDateTime.now();
    String user = "testuser";

    entity.setDeleted(deleted);
    entity.setCreateTime(now);
    entity.setUpdateTime(now);
    entity.setCreateBy(user);
    entity.setUpdateBy(user);

    assertEquals(deleted, entity.getDeleted());
    assert (entity.getCreateTime().equals(now));
    assert (entity.getUpdateTime().equals(now));
    assert (entity.getCreateBy().equals(user));
    assert (entity.getUpdateBy().equals(user));
  }
}
