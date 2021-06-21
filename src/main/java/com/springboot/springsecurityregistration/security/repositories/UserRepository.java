package com.springboot.springsecurityregistration.security.repositories;

import com.springboot.springsecurityregistration.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author KMCruz
 * 6/20/2021
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
