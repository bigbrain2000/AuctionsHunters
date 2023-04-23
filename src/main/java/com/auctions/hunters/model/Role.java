package com.auctions.hunters.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;

import static com.auctions.hunters.utils.DateUtils.DATE_TIME_PATTERN;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "role")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @Column(name = "id", updatable = false, columnDefinition = "INTEGER")
    @GeneratedValue(strategy = IDENTITY)
    @SequenceGenerator(name = "role_sequence", sequenceName = "role_sequence", allocationSize = 1)
    private Integer id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @JsonFormat(shape = STRING, pattern = DATE_TIME_PATTERN)
    @Column(name = "creation_date", nullable = false)
    private OffsetDateTime creationDate;
}