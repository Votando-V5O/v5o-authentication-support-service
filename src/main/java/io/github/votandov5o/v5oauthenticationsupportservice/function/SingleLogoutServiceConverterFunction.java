package io.github.votandov5o.v5oauthenticationsupportservice.function;

import lombok.extern.slf4j.Slf4j;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml.saml2.metadata.impl.SingleLogoutServiceBuilder;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Slf4j
public class SingleLogoutServiceConverterFunction implements Function<String, SingleLogoutService> {
    @Override
    public SingleLogoutService apply(String s) {
        log.debug("SingleLogoutServiceConverterFunction called with URL: {}", s);
        // Main Object
        SingleLogoutService singleLogoutService = new SingleLogoutServiceBuilder().buildObject();

        // Configure SingleLogoutService
        singleLogoutService.setBinding("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
        singleLogoutService.setLocation(s);

        log.debug("SingleLogoutServiceConverterFunction called with single logout service end");
        return singleLogoutService;
    }
}
