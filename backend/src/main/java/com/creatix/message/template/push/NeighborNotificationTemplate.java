package com.creatix.message.template.push;

import com.creatix.domain.entity.store.notification.NeighborhoodNotification;

public class NeighborNotificationTemplate extends PushMessageTemplate {

    protected final NeighborhoodNotification notification;

    public NeighborNotificationTemplate(NeighborhoodNotification notification) {
        this.notification = notification;
    }

    @Override
    public String getTemplateName() {
        return "neighbor-notification";
    }

    public String getMessage() {
        return translateTileFromEnumString(notification.getTitle());
    }

    public String getTimestamp() {
        return formatTimestamp(notification.getCreatedAt(), notification.getTargetApartment().getProperty().getZoneId());
    }

    private String translateTileFromEnumString(String titleEnumString) {
        switch ( titleEnumString ) {
            case "beMindful":
                return "Please Be Mindful of Neighbors";
            case "shutDownTv":
                return "Please Turn Down Tv/Music";
            case "quietFootsteps":
                return "Please Quiet Footsteps";
            case "quietPet":
                return "Please Quiet Pet";
            case "quietGuests":
                return "Please Quiet Guests";
            case "stopSmoking":
                return "This is a Non-smoking Building";
            case "takeOutYourGarbage":
                return "Please Take Out Your Garbage";
            case "greatNeighbor":
                return "You are a great neighbor. Thank you";
            default:
                return titleEnumString;
        }
    }
}
