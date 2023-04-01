package com.auctions.hunters.model;


import com.auctions.hunters.model.enums.CategoryType;
import com.auctions.hunters.model.enums.FuelType;
import com.auctions.hunters.model.enums.PollutionStandard;
import com.auctions.hunters.model.enums.TransmissionType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "car")
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

    @Column(name = "category", nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private CategoryType category;

    @Column(name = "model", nullable = false, columnDefinition = "TEXT")
    private String model;

    @Column(name = "vin", nullable = false, columnDefinition = "TEXT")
    private String vin;

    @Column(name = "tank_capacity", nullable = false)
    private float tankCapacity;

    @Column(name = "color", nullable = false, columnDefinition = "TEXT")
    private String color;

    @Column(name = "manufacturing_year", nullable = false, columnDefinition = "INTEGER")
    private int manufacturingYear;

    @Column(name = "cylinder_capacity", nullable = false)
    private float cylinderCapacity;

    @Column(name = "horse_power", nullable = false)
    private float horsePower;

    @Column(name = "mileage", nullable = false)
    private float mileage;

    @Column(name = "transmission_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransmissionType transmissionType;

    @Column(name = "fuel_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Column(name = "pollution_standard", nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private PollutionStandard pollutionStandard;

    @Column(name = "number_of_previous_owners", nullable = false, columnDefinition = "INTEGER")
    private Integer numberOfPreviousOwners;

    @Column(name = "number_of_previous_accidents", nullable = false, columnDefinition = "INTEGER")
    private Integer numberOfPreviousAccidents;

    @Column(name = "minimum_price", nullable = false)
    private float minimumPrice;
}
