package com.creatix.configuration;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class ApplicationPropertiesTest {
    @Test
    public void buildBackendUrl() throws Exception {
        ApplicationProperties props = new ApplicationProperties();
        props.setBackendUrl(new URL("http://52.33.158.234:8080/aptapp"));
        props.setFrontendUrl(new URL("http://www.aptapp.com/#/"));

        URL linkUrl = props.buildBackendUrl("api/notifications/3346/photos/3346-1-image.jpg");
        assertEquals("http://52.33.158.234:8080/aptapp/api/notifications/3346/photos/3346-1-image.jpg", linkUrl.toString());
        linkUrl = props.buildFrontendUrl("new-user/78945786");
        assertEquals("http://www.aptapp.com/#/new-user/78945786", linkUrl.toString());
    }
}