/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.gateway.client;

import java.util.List;
import lombok.Data;

/**
 * Introspection 接口返回的 data 结构（与 atlas-auth IntrospectResponseVO 一致）
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class IntrospectDataDto {

  private Boolean active;
  private Long userId;
  private String username;
  private List<String> roles;
  private List<String> permissions;
  private Long expiresAt;
}
