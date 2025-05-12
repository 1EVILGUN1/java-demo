package com.example.demo.dto;

import java.util.List;

public record UserDTO(
        Long id,
        String name,
        String dateOfBirth,
        List<String> emails,
        List<String> phones
) {}