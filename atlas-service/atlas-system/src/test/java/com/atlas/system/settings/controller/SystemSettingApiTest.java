/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.settings.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.infra.web.exception.GlobalExceptionHandler;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.settings.model.dto.SystemSettingCreateDTO;
import com.atlas.system.settings.model.enums.SystemSettingType;
import com.atlas.system.settings.model.vo.SystemSettingVO;
import com.atlas.system.settings.service.SystemSettingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * SystemSetting API 测试
 *
 * <p>验证系统设置新增与删除接口的响应结构。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@WebMvcTest(
    controllers = SystemSettingController.class,
    excludeAutoConfiguration = {
      org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    })
@Import(GlobalExceptionHandler.class)
@TestPropertySource(
    properties = {
      "spring.autoconfigure.exclude=com.alibaba.cloud.nacos.NacosConfigAutoConfiguration,com.alibaba.cloud.nacos.NacosDiscoveryAutoConfiguration,com.alibaba.cloud.nacos.endpoint.NacosConfigEndpointAutoConfiguration",
      "spring.cloud.nacos.config.enabled=false",
      "spring.cloud.nacos.discovery.enabled=false"
    })
class SystemSettingApiTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private SystemSettingService systemSettingService;

  @Test
  void testCreateSystemSettingSuccess() throws Exception {
    SystemSettingCreateDTO createDTO = new SystemSettingCreateDTO();
    createDTO.setKey("theme.color");
    createDTO.setValue("blue");

    SystemSettingVO vo = new SystemSettingVO();
    vo.setKey("theme.color");
    vo.setValue("blue");
    vo.setType(SystemSettingType.CUSTOM);

    when(systemSettingService.createSetting(any())).thenReturn(vo);

    mockMvc
        .perform(
            post("/api/v1/system-settings")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(createDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data.key").value("theme.color"))
        .andExpect(jsonPath("$.data.value").value("blue"))
        .andExpect(jsonPath("$.data.type").value("CUSTOM"));
  }

  @Test
  void testDeleteSystemSettingForbidden() throws Exception {
    doThrow(
            new BusinessException(
                SystemErrorCode.SYSTEM_SETTING_SYSTEM_DELETE_FORBIDDEN, "系统默认设置不可删除"))
        .when(systemSettingService)
        .deleteCustomSetting("site.name");

    mockMvc
        .perform(delete("/api/v1/system-settings/site.name"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(SystemErrorCode.SYSTEM_SETTING_SYSTEM_DELETE_FORBIDDEN))
        .andExpect(jsonPath("$.message").value("系统默认设置不可删除"));
  }
}
