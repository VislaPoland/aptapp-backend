package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.PersonalMessage;

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
        return "new-personal-message";
    }

    public PersonalMessage getMessage() {
        return this.personalMessage;
    }

}
