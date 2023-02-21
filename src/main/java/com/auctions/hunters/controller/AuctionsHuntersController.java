package com.auctions.hunters.controller;

import com.auctions.hunters.exceptions.EmailAlreadyExistsException;
import com.auctions.hunters.exceptions.InvalidEmailException;
import com.auctions.hunters.exceptions.WeakPasswordException;
import com.auctions.hunters.model.User;
import com.auctions.hunters.service.user.BuyerService;
import com.auctions.hunters.service.user.SellerService;
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
public class AuctionsHuntersController {

    private final BuyerService buyerService;
    private final SellerService sellerService;

    public AuctionsHuntersController(BuyerService buyerService, SellerService sellerService) {
        this.buyerService = buyerService;
        this.sellerService = sellerService;
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
        return "/loginError";
    }

    @GetMapping("/logout")
    public String getLogoutPage() {
        return "redirect:/login";
    }

    @GetMapping("/buyer/register")
    public String getBuyerRegisterPage(@NotNull Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "/buyer_register";
    }

    @PostMapping("/buyer/register")
    public String registerBuyer(@ModelAttribute @Valid User user) throws EmailAlreadyExistsException, InvalidEmailException, WeakPasswordException {
        buyerService.register(user);
        return "redirect:/login";
    }

    @GetMapping("/seller/register")
    public String getSellerRegisterPage(@NotNull Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "/seller_register";
    }

    @PostMapping("/seller/register")
    public String registerSeller(@ModelAttribute @Valid User user) throws EmailAlreadyExistsException, InvalidEmailException, WeakPasswordException {
        sellerService.register(user);
        return "redirect:/login";
    }

    @GetMapping(path = "/confirm")
    public String confirm(@RequestParam("token") String token) {
        buyerService.confirmToken(token);
        return "/email_validation";
    }
}
