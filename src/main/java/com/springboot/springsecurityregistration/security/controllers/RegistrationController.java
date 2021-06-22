package com.springboot.springsecurityregistration.security.controllers;

import com.springboot.springsecurityregistration.security.domain.User;
import com.springboot.springsecurityregistration.security.domain.VerificationToken;
import com.springboot.springsecurityregistration.security.exceptions.UserAlreadyExistException;
import com.springboot.springsecurityregistration.security.registration.OnRegistrationCompleteEvent;
import com.springboot.springsecurityregistration.security.services.UserService;
import com.springboot.springsecurityregistration.security.dto.UserDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author KMCruz
 * 6/20/2021
 */
@Controller
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messages;

    public RegistrationController(UserService userService, ApplicationEventPublisher eventPublisher,
                                  @Qualifier("messageSource") MessageSource messages) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.messages = messages;
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
        } catch (RuntimeException ex) {
            return "emailError";
        }
        return "successRegister";

    }
    @GetMapping("/registrationConfirm")
    public String confirmRegistration
            (HttpServletRequest request, Model model, @RequestParam("token") String token) {
        System.out.println("Confirmed Registration");
        Locale locale = request.getLocale();

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = messages.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String messageValue = messages.getMessage("auth.message.expired", null, locale);
            model.addAttribute("message", messageValue);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        String message = messages.getMessage("message.accountVerified",null,locale);
        model.addAttribute("message",message);
//        return "registrationConfirm";
        return "redirect:/login?message=" + message;
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request,  Model model, @RequestParam("message")  Optional<String> messageKey, @RequestParam("error" )  Optional<String> error) {
        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        messageKey.ifPresent(key ->model.addAttribute("message",key));
        error.ifPresent( e ->  model.addAttribute("error", e));
        return "login";
    }
}
