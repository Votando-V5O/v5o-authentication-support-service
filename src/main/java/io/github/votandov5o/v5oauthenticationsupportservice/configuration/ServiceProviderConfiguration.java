package io.github.votandov5o.v5oauthenticationsupportservice.configuration;

import io.github.votandov5o.v5oauthenticationsupportservice.dto.ContactDTO;
import io.github.votandov5o.v5oauthenticationsupportservice.dto.OrganizationDTO;
import io.github.votandov5o.v5oauthenticationsupportservice.properties.ServiceProviderConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ServiceProviderConfiguration {

    @Bean
    public OrganizationDTO spOrganization(ServiceProviderConfigurationProperties configurationProperties) {
        return OrganizationDTO.builder()
                .name(configurationProperties.getOrganization().getName())
                .displayName(configurationProperties.getOrganization().getDisplayName())
                .url(configurationProperties.getOrganization().getUrl())
                .build();
    }

    @Bean
    public ContactDTO spContact(ServiceProviderConfigurationProperties configurationProperties) {
        return ContactDTO.builder()
                .name(configurationProperties.getContact().getName())
                .email(configurationProperties.getContact().getEmail())
                .phoneNumber(configurationProperties.getContact().getPhoneNumber())
                .fiscalCode(configurationProperties.getContact().getFiscalCode())
                .build();
    }
}
