package com.auctions.hunters;

import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.ConfirmationToken;
import com.auctions.hunters.model.Role;
import com.auctions.hunters.model.User;
import com.auctions.hunters.model.enums.CategoryType;
import com.auctions.hunters.model.enums.FuelType;
import com.auctions.hunters.model.enums.PollutionStandard;
import com.auctions.hunters.model.enums.TransmissionType;
import com.auctions.hunters.service.car.CarService;
import com.auctions.hunters.service.confirmationtoken.ConfirmationTokenService;
import com.auctions.hunters.service.role.RoleService;
import com.auctions.hunters.service.user.BuyerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.OffsetDateTime;
import java.util.*;

import static com.auctions.hunters.utils.DateUtils.getDateTime;

@SpringBootApplication
public class AuctionsHunters implements CommandLineRunner {

    private final BuyerService buyerService;
    private final RoleService roleService;
    private final ConfirmationTokenService confirmationTokenService;
    private final CarService carService;

    public AuctionsHunters(BuyerService buyerService, RoleService roleService, ConfirmationTokenService confirmationTokenService, CarService carService) {
        this.buyerService = buyerService;
        this.roleService = roleService;
        this.confirmationTokenService = confirmationTokenService;
        this.carService = carService;
    }

    public static void main(String[] args) {
        SpringApplication.run(AuctionsHunters.class, args);

    }

    @Override
    public void run(String... args) {
        insertPredefinedAdmin();
    }

    /**
     * Method used for inserting a predefined administrator account into the database at the service start.
     */
    private void insertPredefinedAdmin() {
        String adminData = "admin";
        String adminPhoneNumber = "1234567890";
        String adminEmail = "auctionshunters@gmail.com";
        String adminCityAddress = "Caracal";
        String adminCreditCardNumber = "1234567890123456";
        OffsetDateTime now = getDateTime();

        if (!buyerService.isUserEmailAlreadyRegistered(adminEmail)) {

            Role role = Role.builder()
                    .name("ADMIN")
                    .creationDate(OffsetDateTime.from(now))
                    .build();
            roleService.save(role);

            Set<Role> set = new HashSet<>();
            set.add(role);

            User admin = new User(adminData, adminData, adminEmail, adminCityAddress,
                    adminPhoneNumber, set, adminCreditCardNumber);

            admin.setLocked(false);
            admin.setEnabled(true);
//            Car newCar = Car.builder()
//                    .user(admin)
//                    .category(CategoryType.SEDAN)
//                    .model("BMW")
//                    .vin("VIN")
//                    .tankCapacity(65)
//                    .color("black")
//                    .manufacturingYear(2004)
//                    .horsePower(116)
//                    .mileage(140000)
//                    .transmissionType(TransmissionType.MANUAL)
//                    .fuelType(FuelType.GASOLINE)
//                    .pollutionStandard(PollutionStandard.EURO_3)
//                    .numberOfPreviousOwners(2)
//                    .numberOfPreviousAccidents(2)
//                    .build();

          // ?// admin.setCarList(List.of(newCar));
            buyerService.save(admin);


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
