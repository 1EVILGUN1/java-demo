package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequestDTO(
        @Email
        String email,
        String phone,
        @NotBlank(message = "Password cannot be empty")
        String password
) {}