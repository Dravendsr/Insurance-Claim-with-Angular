package com.insurance.claimmanagement.service;

import com.insurance.claimmanagement.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    Optional<User> getUserById(Long userId);
    List<User> getAllUsers();
    List<User> getUsersByRole(String role);
    User updateUser(User user);
    User patchUser(Long userId, java.util.Map<String, Object> updates);
    boolean deleteUser(Long userId);
    Optional<User> validateLogin(String username, String password);
}
