package io.github.votandov5o.v5oauthenticationsupportservice.configuration;

import io.github.votandov5o.v5oauthenticationsupportservice.dto.OrganizationDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.metadata.Saml2MetadataResolver;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.authentication.OpenSaml4AuthenticationRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2AuthenticationRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.List;

@Configuration
@Slf4j
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   RelyingPartyRegistrationRepository identityProviders,
                                                   Saml2MetadataResolver serviceProviderMetadataResolver,
                                                   AuthenticationSuccessHandler postSuccessAuthenticationHandler,
                                                   Converter<OpenSaml4AuthenticationProvider.ResponseToken, Saml2Authentication> spidSamlConverter) throws Exception {
        Saml2MetadataFilter metadataFilter = new Saml2MetadataFilter(identityProviders, serviceProviderMetadataResolver);
        OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
        authenticationProvider.setResponseAuthenticationConverter(spidSamlConverter);
        http.addFilterBefore(metadataFilter, Saml2WebSsoAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/saml2/service-provider-metadata/**",
                                "/saml2/metadata")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .saml2Login(saml -> saml.relyingPartyRegistrationRepository(identityProviders)
                        .authenticationManager(new ProviderManager(authenticationProvider))
                        .successHandler(postSuccessAuthenticationHandler)
                )
                .saml2Logout(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    @SneakyThrows
    public RelyingPartyRegistrationRepository identityProviders(List<RelyingPartyRegistration> registrations) {
        return new InMemoryRelyingPartyRegistrationRepository(registrations);
    }

    @Bean
    Saml2AuthenticationRequestResolver authenticationRequestResolver(RelyingPartyRegistrationRepository identityProviders,
                                                                     OrganizationDTO spOrganization) {
        RelyingPartyRegistrationResolver registrationResolver = new DefaultRelyingPartyRegistrationResolver(identityProviders);
        OpenSaml4AuthenticationRequestResolver authenticationRequestResolver = new OpenSaml4AuthenticationRequestResolver(registrationResolver);
        authenticationRequestResolver.setAuthnRequestCustomizer(context -> {
            var authnRequest = context.getAuthnRequest();
            authnRequest.setForceAuthn(true);
            authnRequest.setIsPassive((Boolean) null);

            //NameIdPolicy
            NameIDPolicy nameIDPolicy = new NameIDPolicyBuilder().buildObject();
            nameIDPolicy.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
            authnRequest.setNameIDPolicy(nameIDPolicy);

            //Issuer
            authnRequest.getIssuer()
                    .setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:entity");
            authnRequest.getIssuer()
                    .setNameQualifier(spOrganization.getUrl());

            // AttributeConsumingServiceIndex
            authnRequest.setAttributeConsumingServiceIndex(0);

            // RequestedAuthnContext
            RequestedAuthnContext requestedAuthnContext = new RequestedAuthnContextBuilder().buildObject();
            requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.MINIMUM);

            // AuthnContextClassRef
            AuthnContextClassRef authnContextClassRef = new AuthnContextClassRefBuilder().buildObject();
            authnContextClassRef.setURI("https://www.spid.gov.it/SpidL1");

            requestedAuthnContext.getAuthnContextClassRefs()
                    .add(authnContextClassRef);
            authnRequest.setRequestedAuthnContext(requestedAuthnContext);
        });
        return authenticationRequestResolver;
    }

    @Bean
    public Converter<OpenSaml4AuthenticationProvider.ResponseToken, Saml2Authentication> spidSamlConverter() {
        Converter<OpenSaml4AuthenticationProvider.ResponseToken, Saml2Authentication> delegate = OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter();
        return token -> {
            Saml2Authentication authentication = delegate.convert(token);
            assert authentication != null;
            Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
            return new Saml2Authentication(principal,
                    authentication.getSaml2Response(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));
        };
    }
}
