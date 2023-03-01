package com.auctions.hunters.service.role;

import com.auctions.hunters.model.Role;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * Interface used for declaring the method signatures that can be performed with a {@link Role} entity.
 */
@Validated
@Repository
public interface RoleService {

    /**
     * Save a role in the DB.
     *
     * @param role -  the role we want to save into the DB
     * @return - the saved role
     */
    Role save(@Valid Role role);
}
