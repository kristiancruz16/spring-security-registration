package com.springboot.springsecurityregistration.security.registration;

import lombok.Getter;

/**
 * @author KMCruz
 * 6/23/2021
 */
@Getter
public class SampleEvent {

    private String message;

    public SampleEvent(String message) {
        this.message = message;
    }

}
