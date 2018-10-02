package com.creatix.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="mailto:martin@thinkcreatix.com.com">martin dupal</a>
 */
@ConfigurationProperties(prefix = "bitly")
public class BitlyProperties {

    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
