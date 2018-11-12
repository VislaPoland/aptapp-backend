package com.creatix.message.template.push;

import com.creatix.domain.enums.AccountRole;
import com.creatix.message.template.MessageTemplate;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public abstract class PushMessageTemplate implements MessageTemplate {

    private DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("MM.dd.yyyy HH:mm a");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM.dd.yyyy");


    String formatTimestamp(OffsetDateTime tm, @NotNull ZoneId zoneId) {
        Objects.requireNonNull(zoneId, "Zone Id is null");

        if ( tm == null ) {
            return "";
        }

        final ZonedDateTime zonedTm = ZonedDateTime.ofInstant(tm.toInstant(), zoneId);
        return timestampFormatter.format(zonedTm);
    }

    String formatTime(OffsetDateTime tm, @NotNull ZoneId zoneId) {
        Objects.requireNonNull(zoneId, "Zone Id is null");

        if ( tm == null ) {
            return "";
        }

        final ZonedDateTime zonedTm = ZonedDateTime.ofInstant(tm.toInstant(), zoneId);
        return timeFormatter.format(zonedTm);
    }

    String formatDate(OffsetDateTime tm, @NotNull ZoneId zoneId) {
        Objects.requireNonNull(zoneId, "Zone Id is null");

        if ( tm == null ) {
            return "";
        }

        final ZonedDateTime zonedTm = ZonedDateTime.ofInstant(tm.toInstant(), zoneId);
        return dateFormatter.format(zonedTm);
    }

    String translateRoleNameFromEnum(AccountRole role) {
        switch ( role ) {
            case Tenant:
            case SubTenant:
                return "Neighbor";
            case AssistantPropertyManager:
                return "Property manager assistant";
            case PropertyManager:
                return "Property manager";
            default:
                return "";
        }
    }
}
