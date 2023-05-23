package com.auctions.hunters.service.confirmationtoken;

import com.auctions.hunters.model.ConfirmationToken;
import com.auctions.hunters.repository.ConfirmationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceImplTest {

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    private ConfirmationTokenService uut;

    private ConfirmationToken confirmationToken;

    @BeforeEach
    void setUp() {
        openMocks(this);
        uut = spy(new ConfirmationTokenServiceImpl(confirmationTokenRepository));

        confirmationToken = ConfirmationToken.builder()
                .token("token")
                .build();
    }

    @Test
    void saveConfirmationToken_validConfirmationToken_returnsSuccess() {
        when(confirmationTokenRepository.save(any(ConfirmationToken.class))).thenReturn(confirmationToken);

        ConfirmationToken actualConfirmationToken = uut.saveConfirmationToken(confirmationToken);

        assertEquals(confirmationToken, actualConfirmationToken);
        verify(confirmationTokenRepository, times(1)).save(any(ConfirmationToken.class));

    }

    @Test
    void getToken_existingToken_returnsExistingConfirmationToken() {
        when(confirmationTokenRepository.findByToken(anyString())).thenReturn(Optional.ofNullable(confirmationToken));

        Optional<ConfirmationToken> optionalConfirmationToken = uut.getToken(confirmationToken.getToken());

        assertTrue(optionalConfirmationToken.isPresent());
        assertEquals(confirmationToken, optionalConfirmationToken.get());
        verify(confirmationTokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    void setConfirmedAt_confirmingRegistration_updatesConfirmationTokenConfirmedStatus() {
        doNothing().when(confirmationTokenRepository).updateTokenConfirmationDate(anyString(), any(OffsetDateTime.class));

        uut.setConfirmedAt(confirmationToken.getToken());

        verify(confirmationTokenRepository, times(1)).updateTokenConfirmationDate(anyString(), any(OffsetDateTime.class));
    }
}