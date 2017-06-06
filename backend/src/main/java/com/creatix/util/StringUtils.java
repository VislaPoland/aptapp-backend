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

}
