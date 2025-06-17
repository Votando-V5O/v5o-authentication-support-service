package io.github.votandov5o.v5oauthenticationsupportservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    @GetMapping("/login/spid/arubapec")
    public String loginSpid() {
        // Redirect al flusso SAML
        return "redirect:/login/saml2/sso/arubapec";
    }

    @GetMapping("/login/cie")
    public String loginCie() {
        // Redirect al flusso OAuth2/OIDC
        return "redirect:/oauth2/authorization/cie";
    }
}