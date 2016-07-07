package com.creatix.domain.dto.apartment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Account data transfer object
 */
@ApiModel
@Data
public class PersistApartmentRequest {

    @ApiModelProperty(value = "Unit number", required = true)
    @NotEmpty
    private String unitNumber;

    @ApiModelProperty(value = "Floor number")
    @NotNull
    private Integer floor;

    @ApiModelProperty(required = true)
    @NotNull
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
        private String unitNumber;
    }

}
