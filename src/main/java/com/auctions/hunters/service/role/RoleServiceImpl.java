package com.auctions.hunters.service.role;

import com.auctions.hunters.model.Role;
import com.auctions.hunters.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * Service class used for managing roles and implementing the {@link RoleService} interface.
 */
@Service
@Validated
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Save a {@link Role} in the database.
     *
     * @param role the {@link Role} object we want to save into the database
     * @return the saved role
     */
    @Override
    public Role save(@Valid Role role) {
        return roleRepository.save(role);
    }
}
