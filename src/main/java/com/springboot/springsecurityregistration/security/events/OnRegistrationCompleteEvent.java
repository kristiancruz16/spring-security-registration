package com.springboot.springsecurityregistration.security.events;

import com.springboot.springsecurityregistration.security.domain.User;
import lombok.Getter;

import java.util.Locale;

/**
 * @author KMCruz
 * 6/21/2021
 */
@Getter
public class OnRegistrationCompleteEvent /*extends ApplicationEvent*/ {
    private String appUrl;
    private Locale locale;
    private User user;

    public OnRegistrationCompleteEvent (User user, Locale locale, String appUrl) {
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}
