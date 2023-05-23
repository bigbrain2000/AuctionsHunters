package com.auctions.hunters.security;

import com.auctions.hunters.model.Role;
import com.auctions.hunters.model.User;
import com.auctions.hunters.util.RoleUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsTest implements RoleUtils {

    @Mock
    private User user;

    @InjectMocks
    private MyUserDetails uut;

    @Test
    void getAuthorities() {
        Role role = mockRole();
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        user.setRole(roleSet);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));
        when(user.getRole()).thenReturn(roleSet);

        Collection<? extends GrantedAuthority> grantedAuthorities = uut.getAuthorities();

        assertEquals(authorities, grantedAuthorities.stream().toList());
        verify(user, times(1)).getRole();
    }

    @Test
    void getPassword_validUserData_returnsPassword() {
        String expectedPassword = "password";
        when(user.getPassword()).thenReturn(expectedPassword);

        String actualPassword = uut.getPassword();

        assertEquals(expectedPassword, actualPassword);
        verify(user, times(1)).getPassword();
    }

    @Test
    void getUsername() {
        String expectedUsername = "Alex";
        when(user.getUsername()).thenReturn(expectedUsername);

        String actualUsername = uut.getUsername();

        assertEquals(expectedUsername, actualUsername);
        verify(user, times(1)).getUsername();
    }

    @Test
    void isAccountNonExpired() {
        assertTrue(uut.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked() {
        assertTrue(uut.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired() {
        assertTrue(uut.isCredentialsNonExpired());
    }

    @Test
    void isEnabled() {
        assertTrue(uut.isEnabled());
    }
}