package io.github.votandov5o.v5oauthenticationsupportservice.component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class PostSuccessAuthenticationHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
        log.info("Login successful for user: {}", principal.getName());
        log.info("""
                        Authentication details:
                        Name: {},
                        FiscalCode: {},
                        BirthDate: {}""",
                authentication.getName(),
                principal.getAttribute("fiscalNumber").getFirst(),
                principal.getAttribute("dateOfBirth").getFirst());
        response.sendRedirect("/"); // Redirect to the user profile page
    }
}