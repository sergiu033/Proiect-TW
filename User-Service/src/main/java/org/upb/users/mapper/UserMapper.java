package org.upb.users.mapper;

import org.upb.users.dtos.UserDTO;
import org.upb.users.entity.User;
import org.upb.users.enums.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus().name(),
                user.getCreatedAt(),
                user.getLastLogin()
        );
    }

    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());

        // Set status based on string from DTO
        if (userDTO.getStatus() != null) {
            try {
                user.setStatus(UserStatus.valueOf(userDTO.getStatus()));
            } catch (IllegalArgumentException e) {
                user.setStatus(UserStatus.ACTIVE);
            }
        }

        return user;
    }

    // Method to update existing entity from DTO
    public void updateEntityFromDTO(UserDTO userDTO, User user) {
        if (userDTO == null || user == null) {
            return;
        }

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());

        if (userDTO.getStatus() != null) {
            try {
                user.setStatus(UserStatus.valueOf(userDTO.getStatus()));
            } catch (IllegalArgumentException e) {
                // Keep existing status if invalid
            }
        }
    }
}