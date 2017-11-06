package com.creatix.domain.dto.apartment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class ApartmentDto extends BasicApartmentDto {
    @ApiModelProperty(value = "Full name", required = true)
    private String fullName;

    @ApiModelProperty(value = "Email address", required = true)
    private String primaryEmail;

    @ApiModelProperty(value = "Associated property ID", notes = "This is required for tenant and property manager")
    private Long propertyId;

    @ApiModelProperty(value = "Floor number")
    private String floor;

    @ApiModelProperty(value = "Tenant ID")
    private Long tenantId;

    @ApiModelProperty(required = true)
    private NeighborsDto neighbors;

    @ApiModel
    @Data
    public static class NeighborsDto {
        @ApiModelProperty
        private NeighborApartmentDto above;
        @ApiModelProperty
        private NeighborApartmentDto below;
        @ApiModelProperty
        private NeighborApartmentDto left;
        @ApiModelProperty
        private NeighborApartmentDto right;
    }


    @ApiModel
    @Data
    public static class NeighborApartmentDto {
        @ApiModelProperty(required = true)
        private long id;
        @ApiModelProperty(required = true)
        private String unitNumber;
        @ApiModelProperty(value = "Floor number")
        private String floor;
    }

    @ApiModel
    @Data
    public static class TenantDto {
        @ApiModelProperty(required = true)
        private Long id;
        @ApiModelProperty(required = true)
        private String firstName;
        @ApiModelProperty(required = true)
        private String lastName;
        @ApiModelProperty
        private String fullName;
        @ApiModelProperty(required = true)
        private String email;
        @ApiModelProperty(required = true)
        private String phone;
        @ApiModelProperty
        private Boolean deleted;
    }

}
