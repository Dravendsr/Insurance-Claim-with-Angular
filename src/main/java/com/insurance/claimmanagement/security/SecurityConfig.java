package com.insurance.claimmanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                         BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationProvider authenticationProvider) throws Exception {
        http.authenticationProvider(authenticationProvider)
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/",
                                "/index.html",
                                "/app.js",
                                "/style.css",
                                "/favicon.ico",
                                "/**/*.js",
                                "/**/*.css",
                                "/**/*.html",
                                "/**/*.png",
                                "/**/*.jpg",
                                "/**/*.svg",
                                "/**/*.ico"
                        ).permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/role/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/policies").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/policies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/policies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/policies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/hospitals").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/hospitals/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/hospitals/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/hospitals/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/doctors").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/user-policies").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/user-policies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/user-policies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/user-policies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/payments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/payments/*/process").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/claims/status/PENDING").hasRole("APPROVER")
                        .requestMatchers(HttpMethod.PUT, "/api/claims/*/approve").hasRole("APPROVER")
                        .requestMatchers(HttpMethod.PUT, "/api/claims/*/reject").hasRole("APPROVER")
                        .requestMatchers(HttpMethod.PUT, "/api/claims/*").hasAnyRole("ADMIN", "APPROVER")
                        .requestMatchers(HttpMethod.PATCH, "/api/claims/**").hasAnyRole("ADMIN", "APPROVER")
                        .requestMatchers(HttpMethod.POST, "/api/claims").hasRole("POLICYHOLDER")
                        .requestMatchers(HttpMethod.GET, "/api/admin/analytics/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
