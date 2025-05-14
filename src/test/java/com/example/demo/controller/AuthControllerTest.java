package com.example.demo.controller;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Ничего не настраиваем для SecurityContextHolder, так как /login не требует аутентификации
    }

    @Test
    void login_success() throws Exception {
        // Подготовка данных
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("john.doe@example.com", null, "password123");
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9..."; // Пример токена
        when(authService.login("john.doe@example.com", null, "password123")).thenReturn(token);

        // Выполнение POST-запроса
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));

        // Проверка, что метод сервиса был вызван
        verify(authService).login("john.doe@example.com", null, "password123");
    }

    @Test
    void login_invalidCredentials_throwsIllegalArgumentException() throws Exception {
        // Подготовка данных
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("john.doe@example.com", null, "wrongpassword");
        when(authService.login("john.doe@example.com", null, "wrongpassword"))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        // Выполнение POST-запроса
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));

        // Проверка, что метод сервиса был вызван
        verify(authService).login("john.doe@example.com", null, "wrongpassword");
    }

    @Test
    void login_invalidRequest_throwsConstraintViolationException() throws Exception {
        // Подготовка данных с некорректным email
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("invalid-email", null, "password123");

        // Выполнение POST-запроса
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Проверка, что метод сервиса не был вызван
        verify(authService, never()).login(anyString(), anyString(), anyString());
    }
}