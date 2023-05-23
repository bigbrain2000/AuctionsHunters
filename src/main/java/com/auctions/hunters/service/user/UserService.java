package com.auctions.hunters.service.user;

import com.auctions.hunters.exceptions.EmailAlreadyExistsException;
import com.auctions.hunters.exceptions.InvalidEmailException;
import com.auctions.hunters.exceptions.ResourceNotFoundException;
import com.auctions.hunters.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Interface used for declaring the methods signature that can be performed with a {@link User} entity.
 */
public interface UserService {

    /**
     * Get a list with all the users from the database,
     *
     * @return a list with all the users
     */
    List<User> findAll();

    /**
     * Find a specific {@link User} based on the username.
     *
     * @param username username of {@link User}
     * @return found {@link User}
     * @throws ResourceNotFoundException if the {@link User} was not find by the given username
     */
    User findByUsername(@NotBlank String username) throws ResourceNotFoundException;

    /**
     * Save a {@link User} in the database. By default, a {@link User} reminder to receive emails for an auction is FALSE.
     *
     * @param user the {@link User} entity to be saved in the database
     * @return saved {@link User}
     */
    User save(@NotNull User user);

    /**
     * Update all information for a specific {@link User} entity.
     *
     * @param newUser  the user who will be persisted
     * @return updated {@link User} object
     */
     User update(@NotNull User newUser);

    /**
     * Update the status of a {@link User} reminder for receiving emails.
     *
     * @param id     persisted {@link User} id
     * @param status the reminder status of a {@link User} reminder emails
     */
    void updateReminder(Integer id, boolean status) throws ResourceNotFoundException;

    /**
     * Check if the {@link User} email already exists.
     *
     * @param email the email of {@link User}
     * @return true if the email already exist, otherwise false
     */
    boolean isUserEmailAlreadyRegistered(@NotBlank String email) throws EmailAlreadyExistsException;

    /**
     * Based on entered data, a new user will be saved in the DB and a token
     * is generated for validating email within the next 30 minutes from registering.
     *
     * @param newUser the user who register in the app
     * @return a String which contains the unique token generated for the registered user
     * @throws InvalidEmailException       if a user email is invalid
     * @throws EmailAlreadyExistsException if a user with the same email already exists
     */
    String register(@NotNull User newUser) throws InvalidEmailException, EmailAlreadyExistsException;

    /**
     * After the email is confirmed, the token will be set as confirmed and the {@link User} account will be enabled.
     *
     * @param token the unique generated token for a new user
     */
    void confirmToken(@NotBlank String token);

    /**
     * Get the username of the logged {@link User}.
     *
     * @return the logged username if he is logged in the app
     */
    String getLoggedUsername();
}
