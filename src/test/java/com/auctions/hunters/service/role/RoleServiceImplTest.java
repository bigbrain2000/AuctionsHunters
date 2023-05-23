package com.auctions.hunters.service.role;

import com.auctions.hunters.model.Role;
import com.auctions.hunters.repository.RoleRepository;
import com.auctions.hunters.util.RoleUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest implements RoleUtils {

    @Mock
    private RoleRepository roleRepository;

    private RoleService uut;

    @BeforeEach
    void setUp() {
        openMocks(this);
        uut = spy(new RoleServiceImpl(roleRepository));
    }

    @Test
    void save_correctRole_returnsSuccess() {
        Role expectedRole = mockRole();
        when(roleRepository.save(any(Role.class))).thenReturn(expectedRole);

        uut.save(expectedRole);

        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository, times(1)).save(roleCaptor.capture());
        Role actualRole = roleCaptor.getValue();
        assertEquals(expectedRole, actualRole);
    }

    @Test
    void save_throwsException() {
        Role role = mockRole();
        when(roleRepository.save(any(Role.class))).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> uut.save(role));
        verify(roleRepository, times(1)).save(role);
    }
}