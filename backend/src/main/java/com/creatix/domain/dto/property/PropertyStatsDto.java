package com.creatix.domain.dto.property;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Tomas Sedlak on 6.9.2017.
 */
@ApiModel
@Data
public class PropertyStatsDto {
    @ApiModelProperty(value = "Property ID", required = true)
    private Long id;
    @ApiModelProperty(value = "Employee count", notes = "Property manager, assistant property manager, security, maintenance", required = true)
    private long employeeCount;
    @ApiModelProperty(value = "Activated employee count", notes = "Property manager, assistant property manager, security, maintenance", required = true)
    private long activatedEmployeeCount;
    @ApiModelProperty(value = "Resident count", notes = "Tenants and sub-tenants", required = true)
    private long residentCount;
    @ApiModelProperty(value = "Activated resident count", notes = "Tenants and sub-tenants", required = true)
    private long activatedResidentCount;
}
