package com.creatix.domain.dto.notification.message;

import com.creatix.domain.enums.message.PersonalMessageDeleteStatus;
import com.creatix.domain.enums.message.PersonalMessageStatusType;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by Tomas Michalek on 26/05/2017.
 */
@Data
@ApiModel("Personal message")
public class PersonalMessageDto {

    private Long id;
    private PersonalMessageAccountDto fromAccount;
    private PersonalMessageAccountDto toAccount;
    private String title;
    private String content;
    private PersonalMessageStatusType messageStatus;
    private PersonalMessageDeleteStatus deleteStatus;

}
