package com.springboot.springsecurityregistration.security.repositories;

import com.springboot.springsecurityregistration.security.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author KMCruz
 * 6/25/2021
 */

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByResetToken(String resetToken);
}
