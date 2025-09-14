package com.dabom.member.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

import java.beans.ConstructorProperties;

@Getter
@Configuration
public class CookieProperties {
    @Value("${app.cookie.isSecure}")
    private boolean isSecure;
    @Value("${app.cookie.sameSite}")
    private String sameSite;
    @Value("${app.cookie.domain}")
    private String domain;
}
