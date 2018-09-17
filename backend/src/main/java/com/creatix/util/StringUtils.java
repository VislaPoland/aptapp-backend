package com.creatix.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by kvimbi on 02/06/2017.
 */
public class StringUtils {

    public static List<Long> splitToLong(String str, String regex) {
        return Arrays.stream(str.split(regex))
                .map(e -> {
                    try {
                        return Long.valueOf(e);
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    // TODO: Delete this mapper from web and mobile repository. Use only this mapper. Maybe do it with enum.
    public static String translateTileFromEnumString(String titleEnumString) {
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
