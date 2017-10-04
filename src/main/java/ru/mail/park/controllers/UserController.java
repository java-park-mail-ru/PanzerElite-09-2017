package ru.mail.park.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.models.User;
import ru.mail.park.services.UserService;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
public class UserController {

    private UserService userService = new UserService();

    private static final String SESSIONKEY = "user";

    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> loginUser(@RequestBody User body, HttpSession httpSession) {
        final ResponseEntity<String> res = ResponseEntity.status(userService.login(body)).body("Trying to log in");
        setHttpSession(res, httpSession, body);
        return res;
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> registerUser(@RequestBody User body, HttpSession httpSession) {
        final ResponseEntity<String> res =ResponseEntity.status(userService.register(body)).body("Trying to register");
        setHttpSession(res, httpSession, body);
        return res;
    }

    @RequestMapping(path = "/getuser", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getUser(HttpSession httpSession) {
        final User user = (User) httpSession.getAttribute(SESSIONKEY);
        if (user != null) {
            return ResponseEntity.status(HttpStatus.OK).body(user.getUser().toString());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> logoutUser(HttpSession httpSession) {
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            //httpSession.removeAttribute(SESSIONKEY);
            httpSession.invalidate();
            return ResponseEntity.status(HttpStatus.OK).body("Successful log out");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsuccessful log out");
        }
    }

    @RequestMapping(path = "/changepassword", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> changePasswordUser(@RequestBody User body, HttpSession httpSession) {
        final User user = (User) httpSession.getAttribute(SESSIONKEY);
        body.setLogin(user.getLogin());
        final ResponseEntity<String> res = ResponseEntity.status(userService.changePassword(body)).body("Trying to change password");
        setHttpSession(res, httpSession, body);
        return res;
    }

    public void setHttpSession(ResponseEntity<String> res, HttpSession httpSession, User body) {
        if (res.getStatusCode() == HttpStatus.OK) {
            httpSession.setAttribute(SESSIONKEY, body);
        }
    }
}
