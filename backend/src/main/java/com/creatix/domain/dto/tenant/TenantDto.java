package com.creatix.domain.dto.tenant;

import com.creatix.domain.dto.account.AccountDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel
@Getter
@Setter
public class TenantDto extends AccountDto {

    @ApiModelProperty(value = "Indicate that sms message notifications are enabled/disabled")
    private Boolean enableSms;

    @ApiModelProperty(value = "Assigned parking stalls")
    private List<ParkingStallDto> parkingStalls;

    @ApiModelProperty(value = "Registered tenant vehicles")
    private List<VehicleDto> vehicles;

    @ApiModelProperty(value = "Sub-tenants")
    private List<SubTenantDto> subTenants;

    @ApiModelProperty(value = "Id of the assigned apartment")
    private Long apartmentId;

    @ApiModel
    @Data
    public static class SubTenantDto {
        @ApiModelProperty(value = "Sub-tenant ID", required = true)
        private Long id;

        @ApiModelProperty(value = "First name", required = true)
        private String firstName;

        @ApiModelProperty(value = "Last name", required = true)
        private String lastName;

        @ApiModelProperty(value = "Full name", required = true)
        private String fullName;

        @ApiModelProperty(value = "Phone number", required = true)
        private String primaryPhone;

        @ApiModelProperty(value = "Email address", required = true)
        private String primaryEmail;

        @ApiModelProperty(value = "Company name")
        private String companyName;
    }
}
