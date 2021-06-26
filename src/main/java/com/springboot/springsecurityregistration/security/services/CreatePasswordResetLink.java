package com.springboot.springsecurityregistration.security.services;

import com.springboot.springsecurityregistration.security.domain.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private final JavaMailSender mailSender;
    private final MessageSource messages;


    public CreatePasswordResetLink(UserService userService, JavaMailSender mailSender,
                                   @Qualifier("messageSource") MessageSource messages) {
        this.userService = userService;
        this.mailSender = mailSender;
        this.messages = messages;
    }

    public void mail(User user, Locale locale, String appUrl){
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetToken(user,token);

        String recipientAddress = user.getEmail();
        String subject = "Reset Password";
        String confirmationUrl
                = appUrl + "/login/resetPassword?token=" + token;
        String message = messages.getMessage("message.resetPassword", null, locale);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + "http://localhost:8080"+ confirmationUrl);
        mailSender.send(email);

    }
}
