package io.github.votandov5o.v5oauthenticationsupportservice.function;

import io.github.votandov5o.v5oauthenticationsupportservice.dto.OrganizationDTO;
import lombok.extern.slf4j.Slf4j;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.saml.saml2.metadata.OrganizationName;
import org.opensaml.saml.saml2.metadata.OrganizationURL;
import org.opensaml.saml.saml2.metadata.impl.OrganizationBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationDisplayNameBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationNameBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationURLBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Slf4j
public class OrganizationConverterFunction implements Function<OrganizationDTO, Organization> {
    @Override
    public Organization apply(OrganizationDTO organizationDTO) {
        log.debug("Converting OrganizationDTO to Organization: {}", organizationDTO);
        // Main Object
        Organization organization = new OrganizationBuilder().buildObject();

        // Organization Names
        OrganizationName organizationName = new OrganizationNameBuilder().buildObject();
        organizationName.setValue(organizationDTO.getName());

        // Organization Display Name
        OrganizationDisplayName organizationDisplayName = new OrganizationDisplayNameBuilder().buildObject();
        organizationDisplayName.setValue(organizationDTO.getDisplayName());

        // Organization URL
        OrganizationURL organizationURL = new OrganizationURLBuilder().buildObject();
        organizationURL.setURI(organizationDTO.getUrl());

        // Set the language
        organizationName.setXMLLang("it");
        organizationDisplayName.setXMLLang("it");
        organizationURL.setXMLLang("it");

        // Configure Organization
        organization.getOrganizationNames()
                .add(organizationName);
        organization.getDisplayNames()
                .add(organizationDisplayName);
        organization.getURLs()
                .add(organizationURL);

        log.debug("Converted OrganizationDTO to Organization: {}", organization);
        return organization;
    }
}
