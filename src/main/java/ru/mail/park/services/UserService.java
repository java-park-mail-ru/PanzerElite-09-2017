package ru.mail.park.services;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.models.User;

import java.sql.PreparedStatement;


@Service
@Transactional
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

    public User createUser(User body) {
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            final int three = 3;
            final int four = 4;
            final int five = 5;
            template.update(con -> {
                final PreparedStatement pst = con.prepareStatement(
                        "insert into users(login, password, frags, deaths, rank )"
                                + " values(?,?,?,?,?)" + " returning id",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setString(1, body.getLogin());
                pst.setString(2, body.getPassword());
                pst.setInt(three, body.getFrags());
                pst.setInt(four, body.getDeaths());
                pst.setInt(five, body.getRank());
                return pst;
            }, keyHolder);
            body.setId(keyHolder.getKey().intValue());
            return body;
        } catch (DuplicateKeyException e) {
            return null;
        }
    }

    public Boolean changePassword(User body) {
        if (getUserByLogin(body.getLogin()) == null) {
            return false;
        }
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(con -> {
                final PreparedStatement pst = con.prepareStatement(
                        "update users set"
                                + "  password = COALESCE(?, password) "
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
        Integer rank = res.getInt("rank");
        Integer deaths = res.getInt("deaths");
        Integer frags = res.getInt("frags");
        return new User(id, frags, deaths, rank, login, password);
    };

}

