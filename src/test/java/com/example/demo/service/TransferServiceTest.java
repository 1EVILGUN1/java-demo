package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.model.User;
import com.example.demo.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferService transferService;

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        fromAccount = new Account();
        fromAccount.setId(1L);
        User user = new User();
        user.setId(1L);
        fromAccount.setUser(user);
        fromAccount.setBalance(new BigDecimal("1000.00"));

        toAccount = new Account();
        toAccount.setId(2L);
        User userTwo = new User();
        user.setId(2L);
        toAccount.setUser(userTwo);
        toAccount.setBalance(new BigDecimal("500.00"));
    }

    @Test
    void transfer_success() {
        // Arrange
        when(accountRepository.findByUserId(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserId(2L)).thenReturn(Optional.of(toAccount));

        // Act
        transferService.transfer(1L, 2L, new BigDecimal("300.00"));

        // Assert
        assertEquals(new BigDecimal("700.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("800.00"), toAccount.getBalance());
        verify(accountRepository, times(2)).save(any());
    }

    @Test
    void transfer_insufficientFunds_throwsException() {
        // Arrange
        when(accountRepository.findByUserId(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserId(2L)).thenReturn(Optional.of(toAccount));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(1L, 2L, new BigDecimal("1500.00")));
        assertEquals("Insufficient funds", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void transfer_userNotFound_throwsException() {
        // Arrange
        when(accountRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(1L, 2L, new BigDecimal("300.00")));
        assertEquals("Sender account not found", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }
}
