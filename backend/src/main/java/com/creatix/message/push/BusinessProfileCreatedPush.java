package com.creatix.message.push;

/**
 * Created by Tomas Michalek on 18/04/2017.
 */
public class BusinessProfileCreatedPush extends GenericPushNotification {

    public static final String BUSINESS_PROFILE_ID_ATTRIBUTE_KEY = "businessProfileId";

    public BusinessProfileCreatedPush(long businessProfileId) {
        super();

        if (this.getAttributes().containsKey(BUSINESS_PROFILE_ID_ATTRIBUTE_KEY)) {
            getAttributes().put(
                    BUSINESS_PROFILE_ID_ATTRIBUTE_KEY,
                    String.valueOf(businessProfileId)
            );
        }
    }
}
