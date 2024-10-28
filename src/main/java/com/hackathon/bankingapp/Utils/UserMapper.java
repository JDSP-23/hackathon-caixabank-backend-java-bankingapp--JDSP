package com.hackathon.bankingapp.Utils;

import com.hackathon.bankingapp.DTO.UserCreateDTO;
import com.hackathon.bankingapp.DTO.UserDTO;
import com.hackathon.bankingapp.Entities.User;

public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getAccountNumber(),
                user.getHashedPassword()
        );
    }

    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        return new User(
                userDTO.getName(),
                userDTO.getEmail(),
                userDTO.getPhoneNumber(),
                userDTO.getAddress(),
                userDTO.getAccountNumber(),
                userDTO.getHashedPassword()
        );
    }

    public User toEntity(UserCreateDTO userCreateDTO) {
        if (userCreateDTO == null) {
            return null;
        }
        return new User(
                userCreateDTO.getName(),
                userCreateDTO.getEmail(),
                userCreateDTO.getPhoneNumber(),
                userCreateDTO.getAddress(),
                null,
                userCreateDTO.getPassword()
        );
    }

    public UserCreateDTO toCreateDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserCreateDTO(
                user.getName(),
                user.getHashedPassword(),
                user.getEmail(),
                user.getAddress(),
                user.getPhoneNumber()
        );
    }
}
