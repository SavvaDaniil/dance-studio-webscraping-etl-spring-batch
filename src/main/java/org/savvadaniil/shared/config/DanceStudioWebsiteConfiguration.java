package org.savvadaniil.shared.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DanceStudioWebsiteConfiguration {

    @Value("${BASE_URL}")
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }
}
