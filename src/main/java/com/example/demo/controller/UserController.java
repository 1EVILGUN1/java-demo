package com.example.demo.controller;

import com.example.demo.dto.EmailDTO;
import com.example.demo.dto.PhoneDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @Operation(summary = "Search users with filters", description = "Search users by name, date of birth, phone, or email with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters")
    })
    @GetMapping
    public Page<UserDTO> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String dateOfBirth,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Searching users with filters: name={}, dateOfBirth={}, phone={}, email={}, page={}, size={}",
                name, dateOfBirth, phone, email, page, size);
        Pageable pageable = PageRequest.of(page, size);
        return service.searchUsers(name, dateOfBirth, phone, email, pageable);
    }

    @Operation(summary = "Get user by ID", description = "Retrieve user details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "400", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id, @AuthenticationPrincipal Long userId) {
        log.info("Fetching user with id {} by user {}", id, userId);
        return service.getUserById(id);
    }

    @Operation(summary = "Add email for user", description = "Add a new email for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email added"),
            @ApiResponse(responseCode = "400", description = "Invalid email or email already taken"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/{id}/emails")
    public ResponseEntity<Void> addEmail(
            @PathVariable Long id,
            @Valid @RequestBody EmailDTO emailDTO,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("Adding email {} for user {}", emailDTO.email(), id);
        if (!id.equals(userId)) {
            log.warn("User {} attempted to modify user {} email", userId, id);
            throw new AccessDeniedException("Cannot modify another user's data");
        }
        service.addEmail(id, emailDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update email for user", description = "Update an existing email for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email updated"),
            @ApiResponse(responseCode = "400", description = "Invalid email or email not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}/emails")
    public ResponseEntity<Void> updateEmail(
            @PathVariable Long id,
            @RequestParam String oldEmail,
            @Valid @RequestBody EmailDTO newEmailDTO,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("Updating email from {} to {} for user {}", oldEmail, newEmailDTO.email(), id);
        if (!id.equals(userId)) {
            log.warn("User {} attempted to modify user {} email", userId, id);
            throw new AccessDeniedException("Cannot modify another user's data");
        }
        service.updateEmail(id, oldEmail, newEmailDTO.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete email for user", description = "Delete an email for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email deleted"),
            @ApiResponse(responseCode = "400", description = "Email not found or minimum email requirement not met"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}/emails")
    public ResponseEntity<Void> deleteEmail(
            @PathVariable Long id,
            @RequestParam String email,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("Deleting email {} for user {}", email, id);
        if (!id.equals(userId)) {
            log.warn("User {} attempted to modify user {} email", userId, id);
            throw new AccessDeniedException("Cannot modify another user's data");
        }
        service.deleteEmail(id, email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add phone for user", description = "Add a new phone for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Phone added"),
            @ApiResponse(responseCode = "400", description = "Invalid phone or phone already taken"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/{id}/phones")
    public ResponseEntity<Void> addPhone(
            @PathVariable Long id,
            @Valid @RequestBody PhoneDTO phoneDTO,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("Adding phone {} for user {}", phoneDTO.phone(), id);
        if (!id.equals(userId)) {
            log.warn("User {} attempted to modify user {} phone", userId, id);
            throw new AccessDeniedException("Cannot modify another user's data");
        }
        service.addPhone(id, phoneDTO.phone());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update phone for user", description = "Update an existing phone for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Phone updated"),
            @ApiResponse(responseCode = "400", description = "Invalid phone or phone not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}/phones")
    public ResponseEntity<Void> updatePhone(
            @PathVariable Long id,
            @RequestParam String oldPhone,
            @Valid @RequestBody PhoneDTO newPhoneDTO,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("Updating phone from {} to {} for user {}", oldPhone, newPhoneDTO.phone(), id);
        if (!id.equals(userId)) {
            log.warn("User {} attempted to modify user {} phone", userId, id);
            throw new AccessDeniedException("Cannot modify another user's data");
        }
        service.updatePhone(id, oldPhone, newPhoneDTO.phone());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete phone for user", description = "Delete a phone for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Phone deleted"),
            @ApiResponse(responseCode = "400", description = "Phone not found or minimum phone requirement not met"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}/phones")
    public ResponseEntity<Void> deletePhone(
            @PathVariable Long id,
            @RequestParam String phone,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("Deleting phone {} for user {}", phone, id);
        if (!id.equals(userId)) {
            log.warn("User {} attempted to modify user {} phone", userId, id);
            throw new AccessDeniedException("Cannot modify another user's data");
        }
        service.deletePhone(id, phone);
        return ResponseEntity.ok().build();
    }
}