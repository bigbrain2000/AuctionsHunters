package com.auctions.hunters.repository;

import com.auctions.hunters.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AuctionRepository extends JpaRepository<Auction, Integer> {

    @Override
    List<Auction> findAll();
}
