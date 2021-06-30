package com.springboot.springsecurityregistration.security.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * @author KMCruz
 * 6/22/2021
 */
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final LocaleResolver localeResolver;
    private final MessageSource messages;


    public CustomAuthenticationFailureHandler(LocaleResolver localeResolver, @Qualifier("messageSource") MessageSource messages) {
        this.localeResolver = localeResolver;
        this.messages = messages;

    }



    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        Locale locale = localeResolver.resolveLocale(request);
        LOGGER.info(exception.getMessage());
        String error = messages.getMessage("message.badCredentials", null, locale);
        if (exception.getMessage().equalsIgnoreCase("User account is locked")) {
            error = messages.getMessage("auth.message.blocked", null, locale);
        }
        if (exception.getMessage().equalsIgnoreCase("User is disabled")) {
            error = messages.getMessage("auth.message.disabled", null, locale);
        } else if (exception.getMessage().equalsIgnoreCase("User account has expired")) {
            error = messages.getMessage("auth.message.expired", null, locale);
        }
        setDefaultFailureUrl("/login?error="+error);
        super.onAuthenticationFailure(request, response, exception);

    }
}
