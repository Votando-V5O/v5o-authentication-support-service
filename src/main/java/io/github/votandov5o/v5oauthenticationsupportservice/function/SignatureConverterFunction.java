package io.github.votandov5o.v5oauthenticationsupportservice.function;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureBuilder;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.function.Function;

@Component
@Slf4j
public class SignatureConverterFunction implements Function<RelyingPartyRegistration, Signature> {
    @Override
    @SneakyThrows
    public Signature apply(RelyingPartyRegistration relyingPartyRegistration) {
        // Main Object
        Signature signature = new SignatureBuilder().buildObject();

        // Configure Algorithm
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);

        // Configure Canonicalization
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

        // Configure Credentials
        X509Certificate publicKey = relyingPartyRegistration.getSigningX509Credentials()
                .stream()
                .findFirst()
                .orElseThrow()
                .getCertificate();
        PrivateKey privateKey = relyingPartyRegistration.getSigningX509Credentials()
                .stream()
                .findFirst()
                .orElseThrow()
                .getPrivateKey();
        BasicX509Credential credential = new BasicX509Credential(publicKey, privateKey);
        credential.setUsageType(UsageType.SIGNING);
        signature.setSigningCredential(credential);

        // Configure KeyInfo
        X509KeyInfoGeneratorFactory x509KeyInfoGeneratorFactory = new X509KeyInfoGeneratorFactory();
        x509KeyInfoGeneratorFactory.setEmitEntityCertificate(true);
        signature.setKeyInfo(x509KeyInfoGeneratorFactory.newInstance().generate(credential));

        return signature;
    }
}
