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
import java.util.ArrayList;
import java.util.List;


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
                        "insert into users(login, password, deaths)"
                                + " values(?,?, 1)" + " returning id",
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Boolean incrementFrags(User body) {
        if (getUserByLogin(body.getLogin()) == null) {
            return false;
        }
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(con -> {
                final PreparedStatement pst = con.prepareStatement(
                        "update users set"
                                + "  frags = frags + 1 "
                                + " where LOWER(login) = LOWER(?) ",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setString(1, body.getLogin());
                return pst;
            }, keyHolder);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Boolean incrementDeaths(User body) {
        if (getUserByLogin(body.getLogin()) == null) {
            return false;
        }
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(con -> {
                final PreparedStatement pst = con.prepareStatement(
                        "update users set"
                                + "  deaths = deaths + 1 "
                                + " where LOWER(login) = LOWER(?) ",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setString(1, body.getLogin());
                return pst;
            }, keyHolder);
            return true;
        } catch (DataAccessException e) {

            return false;
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<User> getScoreBoards() {
        try {
            List<Object> myObj = new ArrayList<>();
            StringBuilder myStr = new StringBuilder("SELECT id, login , frags, deaths from users ORDER BY (frags/deaths) DESC limit 10 ;");
            List<User> result = template.query(myStr.toString(), myObj.toArray(), USER_SCORE);
            return result;
        } catch (DataAccessException e) {
            return null;
        }
    }


    private static final RowMapper<User> USER_MAPPER = (res, num) -> {
        String login = res.getString("login");
        String password = res.getString("password");
        Integer id = res.getInt("id");
        Integer frags = res.getInt("frags");
        Integer deaths = res.getInt("deaths");
        return new User(id, frags / deaths, login, password);
    };

    private static final RowMapper<User> USER_SCORE = (res, num) -> {
        String login = res.getString("login");
        String password = "****";
        Integer id = res.getInt("id");
        Integer frags = res.getInt("frags");
        Integer deaths = res.getInt("deaths");
        return new User(id, frags / deaths, login, password);
    };


}


