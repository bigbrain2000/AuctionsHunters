package com.auctions.hunters.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "car", uniqueConstraints = @UniqueConstraint(name = "vin_unique", columnNames = "vin"))
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

    private String vin;
    private String producer;
    private String model;
    private String modelYear;
    private String body;
    private String engineDisplacement;
    private String enginePower;
    private String fuelTypePrimary;
    private String transmission;
    private String drive;
    private String numberOfDoors;
    private String numberOfSeats;
    private String length;
    private String height;
    private String width;
    private String maxWeight;
    private String fuelConsumptionCombined;
    private String emissionStandard;

    @Builder.Default
    private Boolean isAuctioned = false;
}
