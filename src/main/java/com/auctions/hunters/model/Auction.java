package com.auctions.hunters.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.auctions.hunters.utils.DateUtils.DATE_TIME_PATTERN;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "auction")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Auction {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", updatable = false, columnDefinition = "INTEGER")
    private Integer id;

    @OneToOne(fetch = EAGER, cascade = {PERSIST, MERGE})
    @JoinColumn(name = "car_id", referencedColumnName = "id", nullable = false)
    private Car car;

    @Builder.Default
    @ManyToMany(cascade = MERGE, fetch = EAGER)
    @JoinTable(name = "user_bids", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "auction_id"))
    private Set<User> bidders = new HashSet<>();

    @Column(name = "seller_name", nullable = false, columnDefinition = "TEXT")
    private String sellerName;

    @OneToMany(mappedBy = "auction", cascade = ALL, fetch = LAZY)
    private List<Bid> bids;

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

    @Column(name = "is_active", nullable = false)
    private boolean isActive = false;
}
