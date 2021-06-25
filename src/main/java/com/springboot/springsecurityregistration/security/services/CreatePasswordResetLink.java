package com.springboot.springsecurityregistration.security.services;

import com.springboot.springsecurityregistration.security.domain.User;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

/**
 * @author KMCruz
 * 6/25/2021
 */
@Service
public class CreatePasswordResetLink {

    private final UserService userService;

    public CreatePasswordResetLink(UserService userService) {
        this.userService = userService;
    }

    public void mail(User user, Locale locale, String appUrl){
        String token = UUID.randomUUID().toString();


    }
}
