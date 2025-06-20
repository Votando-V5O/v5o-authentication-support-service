package io.github.votandov5o.v5oauthenticationsupportservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

//    @GetMapping("/login/spid/arubapec")
//    public String loginSpid() {
//        // Redirect al flusso SAML
//        return "redirect:/login/saml2/sso/arubapec";
//    }
//
//    @GetMapping("/login/cie")
//    public String loginCie() {
//        // Redirect al flusso OAuth2/OIDC
//        return "redirect:/oauth2/authorization/cie";
//    }

//    @GetMapping("/login/saml2/sso/{registrationId}")
//    public String redirectToLogin(@PathVariable("registrationId") String registrationId) {
//        return "redirect:/saml2/authenticate?registrationId=" + registrationId;
//    }


    public String get(@RequestParam("page") Integer page,
                      @RequestParam("size") Integer size,
                      @RequestHeader("filter") String filter) {
        // Decode filter from Base64
        // unmarshall filter to corresponding DTO
        // Perform the search operation
        // Return the result
        return null;
    }
}