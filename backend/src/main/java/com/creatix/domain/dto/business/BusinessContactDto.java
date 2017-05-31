package com.creatix.domain.dto.business;

import com.creatix.domain.enums.CommunicationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@ApiModel
@Data
@Accessors(chain = true)
public class BusinessContactDto {

    @ApiModelProperty("Contact entry id")
    private Long id;

    @ApiModelProperty(value = "Communication type")
    private CommunicationType communicationType;

    @ApiModelProperty(value = "Value for contact phone/Number")
    private String communicationValue;

    @ApiModelProperty("Contact street")
    private String street;

    @ApiModelProperty("House number")
    private String houseNumber;

    @ApiModelProperty("Zip code")
    private String zipCode;

    @ApiModelProperty("city")
    private String city;

    @ApiModelProperty("State")
    private String state;
}
