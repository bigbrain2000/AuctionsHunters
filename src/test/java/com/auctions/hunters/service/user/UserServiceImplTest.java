package com.auctions.hunters.service.user;

import com.auctions.hunters.exceptions.EmailAlreadyExistsException;
import com.auctions.hunters.exceptions.InvalidEmailException;
import com.auctions.hunters.exceptions.ResourceNotFoundException;
import com.auctions.hunters.model.ConfirmationToken;
import com.auctions.hunters.model.Role;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.UserRepository;
import com.auctions.hunters.security.PasswordEncoder;
import com.auctions.hunters.service.confirmationtoken.ConfirmationTokenService;
import com.auctions.hunters.service.email.EmailService;
import com.auctions.hunters.service.role.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.auctions.hunters.util.RoleUtils.NOW;
import static com.auctions.hunters.utils.DateUtils.getDateTime;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private EmailService emailService;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserService uut;

    private User user;

    private ConfirmationToken confirmationToken;

    @BeforeEach
    void setUp() {
        openMocks(this);
        uut = spy(new UserServiceImpl(userRepository, passwordEncoder, roleService,
                confirmationTokenService, emailService));

        user = new User();
        user.setId(1);
        user.setUsername("Alex");
        user.setPassword("password");
        user.setReminder(FALSE);
        user.setEmail("alex@yahoo.com");

        confirmationToken = new ConfirmationToken();
        confirmationToken.setUser(user);
    }

    @Test
    void findAll_returnsPopulatedUserList() {
        List<User> expectedUserList = List.of(user);
        when(userRepository.findAll()).thenReturn(expectedUserList);

        List<User> actualUserList = uut.findAll();

        assertEquals(expectedUserList, actualUserList);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findAll_returnsEmptyList() {
        List<User> expectedUserList = new ArrayList<>();
        when(userRepository.findAll()).thenReturn(expectedUserList);

        List<User> actualUserList = uut.findAll();

        assertTrue(actualUserList.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findByUsername_userExists_returnsUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(user));

        User actualUser = uut.findByUsername(user.getUsername());

        assertEquals(user, actualUser);
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void findByUsername_userDoesNotExist_throwsResourceNotFoundException() {
        String nonExistentUsername = "nonExistentUsername";
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> uut.findByUsername(nonExistentUsername));
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void save_validUserData_returnsSavedUser() {
        String encryptedPassword = "encryptedPassword";
        when(passwordEncoder.bCryptPasswordEncoder()).thenReturn(bCryptPasswordEncoder);
        when(passwordEncoder.bCryptPasswordEncoder().encode(user.getPassword())).thenReturn(encryptedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User actualUser = uut.save(user);

        assertEquals(encryptedPassword, actualUser.getPassword());
        verify(passwordEncoder, times(2)).bCryptPasswordEncoder();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void update_notFoundUser_throwsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> uut.update(user));

        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void update_foundUser_returnsSuccess() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User actualUser = uut.update(user);

        assertNotNull(actualUser);
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void updateReminder_userExists_emailsReminderIsUpdated() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        uut.updateReminder(user.getId(), TRUE);

        assertEquals(TRUE, user.getReminder());
        verify(userRepository, times(1)).findById(anyInt());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateReminder_userDoesNotExist_throwsException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> uut.updateReminder(eq(anyInt()), TRUE));
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    void isUserEmailAlreadyRegistered_existingEmail_returnsTrue() throws EmailAlreadyExistsException {
        final String expectedUserEmail = "alex@yahoo.com";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));

        boolean isUserEmailAlreadyRegistered = uut.isUserEmailAlreadyRegistered(expectedUserEmail);

        assertTrue(isUserEmailAlreadyRegistered);
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void getLoggedUsername_authenticatedUser_returnsUsername() {
        //authenticate a user using Authentication
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String actualUsername = uut.getLoggedUsername();

        assertEquals(user.getUsername(), actualUsername);
    }

    @Test
    void getLoggedUsername_notAuthenticatedUser_returnsUsername() {
        SecurityContextHolder.clearContext();
        assertNull(uut.getLoggedUsername());
    }

    @Test
    void register_validEmail_returnsSuccess() throws EmailAlreadyExistsException, InvalidEmailException {
        Role role = Role.builder()
                .name("USER")
                .creationDate(NOW)
                .build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(roleService.save(any(Role.class))).thenReturn(role);
        when(passwordEncoder.bCryptPasswordEncoder()).thenReturn(bCryptPasswordEncoder);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(user.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(confirmationTokenService.saveConfirmationToken(any(ConfirmationToken.class)))
                .thenReturn(confirmationToken);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        String registerToken = uut.register(user);

        assertNotNull(registerToken);
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(roleService, times(1)).save(any(Role.class));
        verify(passwordEncoder, times(1)).bCryptPasswordEncoder();
        verify(userRepository, times(1)).save(any(User.class));
        verify(confirmationTokenService, times(1)).saveConfirmationToken(any(ConfirmationToken.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void register_existingEmail_throwsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistsException.class, () -> uut.register(user));
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void register_existingConfirmationToken_throwsException() {
        Role role = Role.builder()
                .name("USER")
                .creationDate(NOW)
                .build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(roleService.save(any(Role.class))).thenReturn(role);
        when(passwordEncoder.bCryptPasswordEncoder()).thenReturn(bCryptPasswordEncoder);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(user.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(confirmationTokenService.saveConfirmationToken(any(ConfirmationToken.class)))
                .thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> uut.register(user));

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(roleService, times(1)).save(any(Role.class));
        verify(passwordEncoder, times(1)).bCryptPasswordEncoder();
        verify(userRepository, times(1)).save(any(User.class));
        verify(confirmationTokenService, times(1)).saveConfirmationToken(any(ConfirmationToken.class));
    }

    @Test
    void confirmToken_userConfirmsHisEmail_returnsSuccess() {
        User user = new User();
        user.setEmail("alex@yahoo.com");
        OffsetDateTime now = getDateTime();
        String token = "token";
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .tokenCreatedAt(now)
                .tokenExpiresAt(now.plusMinutes(30))
                .tokenConfirmedAt(now.plusMinutes(15))
                .user(user)
                .build();
        when(confirmationTokenService.getToken(anyString())).thenReturn(Optional.ofNullable(confirmationToken));
        doNothing().when(confirmationTokenService).setConfirmedAt(token);
        when(userRepository.enableUser(anyString())).thenReturn(1);
        when(userRepository.unlockUser(anyString())).thenReturn(1);

        uut.confirmToken(token);

        verify(confirmationTokenService, times(1)).getToken(anyString());
        verify(userRepository, times(1)).enableUser(anyString());
        verify(userRepository, times(1)).unlockUser(anyString());
    }

    @Test
    void confirmToken_userDoesNotConfirmsHisEmailInTime_t() {
        User user = new User();
        user.setEmail("alex@yahoo.com");
        OffsetDateTime now = getDateTime();
        String token = "token";
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .tokenCreatedAt(now)
                .tokenExpiresAt(now.plusMinutes(30))
                .tokenConfirmedAt(now.plusMinutes(15))
                .user(user)
                .build();
        when(confirmationTokenService.getToken(anyString())).thenReturn(Optional.ofNullable(confirmationToken));
        doNothing().when(confirmationTokenService).setConfirmedAt(token);
        when(userRepository.enableUser(anyString())).thenReturn(1);
        when(userRepository.unlockUser(anyString())).thenReturn(1);

        uut.confirmToken(token);

        verify(confirmationTokenService, times(1)).getToken(anyString());
        verify(userRepository, times(1)).enableUser(anyString());
        verify(userRepository, times(1)).unlockUser(anyString());
    }
}