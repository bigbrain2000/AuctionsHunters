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
 * Subclass for {@link UserFactory} used for defining the methods that a seller can use.
 */
@Service
public class SellerService extends UserFactory {


    protected SellerService(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            RoleService roleService,
                            ConfirmationTokenService confirmationTokenService,
                            EmailService emailService) {
        super(userRepository, passwordEncoder, roleService, confirmationTokenService, emailService);
    }

    @Override
    public String signUpUser(@NotNull User user) throws EmailAlreadyExistsException {

        checkIfEmailAlreadyExists(user.getEmail());

        Role role = addRole("SELLER");
        addUser(user, role);

        return addConfirmationToken(user);
    }
}
