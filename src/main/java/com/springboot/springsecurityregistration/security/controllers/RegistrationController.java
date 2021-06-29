package com.springboot.springsecurityregistration.security.controllers;

import com.springboot.springsecurityregistration.security.domain.PasswordResetToken;
import com.springboot.springsecurityregistration.security.domain.User;
import com.springboot.springsecurityregistration.security.domain.VerificationToken;
import com.springboot.springsecurityregistration.security.dto.PasswordDto;
import com.springboot.springsecurityregistration.security.exceptions.UserAlreadyExistException;
import com.springboot.springsecurityregistration.security.exceptions.UserNotFoundException;
import com.springboot.springsecurityregistration.security.registration.OnRegistrationCompleteEvent;
import com.springboot.springsecurityregistration.security.services.CreatePasswordResetLink;
import com.springboot.springsecurityregistration.security.services.UserService;
import com.springboot.springsecurityregistration.security.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * @author KMCruz
 * 6/20/2021
 */
@Controller
public class RegistrationController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messages;
    private final CreatePasswordResetLink resetLink;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserService userService, ApplicationEventPublisher eventPublisher,
                                  @Qualifier("messageSource") MessageSource messages, CreatePasswordResetLink resetLink,
                                  PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.messages = messages;
        this.resetLink = resetLink;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/user/registration")
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("userDto", userDto);
        return "registration";
    }

    @PostMapping("/user/registration")
    public ModelAndView registerUserAccount (HttpServletRequest request, @Valid UserDto userDto,
                                       Errors errors) {
        LOGGER.debug("Registering User Account: ", userDto);

        if(errors.hasErrors()){
            ModelAndView mav = new ModelAndView("registration");
            List<String> errorList = getErrors(errors);
            mav.addObject("message",errorList);
            return mav;
        }
        User registered;
        try{
            registered = userService.registerNewUserAccount(userDto);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                    request.getLocale(), appUrl));

        }catch (UserAlreadyExistException uaeEx){
            LOGGER.info("User Already exists");
            ModelAndView mav = new ModelAndView("registration","user",userDto);
            String message = messages.getMessage("message.regError",null,request.getLocale());
            mav.addObject("message",message);
            return mav;
        } catch (RuntimeException ex) {
            return new ModelAndView("emailError","user",userDto);
        }
        VerificationToken token = userService.getVerificationTokenByUser(registered);
        LOGGER.info(token.getToken());
        return new ModelAndView("successRegister","token",token.getToken());

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
            model.addAttribute("expired",true);
            model.addAttribute("token",token);
            model.addAttribute("message", message);
            return new ModelAndView("redirect:/badUser",model);
        }

        user.setEnabled(true);
        userService.deleteVerificationToken(verificationToken);
        userService.saveRegisteredUser(user);
        String message = messages.getMessage("message.accountVerified",null,locale);
        model.addAttribute("message",message);
        return new ModelAndView("redirect:/login",model); /*"redirect:/login?message=" + message;*/
    }

    @GetMapping("/login")
    public ModelAndView login(HttpServletRequest request,  ModelMap model, @RequestParam Optional<String> message, @RequestParam Optional<String> error) {
        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        message.ifPresent(key ->model.addAttribute("message",key));
        error.ifPresent( e -> model.addAttribute("error", e));
        return new ModelAndView("login",model);
    }

    @GetMapping("/badUser")
    public ModelAndView handlerBadUser(HttpServletRequest request, ModelMap model, @RequestParam Optional<String> message,
                                 @RequestParam Optional<String> expired, @RequestParam Optional<String> token) {

        message.ifPresent( key -> model.addAttribute("message", key));
        expired.ifPresent( e -> model.addAttribute("expired", e));
        token.ifPresent( t -> model.addAttribute("token", t));

        return new ModelAndView("badUser",model);
    }

    @GetMapping("/user/resendRegistrationToken")
    public ModelAndView resendRegistrationToken(HttpServletRequest request, ModelMap model, @RequestParam Optional<String> token){
        User registeredUser = userService.getUserByVerificationToken(token.get());
        String appUrl = request.getContextPath();

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registeredUser,request.getLocale(),appUrl));
        LOGGER.debug("User: ",registeredUser);
        model.addAttribute(token);
        return new ModelAndView("successRegister",model);
    }
    @GetMapping("/login/resetPassword")
    public String showForgetPasswordPage(Model model){
        User user = new User();
        model.addAttribute("user",user);
        return "forgetPassword";
    }

    @PostMapping("/login/resetPassword")
    public ModelAndView processForgetPassword(HttpServletRequest request, ModelMap model,User user){
        Locale locale = request.getLocale();
        try {
            User searchedUser = userService.findUserByEmail(user.getEmail());
            String appUrl = request.getContextPath();
            resetLink.mail(searchedUser, locale, appUrl);
        }catch (UserNotFoundException notFoundException){
            LOGGER.info("User not found");
            String error  = messages.getMessage("message.userNotFound",null,locale);
            model.addAttribute("error",error);
            return new ModelAndView("redirect:/login",model);
        }

        String message = messages.getMessage("message.resetPasswordEmail",null,locale);
        model.addAttribute("message",message);
        return new ModelAndView("redirect:/login",model);
    }

    @GetMapping("/login/changePassword")
    public ModelAndView showChangePasswordView(HttpServletRequest request,ModelMap model, @RequestParam String token) {
        PasswordResetToken passwordResetToken = userService.getPasswordResetTokenByResetToken(token);
        Locale locale = request.getLocale();

        if(passwordResetToken==null){
            String error = messages.getMessage("auth.message.invalidToken",null,locale);
            model.addAttribute("error",error);
            return new ModelAndView("redirect:/login",model);
        }

        Calendar cal = Calendar.getInstance();
        Date expiryDate = passwordResetToken.getExpiryDate();
        long expiryMinutesRemaining = expiryDate.getTime() - cal.getTime().getTime();
        boolean isExpired = expiryMinutesRemaining<=0;
        if(isExpired){
            String error = messages.getMessage("message.passwordResetLinkExpired",null,locale);
            model.addAttribute("error",error);
            return new ModelAndView("redirect:/login",model);
        }

        model.addAttribute("token",token);
        return new ModelAndView("/changePassword",model);
    }

    @PostMapping("/login/changePassword")
    public ModelAndView processChangePassword(HttpServletRequest request,@Valid PasswordDto passwordDto,
                                              Errors errors, ModelMap model, @RequestParam String token){
        PasswordResetToken resetToken = userService.getPasswordResetTokenByResetToken(token);
        Locale locale = request.getLocale();
        User user = resetToken.getUser();

        if(errors.hasErrors()){
            ModelAndView mav = new ModelAndView("changePassword");
            List<String> errorList = getErrors(errors);
            mav.addObject("error",errorList);
            return mav;
        }

        if(!passwordDto.getPassword().equals(passwordDto.getMatchingPassword())) {
            String error = messages.getMessage("PasswordMatches.user",null,locale);
            model.addAttribute("error",error);
            return new ModelAndView("changePassword",model);
        }
        user.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
        userService.deletePasswordResetToken(resetToken);
        userService.saveRegisteredUser(user);
        String message = messages.getMessage("message.resetPasswordSuc",null,locale);
        model.addAttribute("message",message);
        return new ModelAndView("redirect:/login",model);
    }


    private List<String> getErrors(Errors errors) {
        List<ObjectError> listObjectError = errors.getAllErrors();
        List<String> errorList = new ArrayList<>();
        listObjectError.stream()
                .forEach(e -> errorList.add(e.getDefaultMessage()));
        return errorList;
    }
}
