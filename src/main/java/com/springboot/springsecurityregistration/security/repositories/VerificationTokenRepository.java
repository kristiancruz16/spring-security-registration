package com.springboot.springsecurityregistration.security.repositories;

import com.springboot.springsecurityregistration.security.domain.User;
import com.springboot.springsecurityregistration.security.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author KMCruz
 * 6/21/2021
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    VerificationToken findByToken (String token);

    VerificationToken findByUser(User user);
}
