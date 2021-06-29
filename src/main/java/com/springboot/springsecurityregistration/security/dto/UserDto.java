package com.springboot.springsecurityregistration.security.dto;

import com.springboot.springsecurityregistration.security.validators.PasswordMatches;
import com.springboot.springsecurityregistration.security.validators.ValidEmail;
import com.springboot.springsecurityregistration.security.validators.ValidPassword;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author KMCruz
 * 6/20/2021
 */
@Getter
@Setter
@PasswordMatches(message = "{PasswordMatches.user}")
public class UserDto {

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @NotEmpty
    @ValidPassword
    private String password;
    private String matchingPassword;

    @NotNull
    @NotEmpty
    @ValidEmail
    private String email;
}
