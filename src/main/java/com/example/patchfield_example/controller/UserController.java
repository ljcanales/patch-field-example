package com.example.patchfield_example.controller;

import com.example.patchfield_example.repository.UserRepository;
import com.example.patchfield_example.data.entities.User;
import com.example.patchfield_example.data.request.UserPostRequest;
import com.example.patchfield_example.data.request.UserUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // PATCH endpoint for updating user fields
    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request
    ) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();

        request.getName().ifProvided(user::setName);
        request.getEmail().ifProvided(user::setEmail);

        userRepository.save(user);
        return ResponseEntity.ok(user);

    }

    // POST endpoint for creating a new user
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserPostRequest newUserRequest) {
        User user = new User();
        user.setName(newUserRequest.getName());
        user.setEmail(newUserRequest.getEmail());

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // GET endpoint for retrieving a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        return userOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
