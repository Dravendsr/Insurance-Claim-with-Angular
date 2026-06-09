package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.User;
import com.insurance.claimmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User buildUser(Long id, String username, String role) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(username);
        user.setPassword("password");
        user.setFullName("Test User");
        user.setEmail(username + "@example.com");
        user.setPhoneNumber("9999999999");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user.setAddress("123 Main St");
        user.setRole(role);
        user.setAccountStatus("ACTIVE");
        return user;
    }

    @Test
    void saveUser_shouldSaveUserSuccessfully() {
        // Arrange: mock password encoding and repository save
        User user = buildUser(1L, "john.doe", "POLICYHOLDER");
        user.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: call service saveUser
        User savedUser = userService.saveUser(user);

        // Assert: user is returned, save called, and password was encoded
        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserById_shouldReturnUserWhenExists() {
        // Arrange: repository returns a user for the requested ID
        User user = buildUser(1L, "john.doe", "POLICYHOLDER");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act: call service getUserById
        Optional<User> result = userService.getUserById(1L);

        // Assert: result contains the expected user
        assertTrue(result.isPresent());
        assertEquals("john.doe", result.get().getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_shouldReturnEmptyWhenUserNotFound() {
        // Arrange: repository returns empty when user is missing
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Act: call service getUserById
        Optional<User> result = userService.getUserById(2L);

        // Assert: no user is returned
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        // Arrange: repository returns a list of users
        User user1 = buildUser(1L, "john.doe", "POLICYHOLDER");
        User user2 = buildUser(2L, "jane.smith", "APPROVER");
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        // Act: call service getAllUsers
        List<User> result = userService.getAllUsers();

        // Assert: both users are returned
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUsersByRole_shouldReturnUsersByRole() {
        // Arrange: repository returns users with the specified role
        User user = buildUser(1L, "john.doe", "POLICYHOLDER");
        when(userRepository.findByRole("POLICYHOLDER")).thenReturn(List.of(user));

        // Act: call service getUsersByRole
        List<User> result = userService.getUsersByRole("POLICYHOLDER");

        // Assert: returned list matches the mocked role filter
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("POLICYHOLDER", result.get(0).getRole());
        verify(userRepository, times(1)).findByRole("POLICYHOLDER");
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        // Arrange: existing user is found, and update request includes an empty password
        User existingUser = buildUser(1L, "john.doe", "POLICYHOLDER");
        existingUser.setPassword("encodedPassword");

        User updateRequest = buildUser(1L, "john.doe", "POLICYHOLDER");
        updateRequest.setPassword("");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(updateRequest)).thenReturn(updateRequest);

        // Act: call service updateUser
        User updatedUser = userService.updateUser(updateRequest);

        // Assert: update succeeds and preserves the existing password
        assertNotNull(updatedUser);
        assertEquals("encodedPassword", updatedUser.getPassword());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(updateRequest);
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {
        // Arrange: repository indicates the user exists
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act: call service deleteUser
        boolean deleted = userService.deleteUser(1L);

        // Assert: deletion was performed
        assertTrue(deleted);
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_shouldReturnFalseWhenUserNotFound() {
        // Arrange: repository indicates the user does not exist
        when(userRepository.existsById(2L)).thenReturn(false);

        // Act: call service deleteUser
        boolean deleted = userService.deleteUser(2L);

        // Assert: deletion is not performed when user is missing
        assertFalse(deleted);
        verify(userRepository, times(1)).existsById(2L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void validateLogin_shouldReturnUserWhenCredentialsAreCorrect() {
        // Arrange: repository returns a valid active user with plain password
        User storedUser = buildUser(1L, "john.doe", "POLICYHOLDER");
        storedUser.setPassword("secret");
        storedUser.setAccountStatus("ACTIVE");

        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(storedUser));

        // Act: call service validateLogin
        Optional<User> result = userService.validateLogin("john.doe", "secret");

        // Assert: login succeeds and user is returned
        assertTrue(result.isPresent());
        assertEquals("john.doe", result.get().getUsername());
        verify(userRepository, times(1)).findByUsername("john.doe");
        verify(userRepository, times(1)).save(storedUser);
    }

    @Test
    void validateLogin_shouldReturnEmptyWhenCredentialsAreInvalid() {
        // Arrange: repository returns a user, but password does not match
        User storedUser = buildUser(1L, "john.doe", "POLICYHOLDER");
        storedUser.setPassword("secret");
        storedUser.setAccountStatus("ACTIVE");

        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(storedUser));

        // Act: call service validateLogin with wrong credentials
        Optional<User> result = userService.validateLogin("john.doe", "wrongPassword");

        // Assert: login fails and no save is performed
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("john.doe");
        verify(userRepository, never()).save(any(User.class));
    }
}
