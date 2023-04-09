package com.auctions.hunters.controller;

import com.auctions.hunters.exceptions.EmailAlreadyExistsException;
import com.auctions.hunters.exceptions.InvalidEmailException;
import com.auctions.hunters.model.User;
import com.auctions.hunters.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@Controller
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String getHomePage() {
        return "/homepage";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "/login";
    }

    @GetMapping("/login_error")
    public String getLoginErrorPage() {
        return "/loginError";
    }

    @GetMapping("/logout")
    public String getLogoutPage() {
        return "redirect:/login";
    }

    @GetMapping("/register/user")
    public String getSellerRegisterPage(@NotNull Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "/user_register";
    }

    @PostMapping("/register/user")
    public String registerSeller(@ModelAttribute @Valid User user) throws EmailAlreadyExistsException, InvalidEmailException {
        userService.register(user);
        return "redirect:/login";
    }

    @GetMapping(path = "/confirm")
    public String validateEmail(@RequestParam("token") String token) {
        userService.confirmToken(token);
        return "/email_validation";
    }
}
