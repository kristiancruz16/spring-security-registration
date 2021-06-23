package com.springboot.springsecurityregistration.security.controllers;

import com.springboot.springsecurityregistration.security.domain.User;
import com.springboot.springsecurityregistration.security.domain.VerificationToken;
import com.springboot.springsecurityregistration.security.exceptions.UserAlreadyExistException;
import com.springboot.springsecurityregistration.security.registration.OnRegistrationCompleteEvent;
import com.springboot.springsecurityregistration.security.registration.SampleEvent;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

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
        return "registration";
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
            eventPublisher.publishEvent(new SampleEvent("Hello Sample Application Event"));
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
    public ModelAndView confirmRegistration(HttpServletRequest request, ModelMap model, @RequestParam("token") String token) {
        Locale locale = request.getLocale();

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = messages.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message",message);
            return new ModelAndView("redirect:/badUser",model);
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String message = messages.getMessage("auth.message.expired", null, locale);
            model.addAttribute("message", message);
            return new ModelAndView("redirect:/badUser",model);
        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        String message = messages.getMessage("message.accountVerified",null,locale);
        model.addAttribute("message",message);
        return new ModelAndView("redirect:/login",model); /*"redirect:/login?message=" + message;*/
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request,  Model model, @RequestParam("message")  Optional<String> messageKey, @RequestParam("error" )  Optional<String> error) {
        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        messageKey.ifPresent(key ->model.addAttribute("message",key));
        error.ifPresent( e -> model.addAttribute("error", e));
        return "login";
    }

    @GetMapping("/badUser")
    public String handlerBadUser(HttpServletRequest request, ModelMap model, @RequestParam Optional<String> message,
                                 @RequestParam Optional<String> expired, @RequestParam Optional<String> token) {

        message.ifPresent( key -> model.addAttribute("message", key));
        expired.ifPresent( e -> model.addAttribute("expired", e));
        token.ifPresent( t -> model.addAttribute("token", t));

        return "badUser";
    }
}
