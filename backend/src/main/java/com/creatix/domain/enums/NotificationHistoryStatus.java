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
}
