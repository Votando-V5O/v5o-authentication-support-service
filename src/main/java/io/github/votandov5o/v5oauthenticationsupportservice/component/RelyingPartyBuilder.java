package io.github.votandov5o.v5oauthenticationsupportservice.component;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.opensaml.core.config.InitializationService;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

@Component
@RequiredArgsConstructor
@Slf4j
public class RelyingPartyBuilder {

    @SneakyThrows
    public RelyingPartyRegistration buildRelyingParty(String registrationId, String metadataUrl) {
        InitializationService.initialize();
        log.info("Building relying party registration for ID: {}", registrationId);
        return RelyingPartyRegistrations.fromMetadataLocation(metadataUrl)
                .registrationId(registrationId)
                .entityId("{baseUrl}/{registrationId}")
                .signingX509Credentials(c -> c.add(loadSigningCredentialFromPEM()))
                .build();
    }


    @SneakyThrows
    private Saml2X509Credential loadSigningCredentialFromPEM() {
        String certPath = "keystore/cert.pem";
        String keyPath = "keystore/key.pem";

        // Load certificate
        X509Certificate certificate;
        try (InputStream certInput = getClass().getClassLoader().getResourceAsStream(certPath)) {
            if (certInput == null) {
                throw new IllegalArgumentException("Certificate not found at path: " + certPath);
            }
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            certificate = (X509Certificate) factory.generateCertificate(certInput);
        }

        // Load private key
        PrivateKey privateKey;
        try (InputStream keyInput = getClass().getClassLoader().getResourceAsStream(keyPath)) {
            if (keyInput == null) {
                throw new IllegalArgumentException("Key not found at path: " + certPath);
            }
            PemReader reader = new PemReader(new InputStreamReader(keyInput));
            PemObject pemObject = reader.readPemObject();
            byte[] keyBytes = pemObject.getContent();

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            privateKey = kf.generatePrivate(keySpec);
        }

        return new Saml2X509Credential(privateKey, certificate, Saml2X509Credential.Saml2X509CredentialType.SIGNING);
    }
}
