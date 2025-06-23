package io.github.votandov5o.v5oauthenticationsupportservice.component;

import io.github.votandov5o.v5oauthenticationsupportservice.dto.ContactDTO;
import io.github.votandov5o.v5oauthenticationsupportservice.dto.OrganizationDTO;
import io.github.votandov5o.v5oauthenticationsupportservice.function.ContactPersonConverterFunction;
import io.github.votandov5o.v5oauthenticationsupportservice.function.OrganizationConverterFunction;
import io.github.votandov5o.v5oauthenticationsupportservice.function.SignatureConverterFunction;
import io.github.votandov5o.v5oauthenticationsupportservice.function.SingleLogoutServiceConverterFunction;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;
import org.opensaml.saml.saml2.metadata.ServiceName;
import org.opensaml.saml.saml2.metadata.impl.AttributeConsumingServiceBuilder;
import org.opensaml.saml.saml2.metadata.impl.NameIDFormatBuilder;
import org.opensaml.saml.saml2.metadata.impl.RequestedAttributeBuilder;
import org.opensaml.saml.saml2.metadata.impl.ServiceNameBuilder;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.Signer;
import org.springframework.security.saml2.provider.service.metadata.OpenSaml4MetadataResolver;
import org.springframework.security.saml2.provider.service.metadata.Saml2MetadataResolver;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.UUID;

import static org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport.getMarshallerFactory;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceProviderMetadataResolver implements Saml2MetadataResolver {

    OpenSaml4MetadataResolver resolver = new OpenSaml4MetadataResolver();
    private static final String ID = "_".concat(toHex(UUID.randomUUID().toString()));
    private final OrganizationDTO spOrganization;
    private final ContactDTO spContact;
    private final OrganizationConverterFunction organizationConverterFunction;
    private final SignatureConverterFunction signatureConverterFunction;
    private final SingleLogoutServiceConverterFunction singleLogoutServiceConverterFunction;
    private final ContactPersonConverterFunction contactPersonConverterFunction;
    private Signature signature;

    @Override
    public String resolve(RelyingPartyRegistration relyingPartyRegistration) {
        String baseUrl = relyingPartyRegistration.getEntityId().replace("/" + relyingPartyRegistration.getRegistrationId(), "");
        String openSamlResolved = resolver.resolve(relyingPartyRegistration);
        EntityDescriptor entityDescriptor = unmarshall(openSamlResolved);

        // Set ID
        entityDescriptor.setID(ID);

        // NameId Format
        NameIDFormat nameIDFormat = new NameIDFormatBuilder().buildObject();
        nameIDFormat.setURI("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");

        // Add AttributeConsumingService with RequestedAttribute
        ServiceName serviceName = new ServiceNameBuilder().buildObject();
        serviceName.setValue("Votando V5O");
        serviceName.setXMLLang("it");
        RequestedAttribute spidCode = new RequestedAttributeBuilder().buildObject();
        spidCode.setName("spidCode");
        spidCode.setIsRequired(Boolean.TRUE);
        RequestedAttribute fiscalNumber = new RequestedAttributeBuilder().buildObject();
        fiscalNumber.setName("fiscalNumber");
        fiscalNumber.setIsRequired(Boolean.TRUE);

        AttributeConsumingService attributeConsumingService = new AttributeConsumingServiceBuilder().buildObject();
        attributeConsumingService.setIndex(0);
        attributeConsumingService.getNames()
                .add(serviceName);
        attributeConsumingService.getRequestedAttributes()
                .add(spidCode);
        attributeConsumingService.getRequestedAttributes()
                .add(fiscalNumber);
        entityDescriptor.getSPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol")
                .getAssertionConsumerServices()
                .getFirst()
                .setIndex(0);
        entityDescriptor.getSPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol")
                .getAssertionConsumerServices()
                .getFirst()
                .setIsDefault(Boolean.TRUE);

        entityDescriptor.getSPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol")
                .setAuthnRequestsSigned(Boolean.TRUE);
        entityDescriptor.getSPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol")
                .setWantAssertionsSigned(Boolean.TRUE);
        entityDescriptor.getSPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol")
                .getAttributeConsumingServices()
                .add(attributeConsumingService);


        entityDescriptor.setOrganization(organizationConverterFunction.apply(spOrganization));
        entityDescriptor.getSPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol")
                .getNameIDFormats()
                .add(nameIDFormat);
        entityDescriptor.getSPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol")
                .getSingleLogoutServices()
                .add(singleLogoutServiceConverterFunction.apply(MessageFormat.format("{0}/logout/saml2/slo", baseUrl)));
        entityDescriptor.getContactPersons()
                .add(contactPersonConverterFunction.apply(spContact));
        this.signature = signatureConverterFunction.apply(relyingPartyRegistration);
        entityDescriptor.setSignature(this.signature);
        return marshall(entityDescriptor);
    }

    @SneakyThrows
    private String marshall(XMLObject object) {
        Marshaller marshaller = getMarshallerFactory().getMarshaller(object);
        assert marshaller != null;
        Element element = marshaller.marshall(object);
        Signer.signObject(this.signature);
        element.setAttributeNS("http://www.w3.org/2000/xmlns/",
                "xmlns:spid",
                "https://spid.gov.it/saml-extensions");
        return SerializeSupport.nodeToString(element);
    }

    @SneakyThrows
    private EntityDescriptor unmarshall(String object) {
        BasicParserPool parserPool = (BasicParserPool) XMLObjectProviderRegistrySupport.getParserPool();
        assert parserPool != null;
        Element element = parserPool.parse(new ByteArrayInputStream(object.getBytes())).getDocumentElement();
        UnmarshallerFactory unmarshallerFactory = XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
        assert unmarshaller != null;
        return (EntityDescriptor) unmarshaller.unmarshall(element);
    }

    private static String toHex(String input) {
        StringBuilder hexString = new StringBuilder();
        for (char ch : input.toCharArray()) {
            hexString.append(String.format("%02x", (int) ch));
        }
        return hexString.toString();
    }

}
