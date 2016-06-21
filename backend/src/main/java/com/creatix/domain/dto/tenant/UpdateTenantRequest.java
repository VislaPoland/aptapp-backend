package com.creatix.domain.dto.tenant;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateTenantRequest extends AbstractTenantRequest {
}
