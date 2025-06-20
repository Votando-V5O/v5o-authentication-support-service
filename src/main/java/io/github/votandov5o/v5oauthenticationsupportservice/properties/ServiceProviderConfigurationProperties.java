package io.github.votandov5o.v5oauthenticationsupportservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "io.github.votandov5o.v5oauthenticationsupportservice.service-provider")
public class ServiceProviderConfigurationProperties {
    private SPOrganization organization;
    private SPContact contact;

    @Data
    public static class SPOrganization {
        private String name;
        private String displayName;
        private String url;
    }

    @Data
    public static class SPContact {
        private String name;
        private String email;
        private String phoneNumber;
        private String fiscalCode;
    }
}
