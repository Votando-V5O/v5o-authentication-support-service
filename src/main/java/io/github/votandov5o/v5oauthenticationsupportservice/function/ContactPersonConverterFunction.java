package io.github.votandov5o.v5oauthenticationsupportservice.function;

import io.github.votandov5o.v5oauthenticationsupportservice.dto.ContactDTO;
import lombok.extern.slf4j.Slf4j;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.impl.XSAnyBuilder;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.ContactPersonTypeEnumeration;
import org.opensaml.saml.saml2.metadata.EmailAddress;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.TelephoneNumber;
import org.opensaml.saml.saml2.metadata.impl.ContactPersonBuilder;
import org.opensaml.saml.saml2.metadata.impl.EmailAddressBuilder;
import org.opensaml.saml.saml2.metadata.impl.ExtensionsBuilder;
import org.opensaml.saml.saml2.metadata.impl.TelephoneNumberBuilder;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.util.function.Function;

@Component
@Slf4j
public class ContactPersonConverterFunction implements Function<ContactDTO, ContactPerson> {

    @Override
    public ContactPerson apply(ContactDTO contactDTO) {
        XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
        // Main Object
        ContactPerson contactPerson = new ContactPersonBuilder().buildObject();

        // Extensions
        Extensions extensions = new ExtensionsBuilder().buildObject();
        XSAnyBuilder xsAnyBuilder = (XSAnyBuilder) builderFactory.getBuilder(XSAny.TYPE_NAME);

        // <spid:Private/>
        QName privateQName = new QName("https://spid.gov.it/saml-extensions", "Public", "spid");
//        QName privateQName = new QName("", "Private", "spid");
        XSAny spidPrivate = xsAnyBuilder.buildObject(privateQName);
        extensions.getUnknownXMLObjects()
                .add(spidPrivate);

        // <spid:fiscalCode>...</spid:fiscalCode>
        QName fiscalCodeQName = new QName("https://spid.gov.it/saml-extensions", "IPACode", "spid");
        XSAny fiscalCode = xsAnyBuilder.buildObject(fiscalCodeQName);
        fiscalCode.setTextContent(contactDTO.getFiscalCode());
        extensions.getUnknownXMLObjects().add(fiscalCode);

        // Email
        EmailAddress emailAddress = new EmailAddressBuilder().buildObject();
        emailAddress.setURI(contactDTO.getEmail());

        // Telephone
        TelephoneNumber telephoneNumber = new TelephoneNumberBuilder().buildObject();
        telephoneNumber.setValue(contactDTO.getPhoneNumber());

        // Configure ContactPerson
        contactPerson.setType(ContactPersonTypeEnumeration.OTHER);
        contactPerson.setExtensions(extensions);
        contactPerson.getEmailAddresses()
                .add(emailAddress);
        contactPerson.getTelephoneNumbers()
                .add(telephoneNumber);
        return contactPerson;
    }
}
