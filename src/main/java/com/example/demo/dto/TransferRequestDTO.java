package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransferRequestDTO(
        @NotNull(message = "Receiver ID cannot be null")
        Long toUserId,
        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.01", message = "Amount must be positive")
        BigDecimal amount
) {}