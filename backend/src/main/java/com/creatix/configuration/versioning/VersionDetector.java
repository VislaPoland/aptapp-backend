package com.creatix.configuration.versioning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tomas Sedlak on 14.8.2017.
 */
public class VersionDetector {

    private static final Logger logger = LoggerFactory.getLogger(VersionDetector.class);

    public static double NO_VERSIONING = -1.0;
    private static Pattern VERSION_PATTERN = Pattern.compile("^/api/v?([0-9_.]+)/.*$");

    public static double detect(@NotNull String url) {
        Objects.requireNonNull(url, "url is missing");

        double version = NO_VERSIONING;

        final Matcher matcher = VERSION_PATTERN.matcher(url);
        if ( matcher.find() ) {
            final String strVersion = matcher.group(1).replace("_", ".");
            try {
                version = Double.valueOf(strVersion);
            }
            catch ( NumberFormatException ex ) {
                logger.warn(String.format("Invalid version '%s' in URL %s", strVersion, url), ex);
            }
        }

        return version;
    }

}
