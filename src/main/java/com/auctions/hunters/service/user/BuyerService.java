package com.auctions.hunters.service.user;

import com.auctions.hunters.exceptions.EmailAlreadyExistsException;
import com.auctions.hunters.model.Role;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.UserRepository;
import com.auctions.hunters.security.PasswordEncoder;
import com.auctions.hunters.service.confirmationtoken.ConfirmationTokenService;
import com.auctions.hunters.service.email.EmailService;
import com.auctions.hunters.service.role.RoleService;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * Subclass for {@link UserFactory} used for defining the methods that a buyer can use.
 */
@Service
public class BuyerService extends UserFactory {


    protected BuyerService(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleService roleService,
                           ConfirmationTokenService confirmationTokenService,
                           EmailService emailService) {
        super(userRepository, passwordEncoder, roleService, confirmationTokenService, emailService);
    }

    /**
     * Save a user alongside with the role and confirmation token in the database and returns the confirmation token
     * that`s going to be used in register method from {@link UserFactory}.
     *
     * @param user the user that`s going to be registered in the application
     * @return the confirmation token for the user
     */
    @Override
    public String signUpUser(@NotNull User user) throws EmailAlreadyExistsException {

        checkIfEmailAlreadyExists(user.getEmail());

        Role role = addRole("BUYER");
        addUser(user, role);

        return addConfirmationToken(user);
    }
}
