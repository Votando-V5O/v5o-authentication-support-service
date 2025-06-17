package io.github.votandov5o.v5oauthenticationsupportservice.configuration;

import io.github.votandov5o.v5oauthenticationsupportservice.component.ServiceProviderMetadataResolver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@Slf4j
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   RelyingPartyRegistrationRepository identityProviders,
                                                   ServiceProviderMetadataResolver metadataResolver) throws Exception {
        Saml2MetadataFilter metadataFilter = new Saml2MetadataFilter(identityProviders, metadataResolver);

        http.addFilterBefore(metadataFilter, Saml2WebSsoAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/saml2/service-provider-metadata/**", "/saml2/metadata")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .saml2Login(saml -> saml.relyingPartyRegistrationRepository(identityProviders))
//                .saml2Metadata(Customizer.withDefaults())
                .saml2Logout(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    @SneakyThrows
    public RelyingPartyRegistrationRepository identityProviders(List<RelyingPartyRegistration> registrations) {
        return new InMemoryRelyingPartyRegistrationRepository(registrations);
    }

}
