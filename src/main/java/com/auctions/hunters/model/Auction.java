package com.auctions.hunters.model;

import com.auctions.hunters.model.enums.AuctionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

import static com.auctions.hunters.utils.DateUtils.DATE_TIME_PATTERN;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "auction")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Auction {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", updatable = false, columnDefinition = "INTEGER")
    private Integer id;

    @OneToOne(fetch = EAGER, cascade = MERGE)
    @JoinColumn(name = "car_id", referencedColumnName = "id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "buyer_id")
    private Integer buyerId;

    @OneToMany(mappedBy = "auction", cascade = ALL, fetch = LAZY)
    private List<Bid> bidders;

    @Column(name = "minimum_price", nullable = false)
    private float minimumPrice;

    @JsonFormat(shape = STRING, pattern = DATE_TIME_PATTERN)
    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @JsonFormat(shape = STRING, pattern = DATE_TIME_PATTERN)
    @Column(name = "endTime", nullable = false)
    private OffsetDateTime endTime;

    @Column(name = "starting_price", nullable = false)
    private float startingPrice;

    @Column(name = "current_price")
    private float currentPrice;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuctionStatus status;
}
