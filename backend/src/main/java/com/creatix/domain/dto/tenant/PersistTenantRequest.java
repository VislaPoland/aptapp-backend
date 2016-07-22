package com.creatix.domain.dto.tenant;

import com.creatix.domain.dto.account.PersistAccountRequest;
import com.creatix.domain.enums.TenantType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ApiModel
public class PersistTenantRequest extends PersistAccountRequest {

    @NotNull
    @ApiModelProperty(required = true, value = "Tenant type")
    private TenantType type;

    @NotNull
    @ApiModelProperty(required = true, value = "Apartment ID")
    private Long apartmentId;

    @ApiModelProperty(value = "Parking stall number")
    private String parkingStallNumber;
}
