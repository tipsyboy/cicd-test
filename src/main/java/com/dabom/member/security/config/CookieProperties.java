package com.dabom.member.security.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "app.cookie")
public class CookieProperties {
    private boolean isSecure;
    private String sameSite;
    private String domain;
}
