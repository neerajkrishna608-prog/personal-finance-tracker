package com.finance.personalfinancetracker.repository;

import com.finance.personalfinancetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailAndPassword(String email, String password);

    User findByEmail(String email);

    // Find user using the password reset token
    User findByResetToken(String resetToken);

}