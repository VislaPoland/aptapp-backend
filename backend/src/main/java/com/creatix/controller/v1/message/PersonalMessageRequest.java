package com.creatix.controller.v1.message;

import lombok.Data;

/**
 * Created by kvimbi on 29/05/2017.
 */
@Data
public class PersonalMessageRequest {

    private PersonalMessageRequestType personalMessageRequestType;
    private Long recipientId;
    private String title;
    private String content;

}
