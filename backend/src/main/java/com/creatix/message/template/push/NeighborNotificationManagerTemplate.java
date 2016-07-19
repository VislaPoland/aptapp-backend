package com.creatix.message.template.push;


import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.notification.NeighborhoodNotification;

public class NeighborNotificationManagerTemplate extends NeighborNotificationTemplate {

    public NeighborNotificationManagerTemplate(NeighborhoodNotification notification) {
        super(notification);
    }

    public String getUnitNumberSender() {
        if ( notification.getAuthor() instanceof Tenant ) {
            final Tenant author = (Tenant) notification.getAuthor();
            if ( author.getApartment() != null ) {
                return author.getApartment().getUnitNumber();
            }
        }

        return "n/a";
    }

    public String getUnitNumberRecipient() {
        if ( notification.getTargetApartment() != null ) {
            return notification.getTargetApartment().getUnitNumber();
        }

        return "n/a";
    }

    @Override
    public String getTemplateName() {
        return "neighbor-notification-manager";
    }
}
