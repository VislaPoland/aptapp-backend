package com.creatix.domain.dto.tenant.subs;

import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.dto.account.AccountDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class SubTenantDto extends AccountDto {

    @ApiModelProperty(value = "Parent tenant ID")
    private Long parentTenantId;

    @ApiModelProperty(value = "Address", required = true)
    private AddressDto address;
}
