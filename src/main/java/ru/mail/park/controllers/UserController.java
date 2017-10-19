package ru.mail.park.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.models.User;
import ru.mail.park.services.UserService;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpSession;

@RestController
@CrossOrigin({"*", "https://panzerelitefront.herokuapp.com"})
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService usr) {
        this.userService = usr;
    }

    private static final String SESSIONKEY = "user";

    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> loginUser(@RequestBody User body, HttpSession httpSession) {
        final User user = userService.getUserByLogin(body.getLogin());
        if (user == null || !body.getPassword().equals(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cant find such user");
        }
        setHttpSession(httpSession, user);
        return ResponseEntity.status(HttpStatus.OK).body("Successful Login");
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> registerUser(@RequestBody User body, HttpSession httpSession) {
        if (body == null || body.getLogin() == null || body.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Some conflict, cant create user.");
        }
        if (body.getLogin().isEmpty() || body.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Some conflict, cant create user.");
        }
        final User user = userService.createUser(body);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Some conflict, cant create user.");
        }
        setHttpSession(httpSession, user);
        return ResponseEntity.status(HttpStatus.OK).body("Successful Registration");
    }

    @RequestMapping(path = "/getuser", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getUser(HttpSession httpSession) {
        final User user = (User) httpSession.getAttribute(SESSIONKEY);
        if (user != null) {
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> logoutUser(HttpSession httpSession) {
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            httpSession.invalidate();
            return ResponseEntity.status(HttpStatus.OK).body("Successful logout");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsuccessful logout");
        }
    }

    @RequestMapping(path = "/changepassword", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> changePasswordUser(@RequestBody User body, HttpSession httpSession) {
        final User user = (User) httpSession.getAttribute(SESSIONKEY);
        body.setLogin(user.getLogin());
        if (!userService.changePassword(body)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password change unsuccessful");
        }
        setHttpSession(httpSession, body);
        return ResponseEntity.status(HttpStatus.OK).body("password change successful");
    }


    public void setHttpSession(HttpSession httpSession, User body) {
        httpSession.setAttribute(SESSIONKEY, body);
    }


}
