package com.creatix.domain.dto.apartment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class BasicApartmentDto {
    @ApiModelProperty(value = "Apartment ID", required = true)
    private Long id;
    @ApiModelProperty(value = "Apartment unit number", required = true)
    private String unitNumber;
    @ApiModelProperty(value = "Tenant name", required = true)
    private String tenantName;

    public BasicApartmentDto(Long id, String unitNumber) {
        setId(id);
        setUnitNumber(unitNumber);
    }
}
