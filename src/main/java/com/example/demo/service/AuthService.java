package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String login(String email, String phone, String password) {
        User user = null;
        if (email != null) {
            user = userRepository.findByEmails_Email(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        } else if (phone != null) {
            user = userRepository.findByPhones_Phone(phone)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        return jwtUtil.generateToken(user.getId());
    }
}