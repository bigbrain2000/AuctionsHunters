package com.auctions.hunters.service.confirmationtoken;

import com.auctions.hunters.model.ConfirmationToken;
import com.auctions.hunters.repository.ConfirmationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

import static com.auctions.hunters.utils.DateUtils.getDateTime;

/**
 * Concrete class that implements the {@link ConfirmationTokenService} interface.
 */
@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationTokenServiceImpl.class);

    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    /**
     * Save a {@link ConfirmationToken} in the database.
     *
     * @param token the {@link ConfirmationToken} to be saved
     * @return the saved {@link ConfirmationToken}
     */
    public ConfirmationToken saveConfirmationToken(@NotNull ConfirmationToken token) {
        LOGGER.debug("Token was successfully inserted in the database");
        return confirmationTokenRepository.save(token);
    }

    /**
     * Get a saved {@link ConfirmationToken} from the DB
     *
     * @param token the token to be retrieved
     * @return the persisted searched token
     */
    public Optional<ConfirmationToken> getToken(@NotBlank String token) {
        LOGGER.debug("Token was successfully retrieved from the database");
        return confirmationTokenRepository.findByToken(token);
    }

    /**
     * Set a token confirmation date.
     *
     * @param token the token we want to save
     */
    public void setConfirmedAt(@NotBlank String token) {
        LOGGER.debug("Token was successfully updated in the database");
        confirmationTokenRepository.updateTokenConfirmationDate(token, getDateTime());
    }
}
