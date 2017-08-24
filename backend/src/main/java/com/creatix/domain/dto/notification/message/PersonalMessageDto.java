package com.creatix.domain.dto.notification.message;

import com.creatix.domain.enums.message.PersonalMessageDeleteStatus;
import com.creatix.domain.enums.message.PersonalMessageStatusType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by Tomas Michalek on 26/05/2017.
 */
@Data
@ApiModel("Personal message")
@EqualsAndHashCode(of = "id")
public class PersonalMessageDto {

    @ApiModelProperty
    private Long id;

    @ApiModelProperty
    private PersonalMessageAccountDto fromAccount;

    @ApiModelProperty
    private PersonalMessageAccountDto toAccount;

    @NotEmpty
    @Size(max = 255)
    @ApiModelProperty
    private String title;

    @NotEmpty
    @Size(max = 1024)
    @ApiModelProperty
    private String content;

    @ApiModelProperty
    private PersonalMessageStatusType messageStatus;

    @ApiModelProperty
    private PersonalMessageDeleteStatus deleteStatus;

}
