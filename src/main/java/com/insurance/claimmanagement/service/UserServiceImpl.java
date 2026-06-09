package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.User;
import com.insurance.claimmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Override
    public User saveUser(User user) {
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null");
            }
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Username is required");
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }

            userRepository.findByUsernameIgnoreCase(user.getUsername().trim()).ifPresent(existing -> {
                if (user.getUserId() == null || !existing.getUserId().equals(user.getUserId())) {
                    throw new IllegalArgumentException("Username already exists");
                }
            });
            userRepository.findByEmailIgnoreCase(user.getEmail().trim()).ifPresent(existing -> {
                if (user.getUserId() == null || !existing.getUserId().equals(user.getUserId())) {
                    throw new IllegalArgumentException("Email already exists");
                }
            });

            // Hash the password before saving
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error saving user: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<User> getUserById(Long userId) {
        try {
            return userRepository.findById(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user: " + e.getMessage());
        }
    }
    
    @Override
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all users: " + e.getMessage());
        }
    }
    
    @Override
    public List<User> getUsersByRole(String role) {
        try {
            return userRepository.findByRole(role);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching users by role: " + e.getMessage());
        }
    }
    
    @Override
    public User updateUser(User user) {
        try {
            Optional<User> existingUser = userRepository.findById(user.getUserId());
            if (existingUser.isPresent()) {
                userRepository.findByUsernameIgnoreCase(user.getUsername().trim()).ifPresent(conflict -> {
                    if (!conflict.getUserId().equals(user.getUserId())) {
                        throw new IllegalArgumentException("Username already exists");
                    }
                });
                userRepository.findByEmailIgnoreCase(user.getEmail().trim()).ifPresent(conflict -> {
                    if (!conflict.getUserId().equals(user.getUserId())) {
                        throw new IllegalArgumentException("Email already exists");
                    }
                });

                User dbUser = existingUser.get();

                // Preserve existing password unless a new one is provided
                if (user.getPassword() == null || user.getPassword().isEmpty()) {
                    user.setPassword(dbUser.getPassword());
                } else {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                }

                return userRepository.save(user);
            } else {
                throw new RuntimeException("User not found with ID: " + user.getUserId());
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }
    
    @Override
    public User patchUser(Long userId, Map<String, Object> updates) {
        try {
            Optional<User> existingUser = userRepository.findById(userId);
            if (existingUser.isEmpty()) {
                throw new RuntimeException("User not found with ID: " + userId);
            }

            User user = existingUser.get();

            if (updates.containsKey("username") && updates.get("username") != null) {
                user.setUsername(updates.get("username").toString());
            }
            if (updates.containsKey("password") && updates.get("password") != null) {
                user.setPassword(passwordEncoder.encode(updates.get("password").toString()));
            }
            if (updates.containsKey("fullName") && updates.get("fullName") != null) {
                user.setFullName(updates.get("fullName").toString());
            }
            if (updates.containsKey("email") && updates.get("email") != null) {
                user.setEmail(updates.get("email").toString());
            }
            if (updates.containsKey("phoneNumber") && updates.get("phoneNumber") != null) {
                user.setPhoneNumber(updates.get("phoneNumber").toString());
            }
            if (updates.containsKey("dateOfBirth") && updates.get("dateOfBirth") != null) {
                user.setDateOfBirth(LocalDate.parse(updates.get("dateOfBirth").toString()));
            }
            if (updates.containsKey("address") && updates.get("address") != null) {
                user.setAddress(updates.get("address").toString());
            }
            if (updates.containsKey("role") && updates.get("role") != null) {
                user.setRole(updates.get("role").toString());
            }
            if (updates.containsKey("accountStatus") && updates.get("accountStatus") != null) {
                user.setAccountStatus(updates.get("accountStatus").toString());
            }
            if (updates.containsKey("bankAccountNumber")) {
                user.setBankAccountNumber(updates.get("bankAccountNumber") == null ? null : updates.get("bankAccountNumber").toString());
            }
            if (updates.containsKey("ifscCode")) {
                user.setIfscCode(updates.get("ifscCode") == null ? null : updates.get("ifscCode").toString());
            }
            if (updates.containsKey("bankName")) {
                user.setBankName(updates.get("bankName") == null ? null : updates.get("bankName").toString());
            }
            if (updates.containsKey("lastLogin") && updates.get("lastLogin") != null) {
                user.setLastLogin(LocalDateTime.parse(updates.get("lastLogin").toString()));
            }

            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error patching user: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteUser(Long userId) {
        try {
            if (userRepository.existsById(userId)) {
                userRepository.deleteById(userId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<User> validateLogin(String username, String password) {
        try {
            String inputUsername = username != null ? username.trim() : null;
            String inputPassword = password != null ? password.trim() : null;

            if (inputUsername == null || inputUsername.isEmpty() || inputPassword == null || inputPassword.isEmpty()) {
                return Optional.empty();
            }

            Optional<User> user = userRepository.findByUsername(inputUsername);
            if (user.isEmpty()) {
                user = userRepository.findByUsernameIgnoreCase(inputUsername);
            }

            if (user.isPresent()) {
                User foundUser = user.get();

                // Check account status
                if (foundUser.getAccountStatus() == null ||
                    foundUser.getAccountStatus().trim().equalsIgnoreCase("INACTIVE")) {
                    return Optional.empty();
                }

                String storedPassword = foundUser.getPassword();
                if (storedPassword == null) {
                    return Optional.empty();
                }

                storedPassword = storedPassword.trim();
                boolean passwordMatched = false;

                // If stored password is a BCrypt hash, verify it; otherwise compare plain text.
                if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
                    try {
                        passwordMatched = passwordEncoder.matches(inputPassword, storedPassword);
                    } catch (Exception e) {
                        passwordMatched = false;
                    }
                } else {
                    passwordMatched = inputPassword.equals(storedPassword);
                }

                if (passwordMatched) {
                    foundUser.setLastLogin(LocalDateTime.now());
                    userRepository.save(foundUser);
                    return Optional.of(foundUser);
                }
            }

            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error validating login: " + e.getMessage());
        }
    }
}

