package com.springboot.springsecurityregistration.security.registration;


import com.springboot.springsecurityregistration.security.domain.User;
import com.springboot.springsecurityregistration.security.services.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


import java.util.UUID;

/**
 * @author KMCruz
 * 6/21/2021
 */
@Component
public class RegistrationListener {

    private final UserService userService;
    private final MessageSource messages;
    private final JavaMailSender mailSender;

    public RegistrationListener(UserService userService, @Qualifier("messageSource") MessageSource messages, JavaMailSender mailSender) {
        this.userService = userService;
        this.messages = messages;
        this.mailSender = mailSender;
    }

    @Async
    @EventListener
    public void handlerOnRegistrationCompleteEvent(OnRegistrationCompleteEvent event){
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl
                = event.getAppUrl() + "/registrationConfirm?token=" + token;
        String message = messages.getMessage("message.registrationSuccessLink", null, event.getLocale());

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + "http://localhost:8080"+ confirmationUrl);
        mailSender.send(email);
    }

}
