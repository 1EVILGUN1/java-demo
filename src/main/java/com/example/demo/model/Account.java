package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@ToString
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "initial_deposit", nullable = false, precision = 19, scale = 2)
    private BigDecimal initialDeposit;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @ToString.Exclude // Исключаем из toString во избежание циклических ссылок
    private User user;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return id != null && Objects.equals(id, account.id);
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}