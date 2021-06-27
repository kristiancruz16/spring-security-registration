package com.springboot.springsecurityregistration.security.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
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

    private final LocaleResolver localeResolver;
    private final MessageSource messages;


    public CustomAuthenticationFailureHandler(LocaleResolver localeResolver, @Qualifier("messageSource") MessageSource messages) {
        this.localeResolver = localeResolver;
        this.messages = messages;

    }



    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
//        setDefaultFailureUrl("/login?error=true");
//        super.onAuthenticationFailure(request, response, exception);

        Locale locale = localeResolver.resolveLocale(request);

        String error = messages.getMessage("message.badCredentials", null, locale);

        if (exception.getMessage().equalsIgnoreCase("User is disabled")) {
            error = messages.getMessage("auth.message.disabled", null, locale);
        } else if (exception.getMessage().equalsIgnoreCase("User account has expired")) {
            error = messages.getMessage("auth.message.expired", null, locale);
        }
        setDefaultFailureUrl("/login?error="+error);
        super.onAuthenticationFailure(request, response, exception);
//        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, error);
    }
}
