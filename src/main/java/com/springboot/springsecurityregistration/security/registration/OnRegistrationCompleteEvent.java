package com.springboot.springsecurityregistration.security.registration;

import com.springboot.springsecurityregistration.security.domain.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

/**
 * @author KMCruz
 * 6/21/2021
 */
@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    public OnRegistrationCompleteEvent (User user, Locale locale, String appUrl) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}
