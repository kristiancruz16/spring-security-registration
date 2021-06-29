package com.springboot.springsecurityregistration.security.dto;
import com.springboot.springsecurityregistration.security.validators.ValidPassword;
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
    @ValidPassword
    private String password;

    @NotNull
    @NotEmpty
    private String matchingPassword;
}
