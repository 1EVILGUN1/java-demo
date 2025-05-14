package com.example.demo.repository;

import com.example.demo.TestcontainersConfiguration;
import com.example.demo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // Отключаем замену DataSource
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class) // Подключаем Testcontainers
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmails_Email_found() {
        Optional<User> user = userRepository.findByEmails_Email("john.doe@example.com");
        assertTrue(user.isPresent());
        assertEquals("John Doe", user.get().getName());
    }

    @Test
    void findByEmails_Email_notFound() {
        Optional<User> user = userRepository.findByEmails_Email("nonexistent@example.com");
        assertFalse(user.isPresent());
    }
}