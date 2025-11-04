package org.upb.users.controller;

import org.upb.users.dtos.*;
import org.upb.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // CRUD Endpoints

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserDTO createUserDTO) {
        try {
            UserDTO userDTO = userService.createUser(createUserDTO);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO) {
        return userService.updateUser(id, updateUserDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok().body("User deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }

    // Additional endpoints

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        boolean success = userService.login(loginDTO);
        if (success) {
            return ResponseEntity.ok().body("Login successful");
        }
        return ResponseEntity.badRequest().body("Invalid credentials or user not active");
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody ChangePasswordDTO changePasswordDTO) {
        boolean success = userService.changePassword(id, changePasswordDTO);
        if (success) {
            return ResponseEntity.ok().body("Password changed successfully");
        }
        return ResponseEntity.badRequest().body("Invalid old password or user not found");
    }

    @PostMapping("/{id}/logout")
    public ResponseEntity<?> logout(@PathVariable Long id) {
        boolean success = userService.logout(id);
        if (success) {
            return ResponseEntity.ok().body("Logout successful");
        }
        return ResponseEntity.notFound().build();
    }

    // Search and filter endpoints

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsersByName(@RequestParam String name) {
        List<UserDTO> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterUsersByStatus(@RequestParam String status) {
        try {
            List<UserDTO> users = userService.filterUsersByStatus(status);
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/sorted/first-name")
    public ResponseEntity<List<UserDTO>> getUsersSortedByFirstName() {
        List<UserDTO> users = userService.getUsersSortedByFirstName();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/sorted/registration-date")
    public ResponseEntity<List<UserDTO>> getUsersSortedByRegistrationDate() {
        List<UserDTO> users = userService.getUsersSortedByRegistrationDate();
        return ResponseEntity.ok(users);
    }
}