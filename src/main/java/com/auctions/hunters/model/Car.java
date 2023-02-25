package com.auctions.hunters.model;


import com.auctions.hunters.model.enums.DriveTrainType;
import com.auctions.hunters.model.enums.FuelType;
import com.auctions.hunters.model.enums.TransmissionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;

import static com.auctions.hunters.utils.DateUtils.DATE_TIME_PATTERN;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "car")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Car {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @SequenceGenerator(name = "car_sequence", sequenceName = "car_sequence", allocationSize = 1)
    @Column(name = "id", updatable = false, columnDefinition = "INTEGER")
    private Integer id;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "category", nullable = false, columnDefinition = "TEXT")
    private String category;

    @Column(name = "model", nullable = false, columnDefinition = "TEXT")
    private String model;

    @Column(name = "vin", nullable = false, columnDefinition = "TEXT")
    private String vin;

    @Column(name = "tank_capacity", nullable = false, columnDefinition = "TEXT")
    private String tankCapacity;

    @Column(name = "color", nullable = false, columnDefinition = "TEXT")
    private String color;

    @JsonFormat(shape = STRING, pattern = DATE_TIME_PATTERN)
    @Column(name = "manufacturing_year", nullable = false)
    private OffsetDateTime manufacturingYear;

    @Column(name = "horse_power", nullable = false, columnDefinition = "TEXT")
    private String horsePower;

    @Column(name = "mileage", nullable = false, columnDefinition = "TEXT")
    private String mileage;

    @Column(name = "transmission_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransmissionType transmissionType;

    @Column(name = "pollution_standard", nullable = false, columnDefinition = "TEXT")
    private String pollutionStandard;

    @Column(name = "number_of_previous_owners", nullable = false, columnDefinition = "INTEGER")
    private Integer numberOfPreviousOwners;

    @Column(name = "number_of_previous_accidents", nullable = false, columnDefinition = "INTEGER")
    private Integer numberOfPreviousAccidents;

    @Column(name = "drivetrain", nullable = false)
    @Enumerated(EnumType.STRING)
    private DriveTrainType drivetrain;

    @Column(name = "fuel_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;
}
