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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.auctions.hunters.utils.DateUtils.DATE_TIME_PATTERN;
import static com.auctions.hunters.utils.DateUtils.getDateTime;
import static java.lang.Boolean.FALSE;
import static java.util.List.of;

/**
 * Service class used for managing user business logic and implementing the {@link UserService} interface.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;

    private static final OffsetDateTime NOW = getDateTime();
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository,
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

    /**
     * Get a list with all the users from the database,
     *
     * @return a list with all the users
     */
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

    /**
     * Find a specific {@link User} based on the username.
     *
     * @param username username of {@link User}
     * @return found {@link User}
     * @throws ResourceNotFoundException if the {@link User} was not find by the given username
     */
    @Override
    public User findByUsername(@NotBlank String username) throws ResourceNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> {
            LOGGER.debug("Could not find the user identified by username {} ", username);
            throw new ResourceNotFoundException("User", "username", username);
        });
    }

    /**
     * Save a {@link User} in the database. By default, a {@link User} reminder to receive emails for an auction is FALSE.
     *
     * @param user the {@link User} entity to be saved in the database
     * @return saved {@link User}
     */
    @Override
    public User save(@NotNull User user) {
        user.setPassword(passwordEncoder.bCryptPasswordEncoder().encode(user.getPassword()));
        user.setReminder(false); //by default a user reminder to receive emails for an auction is FALSE
        LOGGER.debug("User {} saved in the database.", user);
        return userRepository.save(user);
    }

    /**
     * Update all information for a specific {@link User} entity.
     *
     * @param newUser the user who will be persisted
     * @return updated {@link User} object
     */
    @Override
    public User update(@NotNull User newUser) {
        String username = newUser.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            LOGGER.debug("Could not find the user by the username {} ", username);
            throw new ResourceNotFoundException("User", "username", username);
        });

        user.setRole(newUser.getRole());
        user.setCarList(newUser.getCarList());
        user.setUsername(username);
        user.setPassword(newUser.getPassword());
        user.setEmail(newUser.getEmail());
        user.setCityAddress(newUser.getCityAddress());
        user.setPhoneNumber(newUser.getPhoneNumber());
        user.setLocked(newUser.getLocked());
        user.setEnabled(newUser.getEnabled());
        user.setReminder(newUser.getReminder());

        LOGGER.debug("User successfully updated in the database");
        return userRepository.save(user);
    }

    /**
     * Update the status of a {@link User} reminder for receiving emails.
     *
     * @param id     persisted {@link User} id
     * @param status the reminder status of a {@link User} reminder emails
     * @throws ResourceNotFoundException if the {@link User} was not find by the id
     */
    public void updateReminder(Integer id, boolean status) throws ResourceNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> {
            LOGGER.debug(String.format("User with the id:%s was not found!", id));
            throw new ResourceNotFoundException("User", "id", id);
        });

        user.setReminder(status);

        userRepository.save(user);
    }

    /**
     * Check if the {@link User} email already exists.
     *
     * @param email the email of {@link User}
     * @return true if the email already exist, otherwise false
     */
    @Override
    public boolean isUserEmailAlreadyRegistered(@NotBlank String email) {
        boolean isUserFound = userRepository.findByEmail(email).isPresent();

        if (!isUserFound) {
            LOGGER.debug("User {} was not found by email", email);
        }

        LOGGER.debug("User {} was found by email", email);
        return isUserFound;
    }

    /**
     * Based on entered data, a new user will be saved in the DB and a token
     * is generated for validating email within the next 30 minutes from registering.
     *
     * @param newUser the user who register in the app
     * @return a String which contains the unique token generated for the registered user
     * @throws InvalidEmailException       if a user email is invalid
     * @throws EmailAlreadyExistsException if a user with the same email already exists
     */
    //local
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
                        FALSE
                )
        );

        String link = "http://localhost:5000/confirm?token=" + token; //url for validating user account

        sendRegistrationEmail(newUser, link);

        return token;
    }

    //AWS
//    public String register(@NotNull User newUser) throws InvalidEmailException, EmailAlreadyExistsException {
//
//        checkIfEmailIsValid(newUser.getEmail());
//
//        String token = signUpUser(
//                new User(
//                        newUser.getUsername(),
//                        newUser.getPassword(),
//                        newUser.getEmail(),
//                        newUser.getCityAddress(),
//                        newUser.getPhoneNumber(),
//                        newUser.getRole(),
//                        FALSE
//                )
//        );
//
//        String link = "http://demo-env-1.eba-tim3wwmf.us-east-1.elasticbeanstalk.com/confirm?token=" + token; //url for validating user account
//
//        sendRegistrationEmail(newUser, link);
//
//        return token;
//    }

    /**
     * Verify is the user email is valid
     *
     * @param email email of user
     * @throws InvalidEmailException if a user email is invalid
     */
    private void checkIfEmailIsValid(@NotBlank String email) throws InvalidEmailException {
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
    private boolean isValidEmailAddress(@NotBlank String email) {

        boolean result = true;
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            result = false;
        }

        return result;
    }

    private String signUpUser(@NotNull User user) throws EmailAlreadyExistsException {

        checkIfEmailAlreadyExists(user.getEmail());

        Role role = addRole("USER");
        addUser(user, role);

        return addConfirmationToken(user);
    }

    /**
     * Check if a user email already exists.
     *
     * @param email email of user
     * @throws EmailAlreadyExistsException - if a user with the same username already exists
     */
    private void checkIfEmailAlreadyExists(@NotBlank String email) throws EmailAlreadyExistsException {
        if (userRepository.findByEmail(email).isPresent()) {
            LOGGER.debug("User email {} already exists", email);
            throw new EmailAlreadyExistsException(email);
        }
    }

    /**
     * Creates a role entity and adds it in the database based on an input.
     *
     * @param roleName the role names parsed as input
     * @return the saved role
     */
    private Role addRole(@NotBlank String roleName) {
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
    private void addUser(@NotNull User user, @NotNull Role role) {
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
    private String addConfirmationToken(@NotNull User user) {
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

    /**
     * Send an email for the given {@link User} to inform about his registration in the application.
     *
     * @param newUser the {@link User} that was registered in the application
     * @param link    the link where the {@link User} can validate his registration
     */
    private void sendRegistrationEmail(@NotNull User newUser, @NotBlank String link) {
        String emailSubject = "Bine ați venit la Vânătorii de licitații: Verificați adresa dvs. de e-mail pentru a începe";
        emailService.sendEmail(newUser.getEmail(), emailSubject, buildEmail(newUser.getUsername(), link));

        LOGGER.debug("Email for confirmation token was sent.");
    }

    /**
     * After the email is confirmed, the token will be set as confirmed and the {@link User} account will be enabled.
     *
     * @param token the unique generated token for a new user
     */
    @Transactional
    @Override
    public void confirmToken(@NotBlank String token) {

        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> {
                    LOGGER.debug("Token was not found.");
                    throw new ResourceNotFoundException("Token", "token", token);
                });

        if (confirmationToken.getTokenConfirmedAt().isBefore(confirmationToken.getTokenExpiresAt())) {
            confirmationTokenService.setConfirmedAt(token);
            LOGGER.debug("Token confirmed by user.");

            //set users scopes as validated
            userRepository.enableUser(confirmationToken.getUser().getEmail());
            userRepository.unlockUser(confirmationToken.getUser().getEmail());
            LOGGER.debug("User enabled his account.");
        }
    }

    /**
     * Get the username of the logged {@link User}.
     *
     * @return the logged username if he is logged in the app
     */
    @Override
    public String getLoggedUsername() {
        //load the current security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //if the user is not authenticated return null
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getName();
    }

    /**
     * Email template that the {@link User} will receive when he will register in the application.
     *
     * @param name the {@link User} that was registered in the application
     * @param link the link where the {@link User} can validate his registration
     * @return a String containing the email template
     */
    @Contract(pure = true)
    private @NotNull String buildEmail(@NotBlank String name, @NotBlank String link) {

        return "<div style=\"width: 500px; margin: 0 auto; text-align: center; font-family: Arial, sans-serif; background-color: lightgray; padding: 40px; border-radius: 10px; box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);\">\n" +
                " <h1 style=\"margin-top: 50px; font-size: 36px; color: #01304A;\">Validare email</h1>\n" +
                " <p>Draga " + name + ",</p>\n" +
                " <p>Vă mulțumim că v-ați înscris la în aplicația noastră! Pentru a finaliza înregistrarea, trebuie să verificăm adresa dumneavoastră de e-mail.</p>\n" +
                " <br>\n" +
                " <p>Apăsați pe butonul de mai jos pentru a valida e-malailul.</p>\n" +
                " <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a  target=\"_blank\" href=\"" + link + "\">Actiează acum</a> </p>" +
                " </p>\n" +
                " <p style=\"font-size: 18px; margin-bottom: 20px; color: gray;\">Cele mai bune urări,</p>\n" +
                " <p style=\"font-size: 18px; margin-bottom: 20px; color: gray;\">echipa Vânătorii de Licitații.</p>\n" +
                "</p>" +
                "</div>";
    }
}

