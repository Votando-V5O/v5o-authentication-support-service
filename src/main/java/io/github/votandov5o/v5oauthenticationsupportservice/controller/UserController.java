package io.github.votandov5o.v5oauthenticationsupportservice.controller;

import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/")
    public String userProfile(Saml2AuthenticatedPrincipal authentication, Model model) {
//        UserDetails userDetails = (UserDetails) authentication.getgetPrincipal();
        model.addAttribute("username", authentication.getName());
        model.addAttribute("authorities", authentication.getAttributes());
        return "profile"; // Return the name of the view (e.g., `profile.html`)
    }
}