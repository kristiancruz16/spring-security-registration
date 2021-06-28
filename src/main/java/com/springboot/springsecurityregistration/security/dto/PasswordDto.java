package com.springboot.springsecurityregistration.security.dto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author KMCruz
 * 6/28/2021
 */
@Getter
@Setter
public class PasswordDto {
    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private String matchingPassword;
}
