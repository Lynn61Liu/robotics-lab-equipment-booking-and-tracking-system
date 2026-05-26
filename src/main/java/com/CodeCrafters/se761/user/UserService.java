package com.CodeCrafters.se761.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Users> getUsers() {
        return userRepository.findAll();
    }

    public Optional<Users> getUserByUPI(String upi){
        return userRepository.findByUpi(upi);
    }

    public ResponseEntity<?> addStaff(String email, String upi, String name, String userProfile, String phone) {
        Optional<Users> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            user.setUserProfile(userProfile);
            user.setUpi(upi);
            user.setName(name);
            user.setPhone(phone);

            userRepository.save(user);

            return ResponseEntity.ok(Map.of("success", true));
        } else {
            Users newUser = new Users();
            newUser.setEmail(email);
            newUser.setUpi(upi);
            newUser.setUserProfile(userProfile);
            newUser.setName(name);
            newUser.setPhone(phone);

            userRepository.save(newUser);

            return ResponseEntity.ok(Map.of("success", true));
        }
    }

    public ResponseEntity<?> removeStaff(String email) {
        Optional<Users> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            System.out.println("user present");
            // Delete the user from the database
            userRepository.delete(user);

            return ResponseEntity.ok(Map.of("success", true));
        }

        return ResponseEntity.ok(Map.of("success", false));
    }
}