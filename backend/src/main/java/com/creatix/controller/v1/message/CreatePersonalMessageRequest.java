package com.creatix.controller.v1.message;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by kvimbi on 29/05/2017.
 */
@Data
@ApiModel
public class CreatePersonalMessageRequest {

    @NotNull
    private PersonalMessageRequestType personalMessageRequestType;

    @NotNull
    @Min(0)
    private Long recipientId;

    @NotEmpty
    @Size(max = 255)
    private String title;

    @NotEmpty
    @Size(max = 1024)
    private String content;

}
