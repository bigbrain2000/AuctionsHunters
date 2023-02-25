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
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.auctions.hunters.utils.DateUtils.DATE_TIME_PATTERN;
import static com.auctions.hunters.utils.DateUtils.getDateTime;
import static java.util.List.of;

/**
 * Factory methods for users which is implementing the {@link UserService} interface.
 */
public abstract class UserFactory implements UserService {

    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    final RoleService roleService;
    final ConfirmationTokenService confirmationTokenService;
    final EmailService emailService;

    private final OffsetDateTime NOW = getDateTime();

    protected static final Logger LOGGER = LoggerFactory.getLogger(UserFactory.class);

    protected UserFactory(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          RoleService roleService,
                          ConfirmationTokenService confirmationTokenService,
                          EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
    }

    public abstract String signUpUser(@NotNull User appUser) throws EmailAlreadyExistsException;

    /**
     * Creates a role entity and adds it in the database based on an input.
     *
     * @param roleName the role names parsed as input
     * @return the saved role
     */
    protected Role addRole(String roleName) {
        Role role = Role.builder()
                .name(roleName)
                .creationDate(NOW)
                .build();
        roleService.save(role);
        return role;
    }

    /**
     * Creates a user entity and adds it in the database based on the inputs.
     *
     * @param user the user that will be stored in the database
     * @param role the saved role that identifies the user
     */
    protected void addUser(User user, Role role) {
        String encodedPassword = passwordEncoder.bCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encodedPassword);

        //set users scopes as not validated
        user.setEnabled(false);
        user.setLocked(true);

        Set<Role> set = new HashSet<>();
        set.add(role);
        user.setRole(set);

        userRepository.save(user);
        LOGGER.debug("User {} inserted into the database", user);
    }

    /**
     * Creates a confirmation entity and adds it in the database based on the input.
     *
     * @param user the user that`s going to have the new confirmation token
     * @return the token
     */
    protected String addConfirmationToken(User user) {
        String token = UUID.randomUUID().toString();

        OffsetDateTime defaultDateTime = OffsetDateTime.parse("2000-01-01T20:20:20.200Z",
                DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(token)
                .tokenCreatedAt(NOW)
                .tokenExpiresAt(NOW.plusMinutes(30))
                .tokenConfirmedAt(defaultDateTime) //if the user did not validate his email, then it will have a default value into the database
                .user(user)
                .build();

        confirmationTokenService.saveConfirmationToken(confirmationToken);
        LOGGER.debug("Token {} for user {} inserted into the database", confirmationToken, user);

        return token;
    }

    @Override
    public List<User> findAll() {
        List<User> userList = userRepository.findAll();

        if (userList.isEmpty()) {
            LOGGER.debug("The user's list was empty and users could not be retrieved from the database.");
            return of();
        }

        LOGGER.debug("The user's list was not empty and users were successfully retrieved from the database.");
        return new ArrayList<>(userList);
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> {
            LOGGER.debug("Could not find the user by the id {} ", id);
            return new ResourceNotFoundException("User", "id", id);
        });
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            LOGGER.debug("Could not find the user identified by email {} ", email);
            return new ResourceNotFoundException("User", "email", email);
        });
    }

    @Override
    public User save(User user) {
        user.setPassword(passwordEncoder.bCryptPasswordEncoder().encode(user.getPassword()));
        LOGGER.debug("User {} saved in the database.", user);
        return userRepository.save(user);
    }

    @Override
    public User update(@NotNull User newUser, Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            LOGGER.debug("Could not find the user by the id {} ", id);
            return new ResourceNotFoundException("User", "id", id);
        });

        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        user.setEmail(newUser.getEmail());
        user.setCityAddress(newUser.getCityAddress());
        user.setPhoneNumber(newUser.getPhoneNumber());
        user.setRole(newUser.getRole());
        user.setCreditCardNumber(newUser.getCreditCardNumber());

        LOGGER.debug("Token successfully updated in the database");
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Integer id) {
        userRepository.findById(id).orElseThrow(() -> {
            LOGGER.debug("Token could not be deleted from the database");
            return new ResourceNotFoundException("Token", "id", id);
        });

        LOGGER.debug("Token successfully deleted from the database");
        userRepository.deleteById(id);
    }


    @Override
    public boolean isUserEmailAlreadyRegistered(String username) {
        boolean userIsFound = userRepository.findByEmail(username).isPresent();

        if (!userIsFound) {
            LOGGER.debug("User {} was not found by email", username);
        }

        return userIsFound;
    }

    /**
     * Check if a user email already exists.
     *
     * @param email - email of user
     * @throws EmailAlreadyExistsException - if a user with the same username already exists
     */
    public void checkIfEmailAlreadyExists(String email) throws EmailAlreadyExistsException {
        if (userRepository.findByEmail(email).isPresent()) {
            LOGGER.debug("User email {} already exists", email);
            throw new EmailAlreadyExistsException(email);
        }
    }

    @Override
    public void checkIfEmailIsValid(String email) throws InvalidEmailException {
        if (!isValidEmailAddress(email) || email.isEmpty()) {
            LOGGER.debug("User email {} is not valid", email);
            throw new InvalidEmailException(email);
        }
    }

    /**
     * Check if the email parsed as input is valid using {@link InternetAddress}.
     *
     * @param email provided email
     * @return true if the input is a valid email and false otherwise
     */
    private boolean isValidEmailAddress(String email) {

        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        }

        return result;
    }

    @Override
    public int enableUser(String email) {
        return userRepository.enableUser(email);
    }

    @Override
    public int unlockUser(String email) {
        return userRepository.unlockUser(email);
    }

    @Override
    public String register(@NotNull User newUser) throws InvalidEmailException, EmailAlreadyExistsException {

        checkIfEmailIsValid(newUser.getEmail());

        String token = signUpUser(
                new User(
                        newUser.getUsername(),
                        newUser.getPassword(),
                        newUser.getEmail(),
                        newUser.getCityAddress(),
                        newUser.getPhoneNumber(),
                        newUser.getRole(),
                        newUser.getCreditCardNumber()
                )
        );

        String link = "http://localhost:8080/confirm?token=" + token; //url for validating user account

        emailService.sendEmail(newUser.getEmail(), buildEmail(newUser.getUsername(), link));
        LOGGER.debug("Email for confirmation token was sent.");

        return token;
    }

    @Transactional
    @Override
    public void confirmToken(String token) {

        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> {
                    LOGGER.debug("Token was not found.");
                    throw new IllegalStateException("Token was not found");
                });

        if (confirmationToken.getTokenConfirmedAt().isBefore(confirmationToken.getTokenExpiresAt())) {
            confirmationTokenService.setConfirmedAt(token);
            LOGGER.debug("Token confirmed by user.");

            //set users scopes as validated
            enableUser(confirmationToken.getUser().getEmail());
            unlockUser(confirmationToken.getUser().getEmail());
            LOGGER.debug("User enabled his account.");
        }
    }

    @Contract(pure = true)
    private @NotNull String buildEmail(String name, String link) {

        return "<div style=\"width: 500px; margin: 0 auto; text-align: center; font-family: Arial, sans-serif; background-color: lightgray; padding: 40px; border-radius: 10px; box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);\">\n" +
                " <h1 style=\"margin-top: 50px; font-size: 36px; color: #01304A;\">Validare email</h1>\n" +
                " <p>Draga " + name + ",</p>\n" +
                " <p>Vă mulțumim că v-ați înscris la în aplicația noastră! Pentru a finaliza înregistrarea, trebuie să verificăm adresa dumneavoastră de email.</p>\n" +
                " <br>\n" +
                " <p>Apăsați pe butonul de mai jos pentru a valida emalailul.</p>\n" +
                " <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a  target=\"_blank\" href=\"" + link + "\">Activate Now</a> </p>" +
                " <p style=\"font-size: 18px; color: gray;\">\n" +
                " Verificându-vă adresa de e-mail, veți putea accesa toate caracteristicile și desciile aplicației noastre de licitații de mașini.</p>\n" +
                " <p style=\"font-size: 18px; margin-bottom: 20px; color: gray;\">\n" +
                " Vă mulțumim pentru timpul acordat!\n" +
                " </p>\n" +
                " <p style=\"font-size: 18px; margin-bottom: 20px; color: gray;\">Cele mai bune urări,</p>\n" +
                " <p style=\"font-size: 18px; margin-bottom: 20px; color: gray;\">echipa AuctionHunters.</p>\n" +
                "</p>" +
                "<blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\">" +
                "</div>";
    }
}

