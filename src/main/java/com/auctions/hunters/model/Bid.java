package com.auctions.hunters.model;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "bid")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Bid {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @SequenceGenerator(name = "bid_sequence", sequenceName = "bid_sequence", allocationSize = 1)
    @Column(name = "id", updatable = false, columnDefinition = "INTEGER")
    private int id;

    @Column(name = "amount")
    private double amount;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}