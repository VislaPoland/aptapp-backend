package com.creatix.domain.enums;

public enum NotificationStatus {
    Pending,
    Resolved,
    /**
     *  Resolved === Closed, we keep Closed only for backward compatibility reasons.
     *  @see NotificationStatus#Resolved
     */
    @Deprecated
    Closed,

    /**
     * Use this status instead of deleting record
     */
    Deleted
}
