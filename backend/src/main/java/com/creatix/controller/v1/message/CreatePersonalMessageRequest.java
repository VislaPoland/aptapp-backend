package com.creatix.controller.v1.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by Tomas Michalek on 29/05/2017.
 */
@Data
@ApiModel
public class CreatePersonalMessageRequest {

    @NotNull
    private PersonalMessageRequestType personalMessageRequestType;

    @ApiParam(name = "recipients", value = "List of recipient account id")
    private List<Long> recipients;

    @ApiParam(name = "property id", value = "Property id of the recipient")
    private Long propertyId;

    @Size(max = 255)
    private String title;

    @NotEmpty
    @Size(max = 1024)
    private String content;

}
