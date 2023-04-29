package com.auctions.hunters.model;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "finished_auction")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FinishedAuction {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", updatable = false, columnDefinition = "INTEGER")
    private Integer id;

    @Column(name = "buyer_id", nullable = false)
    private Integer buyerId;

    @Column(name = "seller_id", nullable = false)
    private Integer sellerId;

    @Column(name = "car_id", nullable = false)
    private Integer carId;
}
