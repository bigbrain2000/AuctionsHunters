package com.auctions.hunters.service.confirmationtoken;


import com.auctions.hunters.exceptions.ResourceNotFoundException;
import com.auctions.hunters.model.ConfirmationToken;
import com.auctions.hunters.repository.ConfirmationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    public ConfirmationToken saveConfirmationToken(ConfirmationToken token) {
        LOGGER.debug("Token was successfully inserted in the database");
        return confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        LOGGER.debug("Token was successfully retrieved from the database");
        return confirmationTokenRepository.findByToken(token);
    }

    public void setConfirmedAt(String token) {
        LOGGER.debug("Token was successfully updated in the database");
        confirmationTokenRepository.updateTokenConfirmationDate(token, getDateTime());
    }

    public void delete(Integer id) throws ResourceNotFoundException {
        confirmationTokenRepository.findById(id).orElseThrow(() -> {
            LOGGER.debug("Token with id {} could not be deleted from the database", id);
            throw new ResourceNotFoundException("Token", "id", id);
        });

        LOGGER.debug("Token successfully deleted from the database");
        confirmationTokenRepository.deleteById(id);
    }
}
