package io.github.votandov5o.v5oauthenticationsupportservice.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/user/profile")
    public String userProfile(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("authorities", userDetails.getAuthorities());
        return "profile"; // Return the name of the view (e.g., `profile.html`)
    }
}