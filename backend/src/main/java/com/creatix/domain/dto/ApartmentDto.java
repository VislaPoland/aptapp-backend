package com.creatix.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Account data transfer object
 */
@ApiModel
@Data
public class ApartmentDto {
    @ApiModelProperty(value = "Full name", required = true)
    private String fullName;

    @ApiModelProperty(value = "Email address", required = true)
    private String primaryEmail;

    @ApiModelProperty(value = "Address", required = true)
    private AddressDto address;

    @ApiModelProperty(value = "Associated property ID", required = false, notes = "This is required for tenant and pproperty manager")
    private Long propertyId;

    @ApiModelProperty(required = true)
    private Neighbors neighbors;

    @ApiModel
    @Data
    public static class Neighbors {
        @ApiModelProperty
        private NeighborApartment above;
        @ApiModelProperty
        private NeighborApartment below;
        @ApiModelProperty
        private NeighborApartment left;
        @ApiModelProperty
        private NeighborApartment right;
        @ApiModelProperty
        private NeighborApartment opposite;
        @ApiModelProperty
        private NeighborApartment behind;
    }


    @ApiModel
    @Data
    public static class NeighborApartment {
        @ApiModelProperty(required = true)
        private long id;
        @ApiModelProperty(required = true)
        private String unitNumber;
    }
}
