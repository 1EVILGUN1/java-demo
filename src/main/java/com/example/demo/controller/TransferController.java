package com.example.demo.controller;

import com.example.demo.dto.TransferRequestDTO;
import com.example.demo.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService service;

    @Operation(summary = "Transfer money", description = "Transfer money from authenticated user to another user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer successful"),
            @ApiResponse(responseCode = "400", description = "Invalid transfer request or insufficient funds"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<Void> transfer(
            @Valid @RequestBody TransferRequestDTO transferRequestDTO,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("Initiating transfer from user {} to user {} with amount {}",
                userId, transferRequestDTO.toUserId(), transferRequestDTO.amount());
        service.transfer(userId, transferRequestDTO.toUserId(), transferRequestDTO.amount());
        return ResponseEntity.ok().build();
    }
}