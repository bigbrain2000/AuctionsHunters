package com.auctions.hunters.model;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "image")
@Data
public class Image {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @SequenceGenerator(name = "image_sequence", sequenceName = "image_sequence", allocationSize = 1)
    @Column(name = "id", updatable = false, columnDefinition = "INTEGER")
    private Integer id;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "car_id", referencedColumnName = "id")
    private Car car;

    @Lob
    @Column(name = "data", nullable = false)
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] data;

    @Column(name = "content_type", nullable = false)
    private String contentType;
}