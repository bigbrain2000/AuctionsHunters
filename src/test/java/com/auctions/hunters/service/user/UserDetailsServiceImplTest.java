package com.auctions.hunters.service.user;

import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl uut;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
    }

    @Test
    void loadUserByUsername_foundUser_returnsSuccess() {
        final String expectedUsername = "Marius";
        user.setUsername(expectedUsername);
        when(userRepository.findByUsername(expectedUsername)).thenReturn(Optional.of(user));

        UserDetails userDetails = uut.loadUserByUsername(expectedUsername);

        assertEquals(expectedUsername, userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> uut.loadUserByUsername("Marius"));

        verify(userRepository, times(1)).findByUsername(anyString());
    }
}