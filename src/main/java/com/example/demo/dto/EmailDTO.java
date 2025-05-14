package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailDTO(
        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Email should be valid")
        String email
) {}