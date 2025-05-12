
package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmails_Email(String email);

    Optional<User> findByPhones_Phone(String phone);

    Page<User> findByEmails_Email(String email, Pageable pageable);

    Page<User> findByPhones_Phone(String phone, Pageable pageable);

    Page<User> findByNameStartingWithAndDateOfBirthAfter(String name, String dateOfBirth, Pageable pageable);
}