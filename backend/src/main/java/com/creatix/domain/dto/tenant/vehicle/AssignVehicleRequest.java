package com.creatix.domain.dto.tenant.vehicle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class AssignVehicleRequest extends AbstractVehicleDto {
}
