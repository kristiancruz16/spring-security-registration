package com.springboot.springsecurityregistration.security.services;

import com.springboot.springsecurityregistration.security.domain.PasswordResetToken;
import com.springboot.springsecurityregistration.security.domain.User;
import com.springboot.springsecurityregistration.security.domain.VerificationToken;
import com.springboot.springsecurityregistration.security.dto.UserDto;
import com.springboot.springsecurityregistration.security.exceptions.UserAlreadyExistException;
import com.springboot.springsecurityregistration.security.exceptions.UserNotFoundException;
import com.springboot.springsecurityregistration.security.repositories.PasswordResetTokenRepository;
import com.springboot.springsecurityregistration.security.repositories.UserRepository;
import com.springboot.springsecurityregistration.security.repositories.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import static com.springboot.springsecurityregistration.security.domain.UserRole.*;

/**
 * @author KMCruz
 * 6/20/2021
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository resetTokenRepository;

    public UserServiceImpl(UserRepository userRepository, VerificationTokenRepository tokenRepository,
                           PasswordEncoder passwordEncoder, PasswordResetTokenRepository resetTokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.resetTokenRepository = resetTokenRepository;
    }

    @Override
    public User registerNewUserAccount(UserDto userDto) {
        if(isEmailExists(userDto.getEmail())) {
            throw new UserAlreadyExistException(userDto.getEmail()+" already exists!");
        }

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setUserRole(USER);

        return userRepository.save(user);
    }

    public boolean isEmailExists(String email){
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public User getUserByVerificationToken(String verificationToken) {
        User user = tokenRepository.findByToken(verificationToken).getUser();
        return user;
    }

    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken();
        if (user.getToken()!=null){
            myToken = user.getToken();
            myToken=myToken.createOrUpdateVerificationToken(user, token);
        }
        else {
            myToken = myToken.createOrUpdateVerificationToken(user,token);
        }
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public VerificationToken getVerificationTokenByUser(User user) {
        return tokenRepository.findByUser(user);
    }

    @Override
    public void createPasswordResetToken(User user, String token) {
        PasswordResetToken resetToken = new PasswordResetToken();
        if (user.getResetToken()!=null){
            resetToken = user.getResetToken();
            resetToken=resetToken.createOrUpdateVerificationToken(user, token);
        }
        else {
            resetToken = resetToken.createOrUpdateVerificationToken(user,token);
        }
        resetTokenRepository.save(resetToken);
    }

    @Override
    public PasswordResetToken getPasswordResetTokenByResetToken(String resetToken) {
        return resetTokenRepository.findByResetToken(resetToken);
    }

    @Override
    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user==null) {
            throw new UserNotFoundException(email+" does not exists.");
        }
        return user;
    }

    @Override
    public void deleteVerificationToken(VerificationToken vToken) {
        tokenRepository.delete(vToken);
    }

    @Override
    public void deletePasswordResetToken(PasswordResetToken passwordResetToken) {
        resetTokenRepository.delete(passwordResetToken);
    }

}
