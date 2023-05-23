package com.auctions.hunters.service.confirmationtoken;

import com.auctions.hunters.model.ConfirmationToken;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Interface used for declaring the methods signatures that can be performed with a {@link ConfirmationToken} entity.
 */
public interface ConfirmationTokenService {

    /**
     * Save a {@link ConfirmationToken} in the database.
     *
     * @param token the {@link ConfirmationToken} to be saved
     * @return the saved {@link ConfirmationToken}
     */
    ConfirmationToken saveConfirmationToken(@NotNull ConfirmationToken token);

    /**
     * Get a saved {@link ConfirmationToken} from the DB
     *
     * @param token the token to be retrieved
     * @return the persisted searched token
     */
    Optional<ConfirmationToken> getToken(@NotBlank String token);

    /**
     * Set a token confirmation date.
     *
     * @param token the token we want to save
     */
    void setConfirmedAt(@NotBlank String token);
}
