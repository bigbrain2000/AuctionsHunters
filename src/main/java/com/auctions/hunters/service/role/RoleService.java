package com.auctions.hunters.service.role;

import com.auctions.hunters.model.Role;

import javax.validation.Valid;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Role} entity.
 */
public interface RoleService {

    /**
     * Save a {@link Role} in the database.
     *
     * @param role the {@link Role} object we want to save into the database
     * @return the saved role
     */
    Role save(@Valid Role role);
}
