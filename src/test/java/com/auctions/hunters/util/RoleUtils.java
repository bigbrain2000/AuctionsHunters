package com.auctions.hunters.util;

import com.auctions.hunters.model.Role;

import java.time.OffsetDateTime;

import static com.auctions.hunters.utils.DateUtils.getDateTime;

public interface RoleUtils {

    OffsetDateTime NOW = getDateTime();

    default Role mockRole() {
        return Role.builder()
                .id(1)
                .name("CLIENT")
                .creationDate(NOW)
                .build();
    }
}
