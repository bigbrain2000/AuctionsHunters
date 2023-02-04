package com.auctions.hunters.controller;

import com.auctions.hunters.exceptions.EmailAlreadyExistsException;
import com.auctions.hunters.exceptions.InvalidEmailException;
import com.auctions.hunters.exceptions.WeakPasswordException;
import com.auctions.hunters.model.User;
import com.auctions.hunters.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
@Validated
public class AuctionsHuntersController {

    private final UserService userService;

    public AuctionsHuntersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String getHomePage() {
        return "/index";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "/login";
    }

    @GetMapping("/login_error")
    public String getLoginErrorPage() {
        return "/login";
    }

    @GetMapping("/logout")
    public String getLogoutPage() {
        return "redirect:/login";
    }

    @GetMapping("/registration")
    public String geRegisterPage(@NotNull Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "/register";
    }

    @PostMapping("/registration")
    public String userRegister(@ModelAttribute @Valid User user) throws EmailAlreadyExistsException, InvalidEmailException, WeakPasswordException {
        userService.register(user);
        return "redirect:/index";
    }
}
