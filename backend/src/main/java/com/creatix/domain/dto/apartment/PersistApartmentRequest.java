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
    private String floor;

    @ApiModelProperty(required = true)
    @NotNull
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
        private String unitNumber;
    }

}
