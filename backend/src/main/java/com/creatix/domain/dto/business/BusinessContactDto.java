package com.creatix.domain.dto.business;

import com.creatix.domain.enums.CommunicationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@ApiModel
@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class BusinessContactDto {

    @ApiModelProperty("Contact entry id")
    private Long id;

    @ApiModelProperty(value = "Communication type")
    private CommunicationType communicationType;

    @ApiModelProperty(value = "Value for contact phone/Number")
    @Size(max = 25)
    private String communicationValue;

    @ApiModelProperty("Contact street")
    @Size(max = 255)
    private String street;

    @ApiModelProperty("House number")
    @Size(max = 10)
    private String houseNumber;

    @ApiModelProperty("Zip code")
    @Size(max = 10)
    private String zipCode;

    @ApiModelProperty("city")
    @Size(max = 50)
    private String city;

    @Size(max = 50)
    @ApiModelProperty("State")
    private String state;
}
