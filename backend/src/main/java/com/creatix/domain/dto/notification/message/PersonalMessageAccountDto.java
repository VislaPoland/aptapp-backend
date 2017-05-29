package com.creatix.domain.dto.notification.message;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by kvimbi on 29/05/2017.
 */
@Data
@ApiModel("From/To Account of personal message")
public class PersonalMessageAccountDto {

    private Long userId;
    private String firstName;
    private String lastName;
    private String companyName;
    private String primaryPhone;
    private String primaryEmail;

}
