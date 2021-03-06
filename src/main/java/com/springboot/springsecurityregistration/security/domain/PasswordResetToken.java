package com.springboot.springsecurityregistration.security.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @author KMCruz
 * 6/25/2021
 */
@Data
@NoArgsConstructor
@Entity
public class PasswordResetToken {

    private static final int EXPIRATION = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String resetToken;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id",nullable = false)
    private User user;

    private Date expiryDate;

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public PasswordResetToken createOrUpdateVerificationToken(User user, String resetToken) {
        this.resetToken = resetToken;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
        return this;
    }
}
