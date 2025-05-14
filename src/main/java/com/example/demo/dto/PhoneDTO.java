package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record PhoneDTO(
        @NotBlank(message = "Phone cannot be empty")
        @Pattern(regexp = "^7\\d{10}$", message = "Phone must start with 7 and contain 11 digits")
        String phone
) {}