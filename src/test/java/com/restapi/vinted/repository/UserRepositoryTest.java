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
    void givenUser_whenSave_thenUserIsSaved(){

        User saved = userRepository.save(user);

        assertNotNull(saved);
        assertThat(saved.getId()).isGreaterThan(0);
        assertEquals(saved.getUsername(), user.getUsername());
        assertEquals(saved.getEmail(), user.getEmail());
    }

    @Test
    void givenUsernameOrEmail_whenFindByUsernameOrEmail_thenOptionalUserIsRetrived() {
        String email = "userNewEmail@email.com";
        user.setEmail(email);
        User saved = userRepository.save(user);

        var founded = userRepository.findByUsernameOrEmail(email, email);

        assertTrue(founded.isPresent());
        assertEquals(founded.get(), saved);
    }

    @Test
    void givenInvalidUsernameOrEmail_whenFindByUsernameOrEmail_thenOptionalEmptyIsRetrived() {
        String email = "userNewEmail@email.com";
        String badEmail = "userBadEmail@email.com";
        user.setEmail(email);
        userRepository.save(user);

        var founded = userRepository.findByUsernameOrEmail(badEmail, badEmail);

        assertTrue(founded.isEmpty());
    }

    @Test
    void givenUsername_whenExistsByUsername_thenTrueIsReturned() {
        String username = "new username";
        user.setUsername(username);
        userRepository.save(user);
        var founded = userRepository.existsByUsername(username);

        assertTrue(founded);
    }

    @Test
    void givenNotExistingUsername_whenExistsByUsername_thenFalseIsReturned() {
        String username = "new username";
        user.setUsername(username);
        userRepository.save(user);
        var founded = userRepository.existsByUsername("bad username");

        assertFalse(founded);
    }

    @Test
    void givenEmail_whenExistsByEmail_thenTrueIsReturned() {
        String email = "userNewEmail@email.com";
        user.setEmail(email);
        userRepository.save(user);
        var founded = userRepository.existsByEmail(email);

        assertTrue(founded);
    }

    @Test
    void givenNotExistingEmail_whenExistsByEmail_thenFalseIsReturned() {
        String email = "userNewEmail@email.com";
        user.setEmail(email);
        userRepository.save(user);
        var founded = userRepository.existsByEmail("userBadEmail@email.com");

        assertFalse(founded);
    }
}