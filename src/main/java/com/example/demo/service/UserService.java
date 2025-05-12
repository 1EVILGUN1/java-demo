package com.example.demo.service;

import com.example.demo.dto.EmailDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.EmailData;
import com.example.demo.model.Phone;
import com.example.demo.model.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.EmailDataRepository;
import com.example.demo.repository.PhoneRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository repository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneRepository phoneRepository;
    private final AccountRepository accountRepository;
    private final UserMapper mapper;

    /**
     * Получение пользователя по ID с кэшированием
     */
    @Cacheable(value = "users", key = "#id")
    public UserDTO getUserById(Long id) {
        log.info("Fetching user with id {}", id);
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    /**
     * Поиск пользователей с фильтрацией и пагинацией
     */
    @Cacheable(value = "userSearch", key = "{#name, #dateOfBirth, #phone, #email, #pageable.pageNumber, #pageable.pageSize}")
    public Page<UserDTO> searchUsers(String name, String dateOfBirth, String phone, String email, Pageable pageable) {
        log.info("Searching users with filters: name={}, dateOfBirth={}, phone={}, email={}", name, dateOfBirth, phone, email);
        Page<User> userPage;
        if (phone != null) {
            userPage = repository.findByPhones_Phone(phone, pageable);
        } else if (email != null) {
            userPage = repository.findByEmails_Email(email, pageable);
        } else {
            userPage = repository.findByNameStartingWithAndDateOfBirthAfter(
                    name != null ? name : "",
                    dateOfBirth != null ? dateOfBirth : "00.00.0000",
                    pageable
            );
        }
        return userPage.map(mapper::toDto);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void addEmail(Long userId, EmailDTO emailDTO) {
        log.info("Adding email {} for user {}", emailDTO.email(), userId);
        if (emailDataRepository.existsByEmail(emailDTO.email())) {
            throw new IllegalArgumentException("Email already taken: " + emailDTO.email());
        }
        User user = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        EmailData emailData = new EmailData();
        emailData.setEmail(emailDTO.email());
        emailData.setUser(user);
        user.getEmails().add(emailData);
        repository.save(user);
    }

    /**
     * Удаление email пользователя
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void deleteEmail(Long userId, String email) {
        log.info("Deleting email {} for user {}", email, userId);
        User user = findById(userId);
        if (user.getEmails().size() <= 1) {
            throw new IllegalStateException("User must have at least one email");
        }
        user.getEmails().removeIf(e -> e.getEmail().equals(email));
        repository.save(user);
    }

    /**
     * Обновление email пользователя
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void updateEmail(Long userId, String oldEmail, String newEmail) {
        log.info("Updating email from {} to {} for user {}", oldEmail, newEmail, userId);
        if (emailDataRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("New email already taken: " + newEmail);
        }
        User user = findById(userId);
        user.getEmails().stream()
                .filter(e -> e.getEmail().equals(oldEmail))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Email not found: " + oldEmail))
                .setEmail(newEmail);
        repository.save(user);
    }

    /**
     * Добавление нового телефона для пользователя
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void addPhone(Long userId, String phone) {
        log.info("Adding phone {} for user {}", phone, userId);
        if (phoneRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Phone already taken: " + phone);
        }
        User user = findById(userId);
        Phone phoneData = new Phone();
        phoneData.setPhone(phone);
        phoneData.setUser(user);
        user.getPhones().add(phoneData);
        repository.save(user);
    }

    /**
     * Удаление телефона пользователя
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void deletePhone(Long userId, String phone) {
        log.info("Deleting phone {} for user {}", phone, userId);
        User user = findById(userId);
        if (user.getPhones().size() <= 1) {
            throw new IllegalStateException("User must have at least one phone");
        }
        user.getPhones().removeIf(p -> p.getPhone().equals(phone));
        repository.save(user);
    }

    /**
     * Обновление телефона пользователя
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void updatePhone(Long userId, String oldPhone, String newPhone) {
        log.info("Updating phone from {} to {} for user {}", oldPhone, newPhone, userId);
        if (phoneRepository.existsByPhone(newPhone)) {
            throw new IllegalArgumentException("New phone already taken: " + newPhone);
        }
        User user = findById(userId);
        user.getPhones().stream()
                .filter(p -> p.getPhone().equals(oldPhone))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Phone not found: " + oldPhone))
                .setPhone(newPhone);
        repository.save(user);
    }

    private User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }
}