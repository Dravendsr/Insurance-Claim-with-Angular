package com.insurance.claimmanagement.security;

import com.insurance.claimmanagement.entity.User;
import com.insurance.claimmanagement.repository.UserRepository;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepository.findByUsername(username != null ? username.trim() : null)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (appUser.getAccountStatus() == null || appUser.getAccountStatus().trim().equalsIgnoreCase("INACTIVE")) {
            throw new UsernameNotFoundException("User account is inactive: " + username);
        }

        String role = appUser.getRole() != null ? appUser.getRole().trim().toUpperCase() : "POLICYHOLDER";
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

        return org.springframework.security.core.userdetails.User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities(Collections.singletonList(authority))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
