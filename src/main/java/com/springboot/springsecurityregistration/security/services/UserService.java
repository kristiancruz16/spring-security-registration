package com.springboot.springsecurityregistration.security.services;

import com.springboot.springsecurityregistration.security.domain.PasswordResetToken;
import com.springboot.springsecurityregistration.security.domain.User;
import com.springboot.springsecurityregistration.security.domain.VerificationToken;
import com.springboot.springsecurityregistration.security.dto.UserDto;
import com.springboot.springsecurityregistration.security.exceptions.UserAlreadyExistException;
import com.springboot.springsecurityregistration.security.exceptions.UserNotFoundException;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * @author KMCruz
 * 6/20/2021
 */
public interface UserService {

    User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException;;

    boolean isEmailExists(String email);

    User getUserByVerificationToken(String verificationToken);

    void saveRegisteredUser(User user);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String token);

    VerificationToken getVerificationTokenByUser(User user);

    void createPasswordResetToken(User user, String token);

    PasswordResetToken getPasswordResetTokenByResetToken(String resetToken);

    User findUserByEmail(String email) throws UserNotFoundException;

    void deleteVerificationToken(VerificationToken vToken);
}
