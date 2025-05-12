package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository repository;

    /**
     * Перевод денег между пользователями
     */
    @Transactional(rollbackOn = Exception.class)
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        log.info("Initiating transfer of {} from user {} to user {}", amount, fromUserId, toUserId);

        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Cannot transfer to the same user");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        // Блокируем аккаунты для предотвращения гонки
        Account fromAccount = repository.findByUserId(fromUserId)
                .orElseThrow(() -> new IllegalArgumentException("Sender account not found"));
        Account toAccount = repository.findByUserId(toUserId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient funds for user {}", fromUserId);
            throw new IllegalArgumentException("Insufficient funds");
        }

        // Обновляем балансы
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Сохраняем изменения
        repository.save(fromAccount);
        repository.save(toAccount);

        log.info("Transfer completed: {} from user {} to user {}", amount, fromUserId, toUserId);
    }
}