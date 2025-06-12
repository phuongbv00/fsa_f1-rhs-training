package fsa.f1rhstraining.service;

import fsa.f1rhstraining.dto.UserDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // Get all users
    List<UserDto> getAllUsers();

    // Get user by ID
    Optional<UserDto> getUserById(Long id);

    // Get user by username
    Optional<UserDto> getUserByUsername(String username);

    // Get user by email
    Optional<UserDto> getUserByEmail(String email);

    // Create a new user
    @Transactional
    UserDto createUser(UserDto userDto);

    // Update an existing user
    @Transactional
    Optional<UserDto> updateUser(Long id, UserDto userDto);

    // Delete a user
    @Transactional
    boolean deleteUser(Long id);
}
