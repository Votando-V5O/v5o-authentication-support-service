package io.github.votandov5o.v5oauthenticationsupportservice.configuration;

import io.github.votandov5o.v5oauthenticationsupportservice.component.RelyingPartyBuilder;
import io.github.votandov5o.v5oauthenticationsupportservice.utility.SSLDisable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;

@Configuration
@Slf4j
public class IdentityProviderConfiguration {
    @Bean
    public RelyingPartyRegistration register(RelyingPartyBuilder builder) {
        return builder.buildRelyingParty("register",
                "https://spid.register.it/login/metadata");
    }

    @Bean
    public RelyingPartyRegistration teamsystem(RelyingPartyBuilder builder) {
        return builder.buildRelyingParty("teamsystem",
                "https://spid.teamsystem.com/idp");
    }

    //    @Bean
    public RelyingPartyRegistration sielte(RelyingPartyBuilder builder) {
        return builder.buildRelyingParty("sielte",
                "https://identity.sieltecloud.it/simplesaml/metadata.xml");
    }

    @Bean
    public RelyingPartyRegistration infocert(RelyingPartyBuilder builder) {
        return builder.buildRelyingParty("infocert",
                "https://identity.infocert.it/metadata/metadata.xml");
    }

    @Bean
    public RelyingPartyRegistration intesigroup(RelyingPartyBuilder builder) {
        return builder.buildRelyingParty("intesigroup",
                "https://spid.intesigroup.com/metadata/metadata.xml");
    }

    @Bean
    public RelyingPartyRegistration titrusttechnologies(RelyingPartyBuilder builder) {
        return builder.buildRelyingParty("titrusttechnologies",
                "https://login.id.tim.it/spid-services/MetadataBrowser/idp");
    }

    @Bean
    @Profile("test")
    public RelyingPartyRegistration demo(RelyingPartyBuilder builder) {
        return builder.buildRelyingParty("demo",
                "https://demo.spid.gov.it/metadata.xml");
    }

    @Bean
    @Profile("local")
    public RelyingPartyRegistration local(RelyingPartyBuilder builder) {
        SSLDisable.execute();
        return builder.buildRelyingParty("local",
                "https://localhost:8443/demo/metadata.xml");
    }

    @Bean
    @Profile("local")
    public RelyingPartyRegistration localVerify(RelyingPartyBuilder builder) {
        SSLDisable.execute();
        return builder.buildRelyingParty("localVerify",
                "https://localhost:8443/metadata.xml");
    }

    @Bean
    public RelyingPartyRegistration lepida(RelyingPartyBuilder builder) {
        return builder.buildRelyingParty("lepida",
                "https://id.lepida.it/idp/shibboleth");
    }

    @Bean
    public RelyingPartyRegistration posteitaliane(RelyingPartyBuilder builder) {
        return builder.buildRelyingParty("posteitaliane",
                "https://posteid.poste.it/jod-fs/metadata/metadata.xml");
    }

    @Bean
    public RelyingPartyRegistration infocamere(RelyingPartyBuilder builder) {
        return builder.buildRelyingParty("infocamere",
                "https://loginspid.infocamere.it/metadata");
    }

    @Bean
    public RelyingPartyRegistration etnahitech(RelyingPartyBuilder builder) {
        return builder.buildRelyingParty("etnahitech",
                "https://id.eht.eu/metadata.xml");
    }

    @Bean
    public RelyingPartyRegistration arubapec(RelyingPartyBuilder builder) {
        return builder.buildRelyingParty("arubapec",
                "https://loginspid.aruba.it/metadata");
    }

    @Bean
    public RelyingPartyRegistration namirial(RelyingPartyBuilder builder) {
        return builder.buildRelyingParty("namirial",
                "https://idp.namirialtsp.com/idp/metadata");
    }
}
