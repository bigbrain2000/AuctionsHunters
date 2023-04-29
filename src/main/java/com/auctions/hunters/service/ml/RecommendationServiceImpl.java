package com.auctions.hunters.service.ml;

import com.auctions.hunters.model.Auction;
import com.auctions.hunters.model.Car;
import com.auctions.hunters.model.User;
import com.auctions.hunters.service.auction.AuctionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private AuctionService auctionService;

    private static final int K_NEIGHBOURS = 1;
    private static final int NUMBER_OF_RECOMMENDED_AUCTIONS = 1;

    /**
     * Return a list with all the different {@link Car} objects retrieved from the recommended {@link Auction} list.
     *
     * @param user the user for which the auction cars will be returned
     * @return a list with {@link Car} objects
     */
    @Override
    public List<Car> getRecommendedAuctionedCarsForUser(User user) {
        return getRecommendations(user.getId()).stream()
                .filter(auction -> auction.getEndTime().isAfter(auction.getStartTime()))
                .map(Auction::getCar)
                .distinct()
                .toList();
    }

    /**
     * Return a list with all the unfinished recommended {@link Auction} objects.
     *
     * @param user the user for which the auctions will be returned
     * @return a list with {@link Auction} objects
     */
    @Override
    public List<Auction> getUnfinishedRecommendedAuctions(User user) {
        return getRecommendations(user.getId()).stream()
                .filter(auction -> auction.getStartTime().toLocalDateTime().isBefore(LocalDateTime.now()))
                .filter(auction -> auction.getEndTime().toLocalDateTime().isAfter(LocalDateTime.now()))
                .distinct()
                .toList();
    }

    //TODO: add a documentation for it in license
    private List<Auction> getRecommendations(Integer uid) {
        List<Integer> knn = getKNN(uid);

        if (knn.isEmpty()) {
            return getTopBidAuctions();
        }

        String knnStr = String.join(", ", knn.stream().map(Object::toString).toList());
        log.debug("The number of KNN for user {}: {}", uid, knnStr);

        return getRecommendedAuctions(uid, knn);
    }

    /**
     * Finds the K nearest neighbors of a user based on the number of auctions they have in common.
     *
     * @return a list of integers representing the ids of the K nearest neighbors
     */
    private List<Integer> getKNN(Integer userId) {
        //retrieve all the auctions for the user specified by id in the parameter
        List<Auction> allUserAuctions = auctionService.getAllAuctionsByUserId(userId);
        //store the count of common auctions for each bidder (excluding the user)
        Map<Integer, Integer> biddersCount = new HashMap<>();

        for (Auction auction : allUserAuctions) {
            List<Integer> bidderIds = auctionService.getBidderIds(auction.getId());

            //iterate over the bidder ids, and for each bidder ID (except the given user's id), increment the count in the biddersCount map
            for (Integer bidderId : bidderIds) {
                if (!bidderId.equals(userId)) {
                    biddersCount.put(bidderId, biddersCount.getOrDefault(bidderId, 0) + 1);
                }
            }
        }

        return biddersCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) //descending order so that the bidders with the most common auctions will appear first
                .limit(K_NEIGHBOURS) //limit the size of the neighbours list
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Retrieve a list of {@link Auction} objects based on a list of the K-nearest neighbours calculated in KNN.
     *
     * @param userId the id of the user for which we will return a list of {@link Auction} objects
     * @param knn    a list of bidders ids (except the given user's id)
     * @return a list of {@link Auction} objects which represents the recommended auctions for the given user id
     */
    private List<Auction> getRecommendedAuctions(Integer userId, List<Integer> knn) {
        //retrieve all the auctions for the user specified by id in the parameter
        List<Auction> excludeAuctions = auctionService.getAllAuctionsByUserId(userId);

        //create a Set of integers who represents the ids of the auctions for which the user created
        Set<Integer> excludeAuctionIds = excludeAuctions.stream()
                .map(Auction::getId)
                .collect(Collectors.toSet());

        //log the excluded auctions ids
        Function<Auction, String> auctionMapper = auction -> "Auction{id=" + auction.getId() + "}";
        String excludeAuctionIdsStr = collectionToString(excludeAuctions, auctionMapper);
        log.debug("Excluded auctions for user {}: {}", userId, excludeAuctionIdsStr);

        List<Auction> recommendedAuctionsList = knn.stream()
                .flatMap(n -> {
                    //for each integer value from the list, which represents the ids of the user neighbours calculated in KMM
                    //retrieve a list with all their auctions
                    List<Auction> neighborAuctionsList = auctionService.getAllAuctionsByUserId(n);

                    //log the neighbours auctions list
                    String neighborAuctionsListStr = collectionToString(neighborAuctionsList, auctionMapper);
                    log.debug("Neighbor auctions for user {}: {}", n, neighborAuctionsListStr);

                    return neighborAuctionsList.stream();
                })
                .distinct() //remove the duplicated auctions inserted
                .filter(auction -> !excludeAuctionIds.contains(auction.getId())) //remove the auctions for the user to get only his neighbour auctions
                .limit(NUMBER_OF_RECOMMENDED_AUCTIONS) //limit the size of the recommended auctions list
                .toList();

        //log the recommended auctions list
        String recommendedAuctionsStr = collectionToString(recommendedAuctionsList, auctionMapper);
        log.debug("Recommended auctions for user {}: {}", userId, recommendedAuctionsStr);

        return recommendedAuctionsList;
    }

    private List<Auction> getTopBidAuctions() {
        return auctionService.getTopBidAuctions(NUMBER_OF_RECOMMENDED_AUCTIONS);
    }

    /**
     * Format the displaying of a {@link Collection} elements.
     */
    private <T> String collectionToString(Collection<T> collection, Function<T, String> mapper) {
        return String.join(", ", collection.stream().map(mapper).toList());
    }
}
