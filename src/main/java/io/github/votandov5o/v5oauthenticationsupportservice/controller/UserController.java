package io.github.votandov5o.v5oauthenticationsupportservice.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/")
    public String userProfile(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("fiscalNumber", ((Saml2AuthenticatedPrincipal) authentication.getPrincipal()).getAttribute("fiscalNumber").getFirst());
        model.addAttribute("dateOfBirth", ((Saml2AuthenticatedPrincipal) authentication.getPrincipal()).getAttribute("dateOfBirth").getFirst());
        return "profile";
    }
}