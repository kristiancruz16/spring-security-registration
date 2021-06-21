package com.springboot.springsecurityregistration.security.dto;

import com.springboot.springsecurityregistration.security.validators.PasswordMatches;
import com.springboot.springsecurityregistration.security.validators.ValidEmail;
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
@PasswordMatches
public class UserDto {

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;

    @NotNull
    @NotEmpty
    @ValidEmail
    private String email;
}
