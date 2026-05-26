package com.CodeCrafters.se761.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) { this.userService = userService; }

    //@GetMapping
    public List<Users> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/addStaff")
    public ResponseEntity<?> manageStaff(@RequestBody Map<String, String> requestMap) {
        String email = requestMap.get("email");
        String upi = requestMap.get("upi");
        String name = requestMap.get("name");
        String userProfile = requestMap.get("user_profile");
        String phone = requestMap.get("phone");

        return userService.addStaff(email, upi, name, userProfile, phone);
    }

    @PostMapping("/deleteStaff")
    public ResponseEntity<?> removeStaff(@RequestBody Map<String, String> requestMap) {
        String email = requestMap.get("email");

        return userService.removeStaff(email);
    }
}