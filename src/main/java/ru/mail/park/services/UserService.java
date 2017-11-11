package ru.mail.park.services;


import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.models.User;

import java.sql.PreparedStatement;


@Service
public class UserService {
    private final JdbcTemplate template;
    private final NamedParameterJdbcTemplate namedTemplate;

    public UserService(JdbcTemplate template, NamedParameterJdbcTemplate namedTemplate) {
        this.template = template;
        this.namedTemplate = namedTemplate;
    }

    public User getUserByLogin(String login) {
        try {
            return template.queryForObject(
                    "SELECT * FROM users WHERE lower(login) = lower(?)",
                    new Object[]{login}, USER_MAPPER);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User createUser(User body) {
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(con -> {
                final PreparedStatement pst = con.prepareStatement(
                        "insert into users(login, password)"
                                + " values(?,?)" + " returning id",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setString(1, body.getLogin());
                pst.setString(2, body.getPassword());

                return pst;
            }, keyHolder);
            body.setId(keyHolder.getKey().intValue());
            return body;
        } catch (DuplicateKeyException e) {
            return null;
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Boolean changePassword(User body) {
        if (getUserByLogin(body.getLogin()) == null) {
            return false;
        }
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(con -> {
                final PreparedStatement pst = con.prepareStatement(
                        "update users set"
                                + "  password = ? "
                                + " where LOWER(login) = LOWER(?) ",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setString(1, body.getPassword());
                pst.setString(2, body.getLogin());
                return pst;
            }, keyHolder);
            return true;
        } catch (DataAccessException e) {

            return false;
        }
    }


    private static final RowMapper<User> USER_MAPPER = (res, num) -> {
        String login = res.getString("login");
        String password = res.getString("password");
        Integer id = res.getInt("id");
        return new User(id, login, password);
    };

}


