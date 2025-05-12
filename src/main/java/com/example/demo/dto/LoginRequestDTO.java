package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        String email,
        String phone,
        @NotBlank(message = "Password cannot be empty")
        String password
) {}