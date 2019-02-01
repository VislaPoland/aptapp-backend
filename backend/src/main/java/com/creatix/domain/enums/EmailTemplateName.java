package com.creatix.domain.enums;

public enum EmailTemplateName {
    ACTIVATION_ADMINISTRATOR("activation-administrator"),
    ACTIVATION_EMPLOYEE("activation-employee"),
    ACTIVATION_PROPERTY_OWNER("activation-property-owner"),
    ACTIVATION_RESET("activation-reset"),
    ACTIVATION_RESET_WEB("activation-reset-web"),
    ACTIVATION_TENANT("activation-tenant"),
    ESCALATED_NOTIFICATION_ONE("escalated-notification-for-more-tenants"),
    ESCALATED_NOTIFICATION_MORE("escalated-notification-for-one-tenant"),
    EXCEPTION_NOTIFICATION("exception-notification"),
    RESET_PASSWORD("reset-password");

    private String value;

    EmailTemplateName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


