package com.example.demo.service;

import com.example.demo.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;


    @Test
    void login_successWithEmail() {
        String token = authService.login("john.doe@example.com", null, "password123");
        assertNotNull(token);
    }

    @Test
    void login_successWithPhone() {
        String token = authService.login(null, "71234567890", "password123");
        assertNotNull(token);
    }

    @Test
    void login_invalidPassword_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login("john.doe@example.com", null, "wrongpassword")
        );
        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    void login_userNotFoundWithEmail_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login("nonexistent@example.com", null, "password123")
        );
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void login_userNotFoundWithPhone_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(null, "79999999999", "password123")
        );
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void login_noEmailOrPhone_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(null, null, "password123")
        );
        assertEquals("Invalid password", exception.getMessage());
    }
}