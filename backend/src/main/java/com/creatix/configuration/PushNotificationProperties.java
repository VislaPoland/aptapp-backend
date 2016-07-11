package com.creatix.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pushnotifications")
public class PushNotificationProperties {

    private boolean appleSandbox;
    private String appleCertificatePath;
    private String appleCertificatePassword;
    private String googleCloudMessagingKey;

    public boolean isAppleSandbox() {
        return appleSandbox;
    }

    public void setAppleSandbox(boolean appleSandbox) {
        this.appleSandbox = appleSandbox;
    }

    public String getAppleCertificatePath() {
        return appleCertificatePath;
    }

    public void setAppleCertificatePath(String appleCertificatePath) {
        this.appleCertificatePath = appleCertificatePath;
    }

    public String getAppleCertificatePassword() {
        return appleCertificatePassword;
    }

    public void setAppleCertificatePassword(String appleCertificatePassword) {
        this.appleCertificatePassword = appleCertificatePassword;
    }

    public String getGoogleCloudMessagingKey() {
        return googleCloudMessagingKey;
    }

    public void setGoogleCloudMessagingKey(String googleCloudMessagingKey) {
        this.googleCloudMessagingKey = googleCloudMessagingKey;
    }

}
