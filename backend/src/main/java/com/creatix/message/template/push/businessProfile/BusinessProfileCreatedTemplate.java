package com.creatix.message.template.push.businessProfile;

import com.creatix.domain.entity.store.business.BusinessProfile;
import com.creatix.domain.enums.PushNotificationTemplateName;
import com.creatix.message.template.push.PushMessageTemplate;

/**
 * Created by Tomas Michalek on 18/04/2017.
 */
public class BusinessProfileCreatedTemplate extends PushMessageTemplate {

    private final BusinessProfile businessProfile;

    public BusinessProfileCreatedTemplate(BusinessProfile businessProfile) {
        this.businessProfile = businessProfile;
    }


    @Override
    public String getTemplateName() {
        return PushNotificationTemplateName.BUSINESS_PROFILE_CREATED.getValue();
    }
}
