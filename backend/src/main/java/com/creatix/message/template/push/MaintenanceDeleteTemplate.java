package com.creatix.message.template.push;

import com.creatix.domain.entity.store.MaintenanceReservation;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.SubTenant;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.notification.MaintenanceNotification;


public class MaintenanceDeleteTemplate extends PushMessageTemplate {

    private final MaintenanceNotification notification;
    private final Account currentAccount;


    public MaintenanceDeleteTemplate(MaintenanceNotification notification, Account currentAccount) {
        this.notification = notification;
        this.currentAccount = currentAccount;
    }

    public String getUnitNumber() {

        switch (currentAccount.getRole()) {
            case Tenant: return ((Tenant)currentAccount).getApartment().getUnitNumber();
            case SubTenant: return ((SubTenant)currentAccount).getApartment().getUnitNumber();
            default: throw new IllegalArgumentException("Invalid push notification for maintenance deleted.");
        }
    }

    public String getNotificationTitle() {
        return notification.getTitle();
    }

    @Override
    public String getTemplateName() {
        return "maintenance-delete";
    }
}
