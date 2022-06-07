package io.github.dsibilio.follina.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("follina")
public class FollinaProperties {
    private String payload;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
