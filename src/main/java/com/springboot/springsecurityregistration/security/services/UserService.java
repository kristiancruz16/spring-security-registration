package com.springboot.springsecurityregistration.security.services;

import com.springboot.springsecurityregistration.security.domain.User;
import com.springboot.springsecurityregistration.security.domain.VerificationToken;
import com.springboot.springsecurityregistration.security.dto.UserDto;
import com.springboot.springsecurityregistration.security.exceptions.UserAlreadyExistException;

/**
 * @author KMCruz
 * 6/20/2021
 */
public interface UserService {

    User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException;;

    boolean isEmailExists(String email);

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String token);
}
