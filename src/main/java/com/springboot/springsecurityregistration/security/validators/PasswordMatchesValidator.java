package com.springboot.springsecurityregistration.security.validators;

import com.springboot.springsecurityregistration.security.dto.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author KMCruz
 * 6/20/2021
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches,Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        UserDto user = (UserDto) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }

}

