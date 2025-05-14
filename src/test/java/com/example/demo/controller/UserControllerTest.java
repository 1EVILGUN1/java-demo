package com.example.demo.controller;

import com.example.demo.TestWebConfig;
import com.example.demo.dto.EmailDTO;
import com.example.demo.dto.PhoneDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestWebConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private Long userId;

    @BeforeEach
    void setUp() {
        // Настраиваем тестовые данные
        userId = 1L;
        userDTO = new UserDTO(
                userId,
                "John Doe",
                "15.05.1990",
                List.of("john.doe@example.com"),
                List.of("71234567890")
        );

        // Настраиваем SecurityContext для имитации аутентификации
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userId);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void searchUsers_success() throws Exception {
        // Мокаем ответ от UserService
        Page<UserDTO> userPage = new PageImpl<>(List.of(userDTO), PageRequest.of(0, 10), 1);
        when(userService.searchUsers(anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn(userPage);

        // Выполняем GET-запрос
        mockMvc.perform(get("/api/users")
                        .param("name", "John")
                        .param("dateOfBirth", "15.05.1990")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Проверяем, что метод сервиса был вызван
        verify(userService).searchUsers("John", "15.05.1990", null, null, PageRequest.of(0, 10));
    }

    @Test
    void getUserById_success() throws Exception {
        // Мокаем ответ от UserService
        when(userService.getUserById(userId)).thenReturn(userDTO);

        // Выполняем GET-запрос
        mockMvc.perform(get("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.emails[0]", is("john.doe@example.com")));

        // Проверяем, что метод сервиса был вызван
        verify(userService).getUserById(userId);
    }

    @Test
    void getUserById_notFound_throwsIllegalArgumentException() throws Exception {
        // Мокаем исключение от UserService
        when(userService.getUserById(999L)).thenThrow(new IllegalArgumentException("User not found with id: 999"));

        // Выполняем GET-запрос
        mockMvc.perform(get("/api/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("User not found with id: 999")));

        // Проверяем, что метод сервиса был вызван
        verify(userService).getUserById(999L);
    }

    @Test
    void addEmail_success() throws Exception {
        // Мокаем поведение UserService
        doNothing().when(userService).addEmail(eq(userId), any(EmailDTO.class));

        // Создаем EmailDTO
        EmailDTO emailDTO = new EmailDTO("new.email@example.com");

        // Выполняем POST-запрос
        mockMvc.perform(post("/api/users/{id}/emails", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailDTO)))
                .andExpect(status().isOk());

        // Проверяем, что метод сервиса был вызван
        verify(userService).addEmail(userId, emailDTO);
    }

    @Test
    void addEmail_accessDenied_throwsAccessDeniedException() throws Exception {
        // Создаем EmailDTO
        EmailDTO emailDTO = new EmailDTO("new.email@example.com");

        // Выполняем POST-запрос с другим userId
        mockMvc.perform(post("/api/users/{id}/emails", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Access denied: Cannot modify another user's data")));

        // Проверяем, что метод сервиса не был вызван
        verify(userService, never()).addEmail(anyLong(), any(EmailDTO.class));
    }

    @Test
    void addEmail_invalidEmail_throwsConstraintViolationException() throws Exception {
        // Создаем EmailDTO с некорректным email
        EmailDTO emailDTO = new EmailDTO("invalid-email");

        // Выполняем POST-запрос
        mockMvc.perform(post("/api/users/{id}/emails", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailDTO)))
                .andExpect(status().isBadRequest());

        // Проверяем, что метод сервиса не был вызван
        verify(userService, never()).addEmail(anyLong(), any(EmailDTO.class));
    }

    @Test
    void updateEmail_success() throws Exception {
        // Мокаем поведение UserService
        doNothing().when(userService).updateEmail(eq(userId), anyString(), anyString());

        // Создаем EmailDTO
        EmailDTO newEmailDTO = new EmailDTO("updated.email@example.com");

        // Выполняем PUT-запрос
        mockMvc.perform(put("/api/users/{id}/emails", userId)
                        .param("oldEmail", "john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmailDTO)))
                .andExpect(status().isOk());

        // Проверяем, что метод сервиса был вызван
        verify(userService).updateEmail(userId, "john.doe@example.com", "updated.email@example.com");
    }

    @Test
    void updateEmail_accessDenied_throwsAccessDeniedException() throws Exception {
        // Создаем EmailDTO
        EmailDTO newEmailDTO = new EmailDTO("updated.email@example.com");

        // Выполняем PUT-запрос с другим userId
        mockMvc.perform(put("/api/users/{id}/emails", 2L)
                        .param("oldEmail", "john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmailDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Access denied: Cannot modify another user's data")));

        // Проверяем, что метод сервиса не был вызван
        verify(userService, never()).updateEmail(anyLong(), anyString(), anyString());
    }

    @Test
    void deleteEmail_success() throws Exception {
        // Мокаем поведение UserService
        doNothing().when(userService).deleteEmail(eq(userId), anyString());

        // Выполняем DELETE-запрос
        mockMvc.perform(delete("/api/users/{id}/emails", userId)
                        .param("email", "john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Проверяем, что метод сервиса был вызван
        verify(userService).deleteEmail(userId, "john.doe@example.com");
    }

    @Test
    void deleteEmail_accessDenied_throwsAccessDeniedException() throws Exception {
        // Выполняем DELETE-запрос с другим userId
        mockMvc.perform(delete("/api/users/{id}/emails", 2L)
                        .param("email", "john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Access denied: Cannot modify another user's data")));

        // Проверяем, что метод сервиса не был вызван
        verify(userService, never()).deleteEmail(anyLong(), anyString());
    }

    @Test
    void addPhone_success() throws Exception {
        // Мокаем поведение UserService
        doNothing().when(userService).addPhone(eq(userId), anyString());

        // Создаем PhoneDTO
        PhoneDTO phoneDTO = new PhoneDTO("79876543210");

        // Выполняем POST-запрос
        mockMvc.perform(post("/api/users/{id}/phones", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(phoneDTO)))
                .andExpect(status().isOk());

        // Проверяем, что метод сервиса был вызван
        verify(userService).addPhone(userId, "79876543210");
    }

    @Test
    void addPhone_accessDenied_throwsAccessDeniedException() throws Exception {
        // Создаем PhoneDTO
        PhoneDTO phoneDTO = new PhoneDTO("79876543210");

        // Выполняем POST-запрос с другим userId
        mockMvc.perform(post("/api/users/{id}/phones", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(phoneDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Access denied: Cannot modify another user's data")));

        // Проверяем, что метод сервиса не был вызван
        verify(userService, never()).addPhone(anyLong(), anyString());
    }

    @Test
    void updatePhone_success() throws Exception {
        // Мокаем поведение UserService
        doNothing().when(userService).updatePhone(eq(userId), anyString(), anyString());

        // Создаем PhoneDTO
        PhoneDTO newPhoneDTO = new PhoneDTO("79876543210");

        // Выполняем PUT-запрос
        mockMvc.perform(put("/api/users/{id}/phones", userId)
                        .param("oldPhone", "71234567890")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPhoneDTO)))
                .andExpect(status().isOk());

        // Проверяем, что метод сервиса был вызван
        verify(userService).updatePhone(userId, "71234567890", "79876543210");
    }

    @Test
    void updatePhone_accessDenied_throwsAccessDeniedException() throws Exception {
        // Создаем PhoneDTO
        PhoneDTO newPhoneDTO = new PhoneDTO("79876543210");

        // Выполняем PUT-запрос с другим userId
        mockMvc.perform(put("/api/users/{id}/phones", 2L)
                        .param("oldPhone", "71234567890")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPhoneDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Access denied: Cannot modify another user's data")));

        // Проверяем, что метод сервиса не был вызван
        verify(userService, never()).updatePhone(anyLong(), anyString(), anyString());
    }

    @Test
    void deletePhone_success() throws Exception {
        // Мокаем поведение UserService
        doNothing().when(userService).deletePhone(eq(userId), anyString());

        // Выполняем DELETE-запрос
        mockMvc.perform(delete("/api/users/{id}/phones", userId)
                        .param("phone", "71234567890")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Проверяем, что метод сервиса был вызван
        verify(userService).deletePhone(userId, "71234567890");
    }

    @Test
    void deletePhone_accessDenied_throwsAccessDeniedException() throws Exception {
        // Выполняем DELETE-запрос с другим userId
        mockMvc.perform(delete("/api/users/{id}/phones", 2L)
                        .param("phone", "71234567890")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("Access denied: Cannot modify another user's data")));

        // Проверяем, что метод сервиса не был вызван
        verify(userService, never()).deletePhone(anyLong(), anyString());
    }
}