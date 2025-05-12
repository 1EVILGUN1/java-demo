package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class BalanceScheduler {
    private final AccountRepository repository;

    /**
     * Начисление 10% на баланс каждые 30 секунд, но не более 207% от начального депозита
     */
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void increaseBalances() {
        log.info("Starting balance increase process");
        List<Account> accounts = repository.findAll();
        for (Account account : accounts) {
            BigDecimal maxBalance = account.getInitialDeposit().multiply(BigDecimal.valueOf(2.07));
            BigDecimal newBalance = account.getBalance().multiply(BigDecimal.valueOf(1.10));

            if (newBalance.compareTo(maxBalance) > 0) {
                newBalance = maxBalance;
            }

            account.setBalance(newBalance);
            repository.save(account);
            log.debug("Updated balance for account {}: {}", account.getId(), newBalance);
        }
        log.info("Balance increase process completed");
    }
}