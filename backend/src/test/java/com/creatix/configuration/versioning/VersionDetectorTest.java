package com.creatix.configuration.versioning;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Tomas Sedlak on 14.8.2017.
 */
public class VersionDetectorTest {

    private double previousImplementation(String requestURI) {
        if (! requestURI.matches("/api/v?[0-9_.]+.*") ) {
            return -1.0;
        }
        String[] split = requestURI.split("/");
        if (split.length > 1) {
            String versionNumber = split[2];
            try {
                return Double.valueOf(versionNumber.replace("_", ".").replace("v", ""));
            } catch (NumberFormatException e) {
                System.err.println(requestURI);
                e.printStackTrace();
            }
        }
        return 0.0;

    }

    @Test
    public void detect() throws Exception {
        assertEquals(VersionDetector.NO_VERSIONING, VersionDetector.detect("/"), 0.001);
        assertEquals(VersionDetector.NO_VERSIONING, VersionDetector.detect("/api/properties/19425"), 0.001);
        assertEquals(1.0, VersionDetector.detect("/api/v1/properties/19425"), 0.001);
        assertEquals(1.1, VersionDetector.detect("/api/v1.1/properties/19425"), 0.001);
        assertEquals(2.3, VersionDetector.detect("/api/v2_3/properties/19425"), 0.001);
        assertEquals(VersionDetector.NO_VERSIONING, VersionDetector.detect("/api/login"), 0.001);
    }

    @Test
    public void compatibility() throws Exception {
        assertEquals(previousImplementation("/"), VersionDetector.detect("/"), 0.001);
        assertEquals(previousImplementation("/api/properties/19425"), VersionDetector.detect("/api/properties/19425"), 0.001);
        assertEquals(previousImplementation("/api/v1/properties/19425"), VersionDetector.detect("/api/v1/properties/19425"), 0.001);
        assertEquals(previousImplementation("/api/v1.1/properties/19425"), VersionDetector.detect("/api/v1.1/properties/19425"), 0.001);
        assertEquals(previousImplementation("/api/v2_3/properties/19425"), VersionDetector.detect("/api/v2_3/properties/19425"), 0.001);
        assertEquals(previousImplementation("/api/login"), VersionDetector.detect("/api/login"), 0.001);
    }
}