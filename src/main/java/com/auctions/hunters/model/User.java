package com.auctions.hunters.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "user_email_unique", columnNames = "email"),
                @UniqueConstraint(name = "username", columnNames = "username")
        })
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @Column(name = "id", updatable = false, columnDefinition = "INTEGER")
    private Integer id;

    @Builder.Default
    @ManyToMany(cascade = MERGE, fetch = EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> role = new HashSet<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = EAGER, cascade = ALL)
    private List<Car> carList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<Auction> auctions;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = LAZY, cascade = ALL)
    private Set<Bid> bids = new HashSet<>();

    @Column(name = "username", nullable = false, columnDefinition = "TEXT")
    private String username;

    @Column(name = "password", nullable = false, columnDefinition = "TEXT")
    private String password;

    @Column(name = "email", nullable = false, columnDefinition = "TEXT")
    private String email;

    @Column(name = "city_address", nullable = false, columnDefinition = "TEXT")
    private String cityAddress;

    @Column(name = "phone_number", nullable = false, columnDefinition = "TEXT")
    private String phoneNumber;

    @Builder.Default
    private Boolean locked = false;

    @Builder.Default
    private Boolean enabled = false;

    public User(String username,
                String password,
                String email,
                String cityAddress,
                String phoneNumber,
                Set<Role> role) {

        this.username = username;
        this.password = password;
        this.email = email;
        this.cityAddress = cityAddress;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}


