package com.creatix.domain.dto.tenant;

import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.dto.ApartmentDto;
import com.creatix.domain.dto.property.PropertyDetailsDto;
import com.creatix.domain.dto.tenant.parkingStall.ParkingStallDto;
import com.creatix.domain.dto.tenant.vehicle.VehicleDto;
import com.creatix.domain.enums.TenantType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class TenantDto {
    @ApiModelProperty(value = "Account ID", required = true)
    private Long id;

    @ApiModelProperty(value = "First name", required = true)
    private String firstName;

    @ApiModelProperty(value = "Last name", required = true)
    private String lastName;

    @ApiModelProperty(value = "Full name", required = true)
    private String fullName;

    @ApiModelProperty(value = "Primary phone number", required = true)
    private String primaryPhone;

    @ApiModelProperty(value = "Primary email address", required = true)
    private String primaryEmail;

    @ApiModelProperty(value = "Secondary email address")
    private String secondaryEmail;

    @ApiModelProperty(value = "Secondary phone number")
    private String secondaryPhone;

    @ApiModelProperty(value = "Company name")
    private String companyName;

    @ApiModelProperty(value = "Tenant type", required = true)
    private TenantType type;

    @ApiModelProperty(value = "Address", required = true)
    private AddressDto address;

    @ApiModelProperty(value = "Associated property details", required = true)
    private PropertyDetailsDto property;

    @ApiModelProperty(value = "Associated apartment", required = true)
    private ApartmentDto apartment;

    @ApiModelProperty(value = "Assigned parking stalls")
    private List<ParkingStallDto> parkingStalls;

    @ApiModelProperty(value = "Registered tenant vehicles")
    private List<VehicleDto> vehicles;

    @ApiModelProperty(value = "Sub tenants")
    private List<SubTenantDto> subs;

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
        private String phone;

        @ApiModelProperty(value = "Email address", required = true)
        private String email;

        @ApiModelProperty(value = "Company name")
        private String companyName;

        @ApiModelProperty(value = "Tenant type", required = true)
        private TenantType type;
    }
}
