package com.springboot.springsecurityregistration.security.exceptions;

/**
 * @author KMCruz
 * 6/21/2021
 */
public class UserAlreadyExistException extends RuntimeException{

    public UserAlreadyExistException() {
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }

    public UserAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExistException(Throwable cause) {
        super(cause);
    }
}
