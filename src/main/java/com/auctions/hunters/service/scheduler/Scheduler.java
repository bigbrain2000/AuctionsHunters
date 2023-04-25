package com.auctions.hunters.service.scheduler;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.service.bid.BidService;
import com.auctions.hunters.service.email.EmailService;
import com.auctions.hunters.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Class used to remind users that their subscription has expired.
 */
@Slf4j
@Component
@EnableScheduling
public class Scheduler {

    private final UserService userService;
    private final BidService bidService;
    private final EmailService emailService;

    public Scheduler(UserService userService,
                     BidService bidService,
                     EmailService emailService) {
        this.userService = userService;
        this.bidService = bidService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 * * ? * *") //every minute reminder
//    @Scheduled(cron = "*/1 * * * * *") //every second
    public void sendEmailIfSubscriptionExpired() {

        List<User> allUsers = userService.findAll();

        for (User user : allUsers) {
            // find all the auctions for each user
            List<Auction> auctions = bidService.findAuctionsByUser(user);

            for (Auction auction : auctions) {
                //find all the bids on each auction for that user
                List<Bid> userBids = bidService.findUserBidsForAuction(user, auction.getId());
                boolean isUserLastBidder = checkIfUserLastBidWasOvertaken(auction, userBids);

                if(isUserLastBidder) {
                    userService.updateReminder(user.getId(), FALSE);
                }

                //if the user has overtaken bids then email him
                if (!isUserLastBidder && !user.getReminder()) {
                    Car car = auction.getCar();

                    sendEmailIfUserBidWasOvertaken(user, car);

                    userService.updateReminder(user.getId(), TRUE);
                }
            }
        }
    }

    private boolean checkIfUserLastBidWasOvertaken(Auction auction, List<Bid> userBids) {
        boolean isUserLastBid = false;

        if (!userBids.isEmpty()) {
            Bid lastUserBid = userBids.get(0); // The first bid in the list is the last bid made by the user, due to the descending order
            isUserLastBid = auction.getCurrentPrice() == lastUserBid.getAmount(); //check if the auction current price is the same as the user last bid
        }

        return isUserLastBid;
    }

    private void sendEmailIfUserBidWasOvertaken(User user, Car car) {
        String emailBodyMessage = createEmailBody(user.getUsername(), car);
        String emailSubject = String.format("Oferta dumneavoastră  pentru %s %s a fost depășită.", car.getProducer(), car.getModel());
        emailService.sendEmail(user.getEmail(), emailSubject, emailBodyMessage);

        log.debug("An email was sent to user {} in order to inform that his last bid for the car {} {} has been overtaken.",
                user.getUsername(), car.getProducer(), car.getModel());
    }

    public String createEmailBody(String userName, Car car) {
        return "Salut " + userName + ",\n\n" +
                "Vrem să vă anunțăm că, din păcate, un alt participant a plasat " +
                "o ofertă mai mare pentru mașina " + car.getProducer() + " " + car.getModel() + " de care erați interesat.\n\n" +
                "Dacă sunteți în continuare interesat, vă încurajăm să revedeți licitația și să luați în considerare " +
                "posibilitatea de a plasa o nouă ofertă. Nu uitați că licitația poate fi destul de dinamică, iar dacă " +
                "o urmăriți cu atenție vă puteți crește șansele de succes.\n\n\n" +
                "Vă dorim mult noroc în viitoarele licitații!\n\n" +
                "Numai bine,\n" +
                "Echipa de licitație";
    }
}