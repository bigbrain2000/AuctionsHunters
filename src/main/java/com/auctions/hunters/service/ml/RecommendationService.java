package com.auctions.hunters.service.ml;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Bid;
import com.auctions.hunters.service.auction.AuctionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationService {

    private final int K_Neighbours = 3;
    private final int Items_to_recommend = 3;

    @Autowired
    private AuctionService auctionService;

    public List<Auction> getRecommendations(Integer uid) {
        List<Integer> knn = getKNN(uid, K_Neighbours);

        if (!knn.isEmpty()) {
            String knnStr = String.join(", ", knn.stream().map(Object::toString).toList());
            log.info("KNN for user {}: {}", uid, knnStr);

            return getItems(uid, knn);
        }

        return getTopBiddedAuctions();
    }

    private List<Auction> getTopBiddedAuctions() {
        return auctionService.getTopBidAuctions(Items_to_recommend);
    }

    private List<Integer> getKNN(Integer userId, Integer k) {
        List<Auction> allUserAuctions = auctionService.getAllAuctionsByUserId(userId);
        Map<Integer, Integer> biddersCount = new HashMap<>();

        for (Auction auction : allUserAuctions) {
            List<Integer> bidderIds = auctionService.getBidderIds(auction.getId());
            for (Integer bidderId : bidderIds) {
                if (!bidderId.equals(userId)) {
                    biddersCount.put(bidderId, biddersCount.getOrDefault(bidderId, 0) + 1);
                }
            }
        }

        return biddersCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(k)
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<Auction> getItems(Integer uid, List<Integer> knn) {
        List<Auction> excludeAuctions = auctionService.getAllAuctionsByUserId(uid);
        Set<Integer> excludeAuctionIds = excludeAuctions.stream().map(Auction::getId).collect(Collectors.toSet());

        //log the excluded auctions ids
        Function<Auction, String> auctionMapper = auction -> "Auction{id=" + auction.getId() + "}";
        String excludeAuctionIdsStr = collectionToString(excludeAuctions, auctionMapper);
        log.info("Excluded auctions for user {}: {}", uid, excludeAuctionIdsStr);

        List<Auction> recommendedAuctions = knn.stream()
                .flatMap(n -> {
                    List<Auction> neighborAuctions = auctionService.getAllAuctionsForBidder(n);

                    String neighborAuctionsStr = collectionToString(neighborAuctions, auctionMapper);
                    log.info("Neighbor auctions for user {}: {}", n, neighborAuctionsStr);

                    return neighborAuctions.stream();
                })
                .distinct()
                .filter(auction -> !excludeAuctionIds.contains(auction.getId()))
                .limit(Items_to_recommend)
                .toList();

        String recommendedAuctionsStr = collectionToString(recommendedAuctions, auctionMapper);
        log.info("Recommended auctions for user {}: {}", uid, recommendedAuctionsStr);

        return recommendedAuctions;
    }

    private <T> String collectionToString(Collection<T> collection, Function<T, String> mapper) {
        return String.join(", ", collection.stream().map(mapper).toList());
    }
}
