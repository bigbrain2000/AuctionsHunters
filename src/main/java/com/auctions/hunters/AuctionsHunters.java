package com.auctions.hunters;

import com.auctions.hunters.exceptions.EmailAlreadyExistsException;
import com.auctions.hunters.model.ConfirmationToken;
import com.auctions.hunters.model.Role;
import com.auctions.hunters.model.User;
import com.auctions.hunters.service.confirmationtoken.ConfirmationTokenService;
import com.auctions.hunters.service.role.RoleService;
import com.auctions.hunters.service.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.auctions.hunters.utils.DateUtils.getDateTime;

@SpringBootApplication
public class AuctionsHunters implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;
    private final ConfirmationTokenService confirmationTokenService;


    public AuctionsHunters(UserService userService,
                           RoleService roleService,
                           ConfirmationTokenService confirmationTokenService) {
        this.userService = userService;
        this.roleService = roleService;
        this.confirmationTokenService = confirmationTokenService;
    }

    public static void main(String[] args) {
        SpringApplication.run(AuctionsHunters.class, args);
    }

    @Override
    public void run(String... args) throws EmailAlreadyExistsException {
        insertPredefinedAdmin();
    }

    /**
     * Method used for inserting a predefined administrator account into the database at the service start.
     */
    private void insertPredefinedAdmin() throws EmailAlreadyExistsException {
        String adminData = "a";
        String adminPhoneNumber = "1234567890";
        String adminEmail = "auctionshunters@gmail.com";
        String adminCityAddress = "Caracal";
        OffsetDateTime now = getDateTime();

        if (!userService.isUserEmailAlreadyRegistered(adminEmail)) {

            Role role = Role.builder()
                    .name("ADMIN")
                    .creationDate(OffsetDateTime.from(now))
                    .build();
            roleService.save(role);

            Set<Role> set = new HashSet<>();
            set.add(role);

            User admin = new User(adminData, adminData, adminEmail, adminCityAddress, adminPhoneNumber, set);

            admin.setLocked(false);
            admin.setEnabled(true);
            userService.save(admin);

            ConfirmationToken adminConfirmationToken = ConfirmationToken.builder()
                    .token(adminData)
                    .tokenCreatedAt(now)
                    .tokenExpiresAt(now)
                    .tokenConfirmedAt(now)
                    .user(admin)
                    .build();

            confirmationTokenService.saveConfirmationToken(adminConfirmationToken);
        }
    }
}
