package ru.mail.park.services;

import org.springframework.http.HttpStatus;
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

    public HttpStatus login(User user) {
        final String login = user.getLogin();
        final String password = user.getPassword();
        if (db.get(login) != null && db.get(login).getPassword().equals(password)) {
            return HttpStatus.OK;
        } else {
            return HttpStatus.NOT_FOUND;
        }
    }

    public HttpStatus register(User user) {
        user.setId(String.valueOf(idgen.getAndIncrement()));
        if (db.put(user.getLogin(), user) == null) {
            return HttpStatus.OK;
        } else {
            idgen.getAndDecrement();
            return HttpStatus.FORBIDDEN;
        }
    }

    public HttpStatus changePassword(User user) {
        final String password = user.getPassword();
        if (db.get(user.getLogin()) != null) {
            db.get(user.getLogin()).setPassword(password);
            return HttpStatus.OK;
        } else {
            return HttpStatus.FORBIDDEN;
        }
    }

}
