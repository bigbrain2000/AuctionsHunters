package com.auctions.hunters.service.role;

import com.auctions.hunters.model.Role;
import com.auctions.hunters.repository.RoleRepository;
import org.springframework.stereotype.Service;

/**
 * Service class used for managing roles and implementing the {@link RoleService} interface.
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }
}
