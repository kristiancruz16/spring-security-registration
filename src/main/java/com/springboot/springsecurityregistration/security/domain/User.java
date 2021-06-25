package com.springboot.springsecurityregistration.security.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * @author KMCruz
 * 6/20/2021
 */
@Getter
@Setter
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private boolean enabled;

    @OneToOne(mappedBy = "user")
    private PasswordResetToken resetToken;

    @OneToOne(mappedBy = "user")
    private VerificationToken token;

    public User() {
        super();
        this.enabled = false;
    }
}