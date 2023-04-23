package com.auctions.hunters.service.bid;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.BidRepository;
import com.auctions.hunters.service.ml.RecommendationServiceImpl;
import com.auctions.hunters.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final UserService userService;

    public BidServiceImpl(BidRepository bidRepository,
                          UserService userService) {
        this.bidRepository = bidRepository;
        this.userService = userService;
    }

    @Override
    public Bid save(float amount, Auction auction) {

        String loggedUsername = userService.getLoggedUsername();
        User user = userService.findByUsername(loggedUsername);

        Bid bid = Bid.builder()
                .amount(amount)
                .auction(auction)
                .user(user)
                .build();

        return bidRepository.save(bid);
    }
}
