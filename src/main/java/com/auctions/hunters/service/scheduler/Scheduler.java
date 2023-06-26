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
 * Class used to remind users that their bids were overtaken. The scheduler is set to send emails every x minutes.
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
//    @Scheduled(cron = "0 0 0 */10 * ?") //every 10 days
    public void sendEmailIfUserBidsWereOvertaken() {
        log.debug("Scheduler process started");
        List<User> allUsers = userService.findAll();

        for (User user : allUsers) {
            // find all the auctions for each user
            List<Auction> auctions = bidService.findAuctionsByUser(user);

            for (Auction auction : auctions) {
                //find all the bids on each auction for that user
                List<Bid> userBids = bidService.findUserBidsForAuction(user, auction.getId());
                boolean isUserLastBidder = checkIfUserLastBidWasOvertaken(auction, userBids);

                setTheLastBidderReminderToFalse(user, isUserLastBidder);

                setTheOtherUsersReminderToTrueAndSendEmail(user, auction, isUserLastBidder);
            }
        }

        log.debug("Scheduler process ended");
    }

    /**
     * If the last {@link Bid} made by the user was overtaken in a {@link Auction} then return TRUE, else return FALSE.
     *
     * @param auction  the {@link Auction} for which we want to verify if the user bid was the last one made
     * @param userBids a list with all the user`s bids
     */
    private boolean checkIfUserLastBidWasOvertaken(Auction auction, List<Bid> userBids) {
        boolean isUserLastBid = false;

        if (!userBids.isEmpty()) {
            Bid lastUserBid = userBids.get(0); // The first bid in the list is the last bid made by the user, due to the descending order
            isUserLastBid = auction.getCurrentPrice() == lastUserBid.getAmount(); //check if the auction current price is the same as the user last bid
        }

        return isUserLastBid;
    }

    /**
     * If the user is the last bidder in the participated auctions list and his reminder is TRUE then set his reminder
     * to FALSE because he does not need a reminder when he is winning the {@link Auction}.
     *
     * @param user             the {@link User} for whom the reminder will be set to FALSE
     * @param isUserLastBidder a flag that indicates if the {@link User} parameter is the last bidder on all his auctions
     */
    private void setTheLastBidderReminderToFalse(User user, boolean isUserLastBidder) {
        if (isUserLastBidder && user.getReminder().equals(TRUE)) {
            userService.updateReminder(user.getId(), FALSE);
        }
    }

    /**
     * If the user that participated in the {@link Auction} is not the last bidder then set his reminder to TRUE as he
     * needs a reminder when he is NOT winning the {@link Auction}.
     *
     * @param user             the {@link User} for whom the reminder will be set to TRUE
     * @param isUserLastBidder a flag that indicates if the {@link User} parameter is the last bidder on all his auctions
     */
    private void setTheOtherUsersReminderToTrueAndSendEmail(User user, Auction auction, boolean isUserLastBidder) {
        if (!isUserLastBidder && (user.getReminder().equals(FALSE))) {
            Car car = auction.getCar();

            sendEmailIfUserBidWasOvertaken(user, car);

            userService.updateReminder(user.getId(), TRUE);
        }
    }

    /**
     * Construct an email template and sends it to the user that bid was overtaken by other participants.
     *
     * @param user the {@link User} for whom the email will be sent
     * @param car  the {@link Car} object that the {@link User} bid
     */
    private void sendEmailIfUserBidWasOvertaken(User user, Car car) {
        String emailBodyMessage = createEmailBody(user.getUsername(), car);
        String emailSubject = String.format("Oferta dumneavoastră  pentru %s %s a fost depășită.", car.getProducer(), car.getModel());
        emailService.sendEmail(user.getEmail(), emailSubject, emailBodyMessage);

        log.debug("An email was sent to user {} in order to inform that his last bid for the car {} {} has been overtaken.",
                user.getUsername(), car.getProducer(), car.getModel());
    }

    private String createEmailBody(String userName, Car car) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="utf-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f0f0f0;
                            color: #333;
                            margin: 0;
                            padding: 20px;
                        }

                        .container {
                            background-color: #ffffff;
                            padding: 20px;
                            border-radius: 4px;
                        }

                        h1 {
                            font-size: 24px;
                            margin: 0 0 10px;
                        }

                        p {
                            font-size: 16px;
                            line-height: 1.5;
                            margin: 0 0 10px;
                        }

                        .footer {
                            font-size: 14px;
                            color: #777;
                            margin-top: 20px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <p>Salut %s,</p>
                        <p>Dorim să vă anunțăm că un alt participant a plasat o ofertă mai mare pentru mașina %s %s de care erați interesat.</p>
                        <p>Dacă sunteți în continuare interesat de această mașina vă încurajăm să plasați o nouă ofertă.</p>
                            <p>Mult noroc în viitoarele licitații,</p>
                        <p>Echipa Vânătorii de Licitații</p>
                    </div>
                </body>
                </html>
                """.formatted(userName, car.getProducer(), car.getModel());
    }
}