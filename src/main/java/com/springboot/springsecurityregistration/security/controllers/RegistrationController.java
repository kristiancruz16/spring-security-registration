package com.springboot.springsecurityregistration.security.controllers;

import com.springboot.springsecurityregistration.security.domain.User;
import com.springboot.springsecurityregistration.security.exceptions.UserAlreadyExistException;
import com.springboot.springsecurityregistration.security.registration.OnRegistrationCompleteEvent;
import com.springboot.springsecurityregistration.security.services.UserService;
import com.springboot.springsecurityregistration.security.dto.UserDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author KMCruz
 * 6/20/2021
 */
@Controller
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    public RegistrationController(UserService userService,  ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/user/registration")
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "registrationform";
    }

    @PostMapping("/user/registration")
    public String registerUserAccount (HttpServletRequest request, @Valid UserDto userDto,
                                       Errors errors) {
        if(errors.hasErrors()) {
            return "emailError";
        }

        try{
            User registered = userService.registerNewUserAccount(userDto);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                    request.getLocale(), appUrl));
        }catch (UserAlreadyExistException uaeEx){
            errors.rejectValue("email","duplicate","email already exists");
        }
        return "successRegister";

    }
}
