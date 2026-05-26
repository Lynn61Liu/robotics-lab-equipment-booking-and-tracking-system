package com.CodeCrafters.se761;

import com.CodeCrafters.se761.equipment.Equipment;
import com.CodeCrafters.se761.equipment.EquipmentController;
import com.CodeCrafters.se761.equipment.EquipmentRepository;
import com.CodeCrafters.se761.equipment.EquipmentService;
import com.CodeCrafters.se761.user.UserController;
import com.CodeCrafters.se761.user.UserRepository;
import com.CodeCrafters.se761.user.UserService;
import com.CodeCrafters.se761.user.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UsersTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testFindByUpi() {
        Users user = new Users("test", "test", "test@example.com", "upi", null);

        Mockito.when(userRepository.findByUpi("upi")).thenReturn(Optional.of(user));

        Optional<Users> foundUser = userRepository.findByUpi("upi");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(user);
    }

    @Test
    public void testFindByEmail() {
        Users user = new Users("test", "test", "test@example.com", null, null);

        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<Users> foundUser = userRepository.findByEmail("test@example.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(user);
    }

    @Test
    public void testGetUsers() {
        // Create a list of mock users
        List<Users> mockUsers = new ArrayList<>();
        mockUsers.add(new Users("test@example.com", "upi", "name", "userProfile", "phone"));
        mockUsers.add(new Users("test2@example.com", "upi2", "name2", "userProfile2", "phone2"));

        Mockito.when(userRepository.findAll()).thenReturn(mockUsers);

        List<Users> users = userService.getUsers();

        assertThat(users).isEqualTo(mockUsers);
    }


    @Test
    public void testAddStaff() {
        // Create a mock user
        Users mockUser = new Users("test@example.com", "upi", "name", "userProfile", "phone");

        // Stub the UserRepository.findByEmail() method to return the mock user
        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        // Call the addStaff() method on the UserService class
        ResponseEntity<?> response = userService.addStaff("test@example.com", "upi", "name", "userProfile", "phone");

        // Assert that the addStaff() method returned a successful response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Assert that the UserRepository.save() method was called with the updated user
        Mockito.verify(userRepository).save(mockUser);
    }
    @Test
    public void testRemoveStaff() {
        // Create a mock user
        Users mockUser = new Users("test@example.com", "upi", "name", "userProfile", "phone");

        // Stub the UserRepository.findByEmail() method to return the mock user
        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        // Call the deleteStaff() method on the UserService class
        ResponseEntity<?> response = userService.removeStaff("test@example.com");

        // Assert that the deleteStaff() method returned a successful response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Assert that the UserRepository.delete() method was called with the mock user
        Mockito.verify(userRepository).delete(mockUser);
    }
}


