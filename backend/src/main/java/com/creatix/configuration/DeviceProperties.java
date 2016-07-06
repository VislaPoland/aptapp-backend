package com.creatix.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "device")
public class DeviceProperties {

    private String udidHeader;
    private String platformHeader;

    public String getUdidHeader() {
        return udidHeader;
    }

    public void setUdidHeader(String udidHeader) {
        this.udidHeader = udidHeader;
    }

    public String getPlatformHeader() {
        return platformHeader;
    }

    public void setPlatformHeader(String platformHeader) {
        this.platformHeader = platformHeader;
    }

}
