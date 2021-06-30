package com.springboot.springsecurityregistration.security.services;

import com.springboot.springsecurityregistration.security.repositories.VerificationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;

/**
 * @author KMCruz
 * 6/30/2021
 */
@Service
@Transactional
public class TokensPurgeTaskService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final VerificationTokenRepository tokenRepository;

    public TokensPurgeTaskService(VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Scheduled(cron = "${cron.scheduled.time}")
    public void purgedExpiredToken(){
        LOGGER.info("Deleting expired verification token..............");
        Date now = Date.from(Instant.now());
        tokenRepository.deleteAllExpiredSince(now);
    }
}
