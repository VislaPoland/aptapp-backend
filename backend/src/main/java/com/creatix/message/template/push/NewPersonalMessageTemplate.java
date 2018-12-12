package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.PersonalMessage;
import com.creatix.domain.enums.PushNotificationTemplateName;

/**
 * Created by Tomas Michalek on 26/05/2017.
 */
public class NewPersonalMessageTemplate extends PushMessageTemplate {


    private final PersonalMessage personalMessage;

    public NewPersonalMessageTemplate(PersonalMessage personalMessage) {
        this.personalMessage = personalMessage;
    }


    @Override
    public String getTemplateName() {
        return PushNotificationTemplateName.NEW_PERSONAL_MESSAGE.getValue();
    }

    public PersonalMessage getMessage() {
        return this.personalMessage;
    }

}
