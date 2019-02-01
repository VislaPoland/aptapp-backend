package com.creatix.domain.enums;

public enum PushNotificationTemplateName {
    BUSINESS_PROFILE_CREATED("business-profile-created"),
    DISCOUNT_COUPON_CREATED("discount-coupon-created"),
    COMMUNITY_ITEM_UPDATE("community-item-update-subscriber"),
    COMMUNITY_ITEM_COMMENT("new-community-item-comment"),
    COMMUNITY_ITEM_COMMENT_REPLY("new-community-item-comment-reply"),
    ESCALATED_MANAGER_NOTIFICATION("escalated-manager-notification"),
    ESCALATED_MANAGER_NOTIFICATION_MORE("escalated-manager-notification-from-more-tenants"),
    ESCALATED_NEIGHBOR_NOTIFICATION("escalated-neighbor-notification"),
    ESCALATED_NEIGHBOR_NOTIFICATION_NO_ISSUE("escalated-neighbor-notification-no-issue-found"),
    ESCALATED_NEIGHBOR_NOTIFICATION_RESOLVED("escalated-neighbor-notification-resolved"),
    EVENT_NOTIFICATION("event-notification"),
    EVENT_NOTIFICATION_ADJUST("event-notification-adjust"),
    EVENT_NOTIFICATION_CANCEL("event-notification-cancel"),
    MAINTENANCE_COMPLETE("maintenance-complete"),
    MAINTENANCE_DELETE("maintenance-delete"),
    MAINTENANCE_NOTIFICATION_EMPLOYEE("maintenance-notification-by-employee"),
    MAINTENANCE_NOTIFICATION_TENANT("maintenance-notification-by-tenant"),
    MAINTENANCE_RESCHEDULE("maintenance-reschedule"),
    MAINTENANCE_RESCHEDULE_CONFIRM_WITH_UNIT("maintenance-reschedule-confirm-with-unit"),
    MAINTENANCE_RESCHEDULE_CONFIRM_WITHOUT_UNIT("maintenance-reschedule-confirm-without-unit"),
    MAINTENANCE_RESCHEDULE_REJECT_WITH_UNIT("maintenance-reschedule-reject-with-unit"),
    MAINTENANCE_RESCHEDULE_REJECT_WITHOUT_UNIT("maintenance-reschedule-reject-without-unit"),
    NEIGHBOR_NOTIFICATION("neighbor-notification"),
    NEIGHBOR_NOTIFICATION_NOT_ME("neighbor-notification-not-me"),
    NEIGHBOR_NOTIFICATION_RESOLVED("neighbor-notification-resolved"),
    NEW_PERSONAL_MESSAGE("new-personal-message"),
    RSVP_REMINDER("rsvp-reminder"),
    SECURITY_NOTIFICATION("security-notification"),
    SECURITY_NOTIFICATION_MANAGER_NO_ISSUE("security-notification-manager-no-issue"),
    SECURITY_NOTIFICATION_MANAGER_RESOLVED("security-notification-manager-resolved"),
    SECURITY_NOTIFICATION_NEIGHBOR_NO_ISSUE("security-notification-neighbor-no-issue"),
    SECURITY_NOTIFICATION_NEIGHBOR_RESOLVED("security-notification-neighbor-resolved");

    private String value;

    PushNotificationTemplateName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
