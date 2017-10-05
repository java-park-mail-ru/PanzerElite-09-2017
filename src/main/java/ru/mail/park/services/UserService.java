package ru.mail.park.services;

import org.springframework.stereotype.Service;
import ru.mail.park.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private Map<String, User> db;
    private AtomicLong idgen;

    public UserService() {
        db = new HashMap<>();
        idgen = new AtomicLong(0);
    }

    public Boolean login(User user) {
        final String login = user.getLogin();
        final String password = user.getPassword();
        final User dbUser = db.get(login);
        return (dbUser != null && dbUser.getPassword().equals(password));
    }

    public Boolean register(User user) {
        user.setId(idgen.getAndIncrement());
        if (db.get(user.getLogin()) == null) {
            db.put(user.getLogin(), user);
            return true;
        } else {
            return false;
        }
    }

    public Boolean changePassword(User user) {
        final User dbUser = db.get(user.getLogin());
        final String password = user.getPassword();
        if (dbUser != null) {
            dbUser.setPassword(password);
            return true;
        } else {
            return false;
        }
    }

}
