package com.springboot.springsecurityregistration.security.services;

import com.springboot.springsecurityregistration.security.domain.User;
import com.springboot.springsecurityregistration.security.domain.VerificationToken;
import com.springboot.springsecurityregistration.security.dto.UserDto;
import com.springboot.springsecurityregistration.security.exceptions.UserAlreadyExistException;
import com.springboot.springsecurityregistration.security.repositories.UserRepository;
import com.springboot.springsecurityregistration.security.repositories.VerificationTokenRepository;
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

    public UserServiceImpl(UserRepository userRepository, VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public User registerNewUserAccount(UserDto userDto) {
        if(isEmailExists(userDto.getEmail())) {
            throw new UserAlreadyExistException(userDto.getEmail()+" already exists!");
        }

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setUserRole(USER);

        return userRepository.save(user);
    }


    public boolean isEmailExists(String email){
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public User getUser(String verificationToken) {
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
}
