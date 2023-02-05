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
import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "confirmation_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = SEQUENCE)
    @SequenceGenerator(name = "confirmation_token_sequence", sequenceName = "confirmation_token_sequence", allocationSize = 1)
    private Integer id;

    @Column(name = "token", nullable = false, columnDefinition = "TEXT")
    private String token;

    @JsonFormat(shape = STRING, pattern = DATE_TIME_PATTERN)
    @Column(name = "token_creation_date", nullable = false)
    private OffsetDateTime tokenCreatedAt;

    @JsonFormat(shape = STRING, pattern = DATE_TIME_PATTERN)
    @Column(name = "token_expiration_date", nullable = false)
    private OffsetDateTime tokenExpiresAt;

    @JsonFormat(shape = STRING, pattern = DATE_TIME_PATTERN)
    @Column(name = "token_confirmation_date", nullable = false)
    private OffsetDateTime tokenConfirmedAt;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public ConfirmationToken(String token, OffsetDateTime tokenCreatedAt, OffsetDateTime tokenExpiresAt, OffsetDateTime tokenConfirmedAt, User user) {
        this.token = token;
        this.tokenCreatedAt = tokenCreatedAt;
        this.tokenExpiresAt = tokenExpiresAt;
        this.tokenConfirmedAt = tokenConfirmedAt;
        this.user = user;
    }
}