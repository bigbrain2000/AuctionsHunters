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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class AuctionsHunters implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;
    private final ConfirmationTokenService confirmationTokenService;

    public AuctionsHunters(UserService userService, RoleService roleService, ConfirmationTokenService confirmationTokenService) {
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
     *
     * @throws EmailAlreadyExistsException exception thrown if the email already exists in the database
     */
    private void insertPredefinedAdmin() throws EmailAlreadyExistsException {
        String adminData = "admin";
        String adminPhoneNumber = "1234567890";
        String adminEmail = "auctionshunters@gmail.com";
        String adminCityAddress = "Caracal";
        String adminCreditCardNumber = "1234567890123456";

        if (!userService.isUserEmailAlreadyRegistered(adminEmail)) {

            Role role = new Role("ADMIN");
            roleService.save(role);
            Set<Role> set = new HashSet<>();
            set.add(role);

            User admin = new User(adminData, adminData, adminEmail, adminCityAddress,
                    adminPhoneNumber, set, adminCreditCardNumber);

            admin.setLocked(false);
            admin.setEnabled(true);

            userService.save(admin);

            ConfirmationToken adminConfirmationToken = new ConfirmationToken(adminData,
                    LocalDateTime.of(LocalDate.of(2022, 2, 2), LocalTime.of(10, 11, 19)),
                    LocalDateTime.of(LocalDate.of(2022, 2, 2), LocalTime.of(10, 11, 19)),
                    LocalDateTime.of(LocalDate.of(2022, 2, 2), LocalTime.of(10, 11, 19)),
                    admin);

            confirmationTokenService.saveConfirmationToken(adminConfirmationToken);
        }
    }
}
