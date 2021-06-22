package com.springboot.springsecurityregistration.security.configs;

import com.springboot.springsecurityregistration.security.services.MyUserDetailsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;


/**
 * @author KMCruz
 * 6/21/2021
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final MyUserDetailsService userDetailsService;
    private final SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler;

    public WebSecurityConfig(MyUserDetailsService userDetailsService, SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler) {
        this.userDetailsService = userDetailsService;
        this.simpleUrlAuthenticationFailureHandler = simpleUrlAuthenticationFailureHandler;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .failureHandler(simpleUrlAuthenticationFailureHandler);
    }


}
