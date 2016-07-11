package com.creatix.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.MalformedURLException;
import java.net.URL;

@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private URL baseUrl;

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public URL buildAbsoluteUrl(String relativePath) throws MalformedURLException {
        return new URL(baseUrl, relativePath);
    }
}
