package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    @Pattern(regexp = "\\d{2}\\.\\d{2}\\.\\d{4}", message = "Date must be in format dd.MM.yyyy")
    private String dateOfBirth;

    @Column(nullable = false, length = 500)
    @Size(min = 8, max = 500, message = "Password must be between 8 and 500 characters")
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Account account;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Size(min = 1, message = "At least one email is required")
    @ToString.Exclude
    private List<EmailData> emails = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Size(min = 1, message = "At least one phone is required")
    @ToString.Exclude
    private List<Phone> phones = new ArrayList<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;

        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}