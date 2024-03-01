package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Role;
import com.restapi.vinted.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    private User user;
    private Role role;

    @BeforeEach
    void init(){

        role = roleRepository.save(Role.builder().name("ROLE_USER").build());

        user = User.builder()
                .name("user")
                .username("username")
                .email("user@email.com")
                .roles(Set.of(role))
                .password("1234qwert")
                .build();
    }

    @AfterEach
    void clearUserData(){
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testSave_ValidUser(){

        User saved = userRepository.save(user);

        assertNotNull(saved);
        assertThat(saved.getId()).isGreaterThan(0);
        assertEquals(saved.getUsername(), user.getUsername());
        assertEquals(saved.getEmail(), user.getEmail());
    }

    @Test
    void testFindByUsernameOrEmail_ExistingUsernameOrEmail() {
        String email = "userNewEmail@email.com";
        user.setEmail(email);
        User saved = userRepository.save(user);

        var founded = userRepository.findByUsernameOrEmail(email, email);

        assertTrue(founded.isPresent());
        assertEquals(founded.get(), saved);
    }

    @Test
    void testFindByUsernameOrEmail_InvalidUsernameOrEmail() {
        String email = "userNewEmail@email.com";
        String badEmail = "userBadEmail@email.com";
        user.setEmail(email);
        userRepository.save(user);

        var founded = userRepository.findByUsernameOrEmail(badEmail, badEmail);

        assertTrue(founded.isEmpty());
    }

    @Test
    void testExistsByUsername_ValidUsername() {
        String username = "new username";
        user.setUsername(username);
        userRepository.save(user);
        var founded = userRepository.existsByUsername(username);

        assertTrue(founded);
    }

    @Test
    void testExistsByUsername_InvalidUsername() {
        String username = "new username";
        user.setUsername(username);
        userRepository.save(user);
        var founded = userRepository.existsByUsername("bad username");

        assertFalse(founded);
    }

    @Test
    void testExistsByEmail_ValidEmail() {
        String email = "userNewEmail@email.com";
        user.setUsername(email);
        userRepository.save(user);
        var founded = userRepository.existsByUsername(email);

        assertTrue(founded);
    }

    @Test
    void testExistsByEmail_InvalidEmail() {
        String email = "userNewEmail@email.com";
        user.setUsername(email);
        userRepository.save(user);
        var founded = userRepository.existsByUsername("userBadEmail@email.com");

        assertFalse(founded);
    }
}