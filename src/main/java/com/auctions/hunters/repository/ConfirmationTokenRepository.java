package com.auctions.hunters.repository;

import com.auctions.hunters.model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Integer> {

    Optional<ConfirmationToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken SET tokenConfirmedAt = :tokenConfirmationDate WHERE token = :token")
    void updateTokenConfirmationDate(@Param("token") String token, @Param("tokenConfirmationDate") OffsetDateTime tokenConfirmationDate);
}