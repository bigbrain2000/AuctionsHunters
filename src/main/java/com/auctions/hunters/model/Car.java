package com.auctions.hunters.model;


import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "car",  uniqueConstraints = @UniqueConstraint(name = "vin_unique", columnNames = "vin"))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
public class Car {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @SequenceGenerator(name = "car_sequence", sequenceName = "car_sequence", allocationSize = 1)
    @Column(name = "id", updatable = false, columnDefinition = "INTEGER")
    private Integer id;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "car", cascade = ALL, orphanRemoval = true, fetch = EAGER)
    private List<Image> images = new ArrayList<>();

    @Column(name = "vin", nullable = false)
    private String vin;

    @Column(name = "producer", nullable = false)
    private String producer;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "modelYear", nullable = false)
    private String modelYear;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "engineDisplacement", nullable = false)
    private String engineDisplacement;

    @Column(name = "enginePower", nullable = false)
    private String enginePower;

    @Column(name = "fuelTypePrimary", nullable = false)
    private String fuelTypePrimary;

    @Column(name = "transmission", nullable = false)
    private String transmission;

    @Column(name = "drive", nullable = false)
    private String drive;

    @Column(name = "numberOfDoors", nullable = false)
    private String numberOfDoors;

    @Column(name = "numberOfSeats", nullable = false)
    private String numberOfSeats;

    @Column(name = "length", nullable = false)
    private String length;

    @Column(name = "height", nullable = false)
    private String height;

    @Column(name = "width", nullable = false)
    private String width;

    @Column(name = "maxWeight", nullable = false)
    private String maxWeight;

    @Column(name = "fuelConsumptionCombined", nullable = false)
    private String fuelConsumptionCombined;

    @Column(name = "emissionStandard", nullable = false)
    private String emissionStandard;

    @Builder.Default
    private Boolean isAuctioned = false;
}
