package com.CodeCrafters.se761.user;

import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/oauth_login")
    public String getLoginPage() {
        return "redirect:/equipmentList";
    }

    @GetMapping("/success")
    public String handleGoogleOAuth2Callback() {
        return "redirect:/equipmentList";
    }

    @GetMapping("/getRole")
    public ResponseEntity<String> getRole(){
        return ResponseEntity.ok("ROLE_ADMIN");
    }

    @GetMapping("/getUPI")
    public ResponseEntity<String> getUPI(){
        return ResponseEntity.ok("localuser");
    }

}
