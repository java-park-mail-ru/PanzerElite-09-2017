package ru.mail.park.services;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.models.User;
import org.junit.rules.ExpectedException;
import ru.mail.park.services.UserService;


import java.sql.*;
import java.sql.PreparedStatement;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserServiceTest {
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String UPDATED_PASSWORD = "updated_password";
    @Autowired
    private UserService userService;
    @Autowired
    private JdbcTemplate template;
    @Autowired
    private NamedParameterJdbcTemplate namedTemplate;


    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testSimpleCreateUser() {
        final User newUser = new User(0, 0, 0, 0, LOGIN, PASSWORD);

        final User createdUser = userService.createUser(newUser);

        assertNotNull(createdUser);
        assertEquals(LOGIN, createdUser.getLogin());
        assertEquals(PASSWORD, createdUser.getPassword());
    }

    @Test
    public void testCreateDuplicateUser() {
        final User user = new User(0, 0, 0, 0, LOGIN, PASSWORD);
        userService.createUser(user);

        final User duplicatedUser = new User(0, 0, 0, 0, LOGIN, PASSWORD);
        assert (userService.createUser(duplicatedUser) == null);
    }


    @Test
    public void testGetUserByLogin() {

        final User user = new User(0, 0, 0, 0, LOGIN, PASSWORD);
        final User createdUser = userService.createUser(user);

        final User foundedUser = userService.getUserByLogin(createdUser.getLogin());

        assertNotNull(foundedUser);
    }

    @Test
    public void updateUser() {
        final User user = new User(0, 0, 0, 0, LOGIN, PASSWORD);
        userService.createUser(user);
        user.setPassword(UPDATED_PASSWORD);

        final Boolean updatedUser = userService.changePassword(user);

        assertNotNull(updatedUser);
        assertEquals(true, updatedUser);
    }
}