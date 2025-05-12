package com.example.demo.repository;

import com.example.demo.model.EmailData;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {
    boolean existsByEmail(String email);
}
