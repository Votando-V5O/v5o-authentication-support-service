package io.github.votandov5o.v5oauthenticationsupportservice.component;

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
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.saml.saml2.metadata.OrganizationName;
import org.opensaml.saml.saml2.metadata.OrganizationURL;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml.saml2.metadata.impl.AttributeConsumingServiceBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationDisplayNameBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationNameBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationURLBuilder;
import org.opensaml.saml.saml2.metadata.impl.RequestedAttributeBuilder;
import org.opensaml.saml.saml2.metadata.impl.SingleLogoutServiceBuilder;
import org.springframework.security.saml2.provider.service.metadata.OpenSaml4MetadataResolver;
import org.springframework.security.saml2.provider.service.metadata.Saml2MetadataResolver;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;

import static org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport.getMarshallerFactory;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceProviderMetadataResolver implements Saml2MetadataResolver {

    OpenSaml4MetadataResolver resolver = new OpenSaml4MetadataResolver();

    @Override
    public String resolve(RelyingPartyRegistration relyingPartyRegistration) {
        String openSamlResolved = resolver.resolve(relyingPartyRegistration);
        EntityDescriptor entityDescriptor = unmarshall(openSamlResolved);

        // Add AttributeConsumingService with RequestedAttribute
        RequestedAttribute spidCode = new RequestedAttributeBuilder().buildObject();
        spidCode.setName("spidCode");
        spidCode.setIsRequired(Boolean.TRUE);
        RequestedAttribute fiscalNumber = new RequestedAttributeBuilder().buildObject();
        fiscalNumber.setName("fiscalNumber");
        fiscalNumber.setIsRequired(Boolean.TRUE);

        AttributeConsumingService attributeConsumingService = new AttributeConsumingServiceBuilder().buildObject();
        attributeConsumingService.setIndex(1);
        attributeConsumingService.getRequestedAttributes()
                .add(spidCode);
        attributeConsumingService.getRequestedAttributes()
                .add(fiscalNumber);

        // Add Organization and ContactPerson
        Organization organization = new OrganizationBuilder().buildObject();
        OrganizationName organizationName = new OrganizationNameBuilder().buildObject();
        OrganizationURL organizationURL = new OrganizationURLBuilder().buildObject();
        OrganizationDisplayName organizationDisplayName = new OrganizationDisplayNameBuilder().buildObject();
        organizationDisplayName.setValue("Votando V5O");
        organizationName.setValue("Votando-V5O");
        organizationURL.setURI("https://votando-v5o.github.io");
        organization.getOrganizationNames()
                .add(organizationName);
        organization.getDisplayNames()
                .add(organizationDisplayName);
        organization.getURLs()
                .add(organizationURL);


        SingleLogoutService singleLogoutService = new SingleLogoutServiceBuilder().buildObject();
        singleLogoutService.setBinding("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
        singleLogoutService.setLocation("{baseUrl}/logout/saml2/slo");
        entityDescriptor.getSPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol")
                .getSingleLogoutServices()
                .add(singleLogoutService);
        entityDescriptor.getSPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol")
                .setAuthnRequestsSigned(Boolean.TRUE);
        entityDescriptor.getSPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol")
                .getAttributeConsumingServices()
                .add(attributeConsumingService);
        entityDescriptor.setOrganization(organization);
        return marshall(entityDescriptor);
    }

    @SneakyThrows
    private String marshall(XMLObject object) {
        Marshaller marshaller = getMarshallerFactory().getMarshaller(object);
        assert marshaller != null;
        Element element = marshaller.marshall(object);
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

}
