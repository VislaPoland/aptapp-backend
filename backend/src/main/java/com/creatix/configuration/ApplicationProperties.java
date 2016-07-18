package com.creatix.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;

@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private URL backendUrl;
    private URL frontendUrl;
    private URL adminUrl;

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

    public URL getAdminUrl() {
        return adminUrl;
    }

    public void setAdminUrl(URL adminUrl) {
        this.adminUrl = adminUrl;
    }

    @NotNull
    public URL buildBackendUrl(String relativePath) throws MalformedURLException {
        return concat(backendUrl, relativePath);
    }

    @NotNull
    public URL buildFrontendUrl(String relativePath) throws MalformedURLException {
        return concat(frontendUrl, relativePath);
    }

    @NotNull
    public URL buildAdminUrl(String relativePath) throws MalformedURLException {
        return concat(frontendUrl, relativePath);
    }

    private URL concat(URL rootUrl, String relative) throws MalformedURLException {
        final String root = rootUrl.toString();
        final String relativeSanitized = StringUtils.trimToEmpty(relative);

        if ( !(StringUtils.endsWith(root, "/")) && !(StringUtils.startsWith(relativeSanitized, "/")) ) {
            return new URL(root + "/" + relativeSanitized);
        }
        else {
            return new URL(root + relativeSanitized);
        }
    }
}
