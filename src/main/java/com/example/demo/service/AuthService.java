package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Аутентификация пользователя по email или телефону
     */
    public String login(String email, String phone, String password) {
        log.info("Attempting login with email={} or phone={}", email, phone);
        User user = userRepository.findByEmails_Email(email)
                .or(() -> userRepository.findByPhones_Phone(phone))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Invalid password for user {}", user.getId());
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getId());
        log.info("Login successful for user {}", user.getId());
        return token;
    }
}