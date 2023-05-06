package com.auctions.hunters.model;

import com.auctions.hunters.model.enums.CarStatus;
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
@Getter
@Setter
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
    private String series;
    private String drive;
    private String engineDisplacement;
    private String enginePower;
    private String fuelTypePrimary;
    private String engineCode;
    private String transmission;
    private String numberOfGears;
    private String emissionStandard;
    private String manufacturerAddress;
    private String manufacturerCountry;
    private String engineRpm;
    private String numberOfCylinders;
    private String fuelConsumptionCombined;
    private String fuelConsumptionUrban;
    private String co2Emission;
    private String axleRatio;
    private String numberOfWheels;
    private String numberOfAxles;
    private String numberOfDoors;
    private String numberOfSeats;
    private String frontBrakes;
    private String brakeSystem;
    private String suspension;
    private String steeringType;
    private String wheelSize;
    private String wheelBase;
    private String height;
    private String length;
    private String width;
    private String maxSpeed;
    private String emptyWeight;
    private String maxWeight;
    private String maxRoofLoad;
    private String abs;

    @Enumerated(EnumType.STRING)
    private CarStatus status;
}
