package com.CodeCrafters.se761;

import com.CodeCrafters.se761.user.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    @Test
    void oauthLoginRedirectsToEquipmentList() {
        assertEquals("redirect:/equipmentList", loginController.getLoginPage());
    }

    @Test
    void successRedirectsToEquipmentList() {
        assertEquals("redirect:/equipmentList", loginController.handleGoogleOAuth2Callback());
    }

    @Test
    void getRoleReturnsAdminRole() {
        ResponseEntity<String> response = loginController.getRole();

        assertEquals("ROLE_ADMIN", response.getBody());
    }

    @Test
    void getUpiReturnsLocalUser() {
        ResponseEntity<String> response = loginController.getUPI();

        assertEquals("localuser", response.getBody());
    }
}
