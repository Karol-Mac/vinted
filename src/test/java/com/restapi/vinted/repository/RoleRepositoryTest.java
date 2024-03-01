package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testSave_ValidClothe(){
        Role role = Role.builder().name("ROLE_USER").build();

        var savedRole = roleRepository.save(role);

        assertNotNull(savedRole);
        assertThat(savedRole.getId()).isGreaterThan(0);
        assertEquals(role.getName(), savedRole.getName());
    }


    @Test
    public void testFindByName_ExistingRoleName(){
        Role role = Role.builder().name("ROLE_USER").build();
        var savedRole = roleRepository.save(role);

        var foundedRole = roleRepository.findByName(savedRole.getName());

        assertTrue(foundedRole.isPresent());
        assertEquals(foundedRole.get(), savedRole);
    }

    @Test
    public void testFindByName_InvalidRoleName(){
        Role role = Role.builder().name("ROLE_USER").build();
        var savedRole = roleRepository.save(role);

        var foundedRole = roleRepository.findByName("ROLE_ADMIN");

        assertFalse(foundedRole.isPresent());
    }
}
