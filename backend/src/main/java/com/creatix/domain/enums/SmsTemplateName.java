package com.creatix.domain.enums;

public enum SmsTemplateName {
    ACTIVATION_ACCOUNT("activation-account"),
    ACTIVATION_ACCOUNT_WEB("activation-account-web"),
    ESCALATED_NOTIFICATION_ONE("escalated-notification-for-more-tenants"),
    ESCALATED_NOTIFICATION_MORE("escalated-notification-for-one-tenant"),
    NEIGHBOR_NOTIFICATION("neighbor-notification"),
    PERSONAL_MESSAGE("personal-message-to-tenant"),
    RSVP_REMINDER("rsvp-reminder");

    private String value;

    SmsTemplateName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
