package com.creatix.message.template.push;

import com.creatix.domain.entity.store.Property;
import com.creatix.message.template.MessageTemplate;

import javax.validation.constraints.NotNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public abstract class PushMessageTemplate implements MessageTemplate {

    private DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("MM.dd.yyyy");

    String formatTimestamp(Date tm, @NotNull ZoneId zoneId) {
        Objects.requireNonNull(zoneId, "Zone Id is null");

        if ( tm == null ) {
            return "";
        }

        final ZonedDateTime zonedTm = ZonedDateTime.ofInstant(tm.toInstant(), zoneId);
        return timestampFormatter.format(zonedTm);
    }

}
