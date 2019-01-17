package com.creatix.domain.enums;

public enum NotificationHistoryStatus {
    /**
     *  Combination of notification and reservation statuses
     *  @see NotificationStatus,ReservationStatus
     */
    Cancelled,
    Closed,
    Confirmed,
    Deleted,
    Pending,
    Rescheduled,
    Rejected,
    Resolved,
    /**
     *
     * used as global state to group notification status when receiver of notification has possibility to response
     *
     * e.g. {@link com.creatix.domain.entity.store.notification.MaintenanceNotification}
     * has
     * {@link NotificationHistoryStatus#Rejected}
     * {@link NotificationHistoryStatus#Rescheduled}
     * {@link NotificationHistoryStatus#Confirmed}
     *
     * @see com.creatix.domain.entity.store.notification.reporting.NotificationStatusFlow
     */
    Responded
}
