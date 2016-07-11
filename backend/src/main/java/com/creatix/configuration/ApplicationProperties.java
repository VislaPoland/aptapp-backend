package com.creatix.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.MalformedURLException;
import java.net.URL;

@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private URL backendUrl;
    private URL frontendUrl;

    public URL getBackendUrl() {
        return backendUrl;
    }

    public void setBackendUrl(URL backendUrl) {
        this.backendUrl = backendUrl;
    }

    public URL getFrontendUrl() {
        return frontendUrl;
    }

    public void setFrontendUrl(URL frontendUrl) {
        this.frontendUrl = frontendUrl;
    }

    public URL buildBackendUrl(String relativePath) throws MalformedURLException {
        return new URL(backendUrl, relativePath);
    }

    public URL buildFrontendUrl(String relativePath) throws MalformedURLException {
        return new URL(frontendUrl, relativePath);
    }
}
