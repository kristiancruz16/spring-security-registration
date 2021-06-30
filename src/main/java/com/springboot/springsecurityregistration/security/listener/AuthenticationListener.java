package com.springboot.springsecurityregistration.security.listener;

import com.springboot.springsecurityregistration.security.services.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * @author KMCruz
 * 6/30/2021
 */
@Component
public class AuthenticationListener {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final LoginAttemptService loginAttemptService;


    public AuthenticationListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }


    @EventListener
    public void handlerAuthenticationFailureBadCredentialsEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getPrincipal().toString();
        LOGGER.info(String.format("Username : %s",username));
        //todo create an implementation that adds login attempt failure and locked the account if the user reached the login attempt limit
    }

    @EventListener
    public void handlerAuthenticationSuccessEvent(AuthenticationSuccessEvent event){
        LOGGER.info("Success");
        //todo create an implementation that resets the number of login attempts to 0 if logged in successfully

    }
}
