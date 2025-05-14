package com.example.demo.service;

import com.example.demo.TestcontainersConfiguration;
import com.example.demo.dto.EmailDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    private String name = "John Doe";
    private String emailAdress = "john.doe@example.com";
    private String dateOfBirth = "15.05.1990";
    private String phoneNumber = "71234567890";
    private String newEmailAdress = "new.email@example.com";
    private String newPhoneNumber = "79876543211";

    @Test
    void getUserById_success() {
        UserDTO userDTO = userService.getUserById(1L);
        assertNotNull(userDTO);
        assertEquals(name, userDTO.name());
        assertEquals(emailAdress, userDTO.emails().getFirst());

        // Verify caching by comparing contents
        UserDTO cachedUserDTO = userService.getUserById(1L);
        assertEquals(userDTO, cachedUserDTO);
    }

    @Test
    void getUserById_notFound_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserById(999L)
        );
        assertEquals("User not found with id: 999", exception.getMessage());
    }

    @Test
    void searchUsers_byPhone_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDTO> result = userService.searchUsers(null, null, phoneNumber, null, pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals(name, result.getContent().get(0).name());
    }

    @Test
    void searchUsers_byEmail_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDTO> result = userService.searchUsers(null, null, null, emailAdress, pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals(name, result.getContent().get(0).name());
    }

    @Test
    void searchUsers_byNameAndDateOfBirth_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDTO> result = userService.searchUsers("John", dateOfBirth, null, null, pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals(name, result.getContent().get(0).name());
    }

    @Test
    void searchUsers_emptyResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDTO> result = userService.searchUsers(null, null, null, "nonexistent@example.com", pageable);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void addEmail_success() {
        EmailDTO emailDTO = new EmailDTO(newEmailAdress);
        userService.addEmail(1L, emailDTO);

        User updatedUser = userRepository.findById(1L).orElseThrow();
        assertEquals(2, updatedUser.getEmails().size());
        assertTrue(updatedUser.getEmails().stream().anyMatch(e -> e.getEmail().equals(newEmailAdress)));

        // Verify cache eviction
        assertNull(cacheManager.getCache("users").get(1L));
    }

    @Test
    void addEmail_emailTaken_throwsIllegalArgumentException() {
        EmailDTO emailDTO = new EmailDTO(emailAdress);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addEmail(1L, emailDTO)
        );
        assertEquals("Email already taken: " + emailAdress, exception.getMessage());
    }

    @Test
    void addEmail_userNotFound_throwsIllegalArgumentException() {
        EmailDTO emailDTO = new EmailDTO("new.email@example.com");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addEmail(999L, emailDTO)
        );
        assertEquals("User not found with id: 999", exception.getMessage());
    }

    @Test
    void deleteEmail_success() {
        // Add a second email
        EmailDTO emailDTO = new EmailDTO(newEmailAdress);
        userService.addEmail(1L, emailDTO);

        // Delete the email
        userService.deleteEmail(1L, newEmailAdress);
        User updatedUser = userRepository.findById(1L).orElseThrow();
        assertEquals(1, updatedUser.getEmails().size());
        assertEquals(emailAdress, updatedUser.getEmails().get(0).getEmail());

        // Verify cache eviction
        assertNull(cacheManager.getCache("users").get(1L));
    }

    @Test
    void deleteEmail_lastEmail_throwsIllegalStateException() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.deleteEmail(1L, emailAdress)
        );
        assertEquals("User must have at least one email", exception.getMessage());
    }

    @Test
    void updateEmail_success() {
        userService.updateEmail(1L, emailAdress, newEmailAdress);
        User updatedUser = userRepository.findById(1L).orElseThrow();
        assertEquals(newEmailAdress, updatedUser.getEmails().get(0).getEmail());

        // Verify cache eviction
        assertNull(cacheManager.getCache("users").get(1L));
    }

    @Test
    void updateEmail_emailTaken_throwsIllegalArgumentException() {
        // Add a second email
        EmailDTO emailDTO = new EmailDTO(newEmailAdress);
        userService.addEmail(1L, emailDTO);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateEmail(1L, emailAdress, newEmailAdress)
        );
        assertEquals("New email already taken: " + newEmailAdress, exception.getMessage());
    }

    @Test
    void updateEmail_emailNotFound_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateEmail(1L, "nonexistent@example.com", newEmailAdress)
        );
        assertEquals("Email not found: nonexistent@example.com", exception.getMessage());
    }

    @Test
    void addPhone_success() {
        userService.addPhone(1L, newPhoneNumber);
        User updatedUser = userRepository.findById(1L).orElseThrow();
        assertEquals(2, updatedUser.getPhones().size());
        assertTrue(updatedUser.getPhones().stream().anyMatch(p -> p.getPhone().equals(newPhoneNumber)));

        // Verify cache eviction
        assertNull(cacheManager.getCache("users").get(1L));
    }

    @Test
    void addPhone_phoneTaken_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addPhone(1L, phoneNumber)
        );
        assertEquals("Phone already taken: " + phoneNumber, exception.getMessage());
    }

    @Test
    void deletePhone_success() {
        // Add a second phone
        userService.addPhone(1L, newPhoneNumber);

        // Delete the phone
        userService.deletePhone(1L, newPhoneNumber);
        User updatedUser = userRepository.findById(1L).orElseThrow();
        assertEquals(1, updatedUser.getPhones().size());
        assertEquals(phoneNumber, updatedUser.getPhones().get(0).getPhone());

        // Verify cache eviction
        assertNull(cacheManager.getCache("users").get(1L));
    }

    @Test
    void deletePhone_lastPhone_throwsIllegalStateException() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.deletePhone(1L, phoneNumber)
        );
        assertEquals("User must have at least one phone", exception.getMessage());
    }

    @Test
    void updatePhone_success() {
        userService.updatePhone(1L, phoneNumber, newPhoneNumber);
        User updatedUser = userRepository.findById(1L).orElseThrow();
        assertEquals(newPhoneNumber, updatedUser.getPhones().get(0).getPhone());

        // Verify cache eviction
        assertNull(cacheManager.getCache("users").get(1L));
    }

    @Test
    void updatePhone_phoneTaken_throwsIllegalArgumentException() {
        // Add a second phone
        userService.addPhone(1L, newPhoneNumber);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updatePhone(1L, phoneNumber, newPhoneNumber)
        );
        assertEquals("New phone already taken: " + newPhoneNumber, exception.getMessage());
    }

    @Test
    void updatePhone_phoneNotFound_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updatePhone(1L, "79999999998", newPhoneNumber)
        );
        assertEquals("Phone not found: 79999999998", exception.getMessage());
    }
}