package com.creatix.message.template.email;

import java.net.MalformedURLException;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.entity.store.account.PropertyManager;
import com.creatix.domain.entity.store.account.Tenant;

/**
 * @author <a href="mailto:martin@thinkcreatix.com.com">martin dupal</a>
 */
public class EscalatedManagerMessageForMoreTenantsTemplate implements EmailMessageTemplate {

    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(EscalatedManagerMessageTemplate.class);

    @Override
    public String getTemplateName() {
        return "escalated-notification-for-more-tenants";
    }

    @Nonnull
    private final Tenant offender;
    @Nonnull
    private final PropertyManager propertyManager;
    @Nonnull
    private final ApplicationProperties properties;
    @Nonnull
    private final List<String> notificationsMessages;

    public EscalatedManagerMessageForMoreTenantsTemplate(
            @Nonnull Tenant offender,
            @Nonnull PropertyManager propertyManager,
            @Nonnull ApplicationProperties properties, @Nonnull List<String> notificationsMessages) {
        this.offender = offender;
        this.propertyManager = propertyManager;
        this.properties = properties;
        this.notificationsMessages = notificationsMessages;
    }

    public String getApplicationUrl() {
        return properties.getAdminUrl().toString();
    }

    public String getLogoUrl() {
        try {
            return properties.buildBackendUrl("static/aptapp_logo.png").toString();
        } catch (MalformedURLException e) {
            LOGGER.error("Unable to load apt-app logo", e);
        }
        return null;
    }

    @Override
    public String getSubject() {
        return "Apt. App Neighbor Notification Alert: Resident receiving multiple messages.";
    }

    @Override
    public String getRecipient() {
        return propertyManager.getPrimaryEmail();
    }

    public String getOffenderUnit() {
        return offender.getApartment().getUnitNumber();
    }

    public String getNotifications() {
        return String.join("<br/>", notificationsMessages);
    }
}
