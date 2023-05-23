package com.auctions.hunters.controller;

import com.auctions.hunters.exceptions.EmailAlreadyExistsException;
import com.auctions.hunters.exceptions.InvalidEmailException;
import com.auctions.hunters.model.User;
import com.auctions.hunters.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private RegistrationController uut;

    @Mock
    private User user;
    @Mock
    private Model modelAtr;

    @Test
    void getHomePage() {
        assertEquals("/homepage", uut.getHomePage());
    }

    @Test
    void getLoginPage() {
        assertEquals("/login", uut.getLoginPage());
    }

    @Test
    void getLoginErrorPage() {
        assertEquals("/loginError", uut.getLoginErrorPage());
    }

    @Test
    void getLogoutPage() {
        assertEquals("redirect:/login", uut.getLogoutPage());
    }

    @Test
    void getSellerRegisterPage() {
        String result = uut.getSellerRegisterPage(modelAtr);

        assertEquals("/user_register", result);
    }

    @Test
    void registerSeller() throws EmailAlreadyExistsException, InvalidEmailException {
        when(userService.register(user)).thenReturn("token");

        String result = uut.registerSeller(user);

        assertEquals("redirect:/login", result);
        verify(userService, times(1)).register(user);
    }

    @Test
    void validateEmail() {
        final String token = "token";
        doNothing().when(userService).confirmToken(token);

        String result = uut.validateEmail(token);

        assertEquals("/email_validation", result);
        verify(userService, times(1)).confirmToken(token);
    }
}