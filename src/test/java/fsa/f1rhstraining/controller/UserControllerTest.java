package fsa.f1rhstraining.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fsa.f1rhstraining.dto.UserDto;
import fsa.f1rhstraining.service.UserService;
import fsa.f1rhstraining.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public UserService userService() {
            return Mockito.mock(UserServiceImpl.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllUsers() throws Exception {
        UserDto user1 = createSampleUser(1L, "user1", "user1@example.com", "password1", "User One");
        UserDto user2 = createSampleUser(2L, "user2", "user2@example.com", "password2", "User Two");
        
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));
        
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    public void testGetUserById() throws Exception {
        UserDto user = createSampleUser(1L, "testuser", "test@example.com", "password", "Test User");
        
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testGetUserByUsername() throws Exception {
        UserDto user = createSampleUser(1L, "testuser", "test@example.com", "password", "Test User");
        
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(user));
        
        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testGetUserByEmail() throws Exception {
        UserDto user = createSampleUser(1L, "testuser", "test@example.com", "password", "Test User");
        
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
        
        mockMvc.perform(get("/api/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void testCreateUser() throws Exception {
        UserDto userToCreate = createSampleUser(null, "newuser", "new@example.com", "password", "New User");
        UserDto createdUser = createSampleUser(1L, "newuser", "new@example.com", "password", "New User");
        
        when(userService.createUser(any(UserDto.class))).thenReturn(createdUser);
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    public void testCreateUserWithExistingUsername() throws Exception {
        UserDto userToCreate = createSampleUser(null, "existinguser", "new@example.com", "password", "New User");
        
        when(userService.createUser(any(UserDto.class))).thenThrow(new IllegalArgumentException("Username already exists"));
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserDto userToUpdate = createSampleUser(1L, "updateduser", "updated@example.com", "password", "Updated User");
        
        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(Optional.of(userToUpdate));
        
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);
        
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    private UserDto createSampleUser(Long id, String username, String email, String password, String fullName) {
        return UserDto.builder()
                .id(id)
                .username(username)
                .email(email)
                .password(password)
                .fullName(fullName)
                .createdAt(LocalDateTime.now())
                .build();
    }
}