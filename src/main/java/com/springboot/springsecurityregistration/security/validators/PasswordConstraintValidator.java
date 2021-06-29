package com.springboot.springsecurityregistration.security.validators;


import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * @author KMCruz
 * 6/29/2021
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    public void initialize(ValidPassword arg0) {
    }

    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator(List.of(
                new LengthRule(8, 30),
                new UppercaseCharacterRule(1),
                new DigitCharacterRule(1),
                new SpecialCharacterRule(1),
                new NumericalSequenceRule(3,false),
                new AlphabeticalSequenceRule(3,false),
                new QwertySequenceRule(3,false),
                new WhitespaceRule()));

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        for(String message : validator.getMessages(result)) {
            System.out.println(message);
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
        return false;
    }
}
