package com.creatix.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "device")
public class DeviceProperties {

    private String udidHeader;
    private String platformHeader;
    private String sessionKeyDevice;
    private String sessionKeyPlatform;

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

    public String getSessionKeyDevice() {
        return sessionKeyDevice;
    }

    public void setSessionKeyDevice(String sessionKeyDevice) {
        this.sessionKeyDevice = sessionKeyDevice;
    }

    public String getSessionKeyPlatform() {
        return sessionKeyPlatform;
    }

    public void setSessionKeyPlatform(String sessionKeyPlatform) {
        this.sessionKeyPlatform = sessionKeyPlatform;
    }

}
