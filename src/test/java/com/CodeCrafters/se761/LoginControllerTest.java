package com.CodeCrafters.se761;


import com.CodeCrafters.se761.user.LoginController;
import com.CodeCrafters.se761.user.UserRepository;
import com.CodeCrafters.se761.user.Users;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @Mock
    private UserRepository userRepository;

    private ClientRegistrationRepository clientRegistrationRepository;

    @InjectMocks
    private LoginController loginController;

    @Test
    public void testHandleGoogleOAuth2Callback_UserExists() throws Exception {

        Users user = new Users("test", "test", "test@example.com", null, null);

        Optional<Users> optionalUser = Optional.of(user);

        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(optionalUser);

        String clientId = "1040650097679-kkmplg72ptscatbdck336gpk7v57cj68.apps.googleusercontent.com";
        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                new OAuth2User() {
                    @Override
                    public Map<String, Object> getAttributes() {
                        Map<String, Object> attributes = new HashMap<>();
                        attributes.put("email", "test@example.com");
                        return attributes;
                    }

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return null;
                    }

                    @Override
                    public String getName() {
                        return null;
                    }
                },
                null,
                clientId
        );

        loginController.handleGoogleOAuth2Callback(authentication);

        Mockito.verify(userRepository, Mockito.times(1)).findByEmail("test@example.com");

        Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    public void testGetRole_Student() throws Exception {

        String clientId = "1040650097679-kkmplg72ptscatbdck336gpk7v57cj68.apps.googleusercontent.com";
        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                new OAuth2User() {
                    @Override
                    public Map<String, Object> getAttributes() {
                        return Collections.singletonMap("email", "student@example.com");
                    }

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
                    }

                    @Override
                    public String getName() {
                        return null;
                    }
                },
                null,
                clientId
        );

        ResponseEntity<String> responseStudent = loginController.getRole(authentication);

        assertEquals("ROLE_STUDENT", responseStudent.getBody());
    }

    @Test
    public void testGetUPI_Staff() throws Exception {

        String clientId = "1040650097679-kkmplg72ptscatbdck336gpk7v57cj68.apps.googleusercontent.com";
        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                new OAuth2User() {
                    @Override
                    public Map<String, Object> getAttributes() {
                        return Collections.singletonMap("email", "staff123@example.com");
                    }

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return Arrays.asList(new SimpleGrantedAuthority("ROLE_STAFF"));
                    }

                    @Override
                    public String getName() {
                        return null;
                    }
                },
                null,
                clientId
        );

        ResponseEntity<String> response = loginController.getUPI(authentication);

        assertEquals("staff123", response.getBody());
    }

    @Test
    public void testGetUPI_Student() throws Exception {

        String clientId = "1040650097679-kkmplg72ptscatbdck336gpk7v57cj68.apps.googleusercontent.com";
        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                new OAuth2User() {
                    @Override
                    public Map<String, Object> getAttributes() {
                        return Collections.singletonMap("email", "student123@example.com");
                    }

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return Arrays.asList(new SimpleGrantedAuthority("ROLE_STUDENT"));
                    }

                    @Override
                    public String getName() {
                        return null;
                    }
                },
                null,
                clientId
        );

        ResponseEntity<String> response = loginController.getUPI(authentication);

        assertEquals("student123", response.getBody());
    }

    @Test
    public void testGetUPI_Admin() throws Exception {

        // Create a new mock OAuth2AuthenticationToken object.
        String clientId = "1040650097679-kkmplg72ptscatbdck336gpk7v57cj68.apps.googleusercontent.com";
        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                new OAuth2User() {
                    @Override
                    public Map<String, Object> getAttributes() {
                        return Collections.singletonMap("email", "admin123@example.com");
                    }

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }

                    @Override
                    public String getName() {
                        return null;
                    }
                },
                null,
                clientId
        );

        ResponseEntity<String> response = loginController.getUPI(authentication);

        assertEquals("admin123", response.getBody());
    }
}


