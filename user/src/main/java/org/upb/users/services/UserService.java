package org.upb.users.services;

import org.upb.users.dtos.*;
import org.upb.users.entity.User;
import org.upb.users.enums.UserStatus;
import org.upb.users.mapper.UserMapper;
import org.upb.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    // CREATE
    public UserDTO createUser(CreateUserDTO createUserDTO) {
        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User(
                createUserDTO.getUsername(),
                createUserDTO.getPassword(),
                createUserDTO.getEmail(),
                createUserDTO.getFirstName(),
                createUserDTO.getLastName()
        );

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    // READ
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO);
    }

    // UPDATE
    public Optional<UserDTO> updateUser(Long id, UpdateUserDTO updateUserDTO) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEmail(updateUserDTO.getEmail());
                    user.setFirstName(updateUserDTO.getFirstName());
                    user.setLastName(updateUserDTO.getLastName());
                    User updatedUser = userRepository.save(user);
                    return userMapper.toDTO(updatedUser);
                });
    }

    // DELETE - hard delete
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Additional endpoints
    public boolean login(LoginDTO loginDTO) {
        return userRepository.findByUsername(loginDTO.getUsername())
                .map(user -> {
                    if (user.getPassword().equals(loginDTO.getPassword()) && user.getStatus() == UserStatus.ACTIVE) {
                        user.setLastLogin(LocalDateTime.now());
                        userRepository.save(user);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    public boolean changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (user.getPassword().equals(changePasswordDTO.getOldPassword())) {
                        user.setPassword(changePasswordDTO.getNewPassword());
                        userRepository.save(user);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    public boolean logout(Long userId) {
        return userRepository.findById(userId).isPresent();
    }

    // Search and filter methods
    public List<UserDTO> searchUsersByName(String name) {
        return userRepository.findByNameContaining(name)
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> filterUsersByStatus(String status) {
        try {
            UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());
            return userRepository.findByStatus(userStatus)
                    .stream()
                    .map(userMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    public List<UserDTO> getUsersSortedByFirstName() {
        return userRepository.findAllByOrderByFirstNameAsc()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getUsersSortedByRegistrationDate() {
        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
}