package com.auctions.hunters.service.bid;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.model.User;
import com.auctions.hunters.repository.BidRepository;
import com.auctions.hunters.service.ml.RecommendationService;
import com.auctions.hunters.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Service
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final UserService userService;
    private final RecommendationService recommendationService;

    public BidServiceImpl(BidRepository bidRepository,
                          UserService userService,
                          RecommendationService recommendationService) {
        this.bidRepository = bidRepository;
        this.userService = userService;
        this.recommendationService = recommendationService;
    }

//    @PostConstruct
//    void ceva() {
//        List<Auction> recommendations = recommendationService.getRecommendations(4);
//
//        for (Auction auction : recommendations) {
//            log.info("Licitatie {}", auction.getId());
//        }
//
//        log.info(String.valueOf(recommendations.size()));
//    }

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
