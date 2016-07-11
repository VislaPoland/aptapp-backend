package com.creatix.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.MalformedURLException;
import java.net.URL;

@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public URL buildAbsoluteUrl(String relativePath) throws MalformedURLException {
        final URL baseUrl = new URL(getBaseUrl());
        return new URL(baseUrl, relativePath);
    }
}
