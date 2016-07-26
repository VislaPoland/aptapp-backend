package com.creatix.domain.dto.tenant;

import com.creatix.domain.dto.account.PersistAccountRequest;
import com.creatix.domain.enums.TenantType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

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

    @ApiModelProperty(value = "Parking stalls")
    private List<ParkingStallDto> parkingStalls;

    @ApiModelProperty(value = "Vehicles")
    private List<VehicleDto> vehicles;

}
