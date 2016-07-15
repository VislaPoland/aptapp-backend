package com.creatix.domain.dto.account;

import com.creatix.domain.dto.property.PropertyDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel
@Getter
@Setter
public class PropertyOwnerDto extends AccountDto {
    @ApiModelProperty(value = "Owned properties")
    private List<PropertyDto> ownedProperties;
}
