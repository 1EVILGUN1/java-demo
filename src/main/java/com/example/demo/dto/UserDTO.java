package com.example.demo.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserDTO(
        Long id,
        String name,
        String dateOfBirth,
        List<String> emails,
        List<String> phones
) {}