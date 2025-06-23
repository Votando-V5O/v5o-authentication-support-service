package io.github.votandov5o.v5oauthenticationsupportservice.component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Saml2Authentication saml2Authentication = (Saml2Authentication) authentication;
        log.info("Login successful for user: {}", authentication.getName());
        log.info("""
                        Authentication details:
                        Name: {},
                        FiscalCode: {}""",
                authentication.getName(),
                saml2Authentication.getCredentials());
//        log.info(new ObjectMapper().writeValueAsString(authentication));
        response.sendRedirect("/"); // Redirect to the user profile page
    }
}