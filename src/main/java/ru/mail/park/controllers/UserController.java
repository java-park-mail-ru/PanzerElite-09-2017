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
        final ResponseEntity<String> res = ResponseEntity.status(getStatus(userService.login(body)))
                .body("Trying to login");
        setHttpSession(res, httpSession, body);
        return res;
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> registerUser(@RequestBody User body, HttpSession httpSession) {
        final ResponseEntity<String> res = ResponseEntity.status(getStatus(userService.register(body)))
                .body("Trying to register");
        setHttpSession(res, httpSession, body);
        return res;
    }

    @RequestMapping(path = "/getuser", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getUser(HttpSession httpSession) {
        final User user = (User) httpSession.getAttribute(SESSIONKEY);
        if (user != null) {
            //return ResponseEntity.status(HttpStatus.OK).body(user.getUser().toString());
            return ResponseEntity.status(HttpStatus.OK).body("xuy");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> logoutUser(HttpSession httpSession) {
        if (httpSession.getAttribute(SESSIONKEY) != null) {
            httpSession.invalidate();
            return ResponseEntity.status(HttpStatus.OK).body("Successful logout");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsuccessful logout");
        }
    }

    @RequestMapping(path = "/changepassword", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> changePasswordUser(@RequestBody User body, HttpSession httpSession) {
        final User user = (User) httpSession.getAttribute(SESSIONKEY);
        body.setLogin(user.getLogin());
        final ResponseEntity<String> res = ResponseEntity.status(getStatus(userService.changePassword(body)))
                .body("Trying to change password");
        setHttpSession(res, httpSession, body);
        return res;
    }

    public void setHttpSession(ResponseEntity<String> res, HttpSession httpSession, User body) {
        if (res.getStatusCode() == HttpStatus.OK) {
            httpSession.setAttribute(SESSIONKEY, body);
        }
    }

    public HttpStatus getStatus(Boolean flag) {

        if (flag) {
            return HttpStatus.OK;
        } else {
            return HttpStatus.FORBIDDEN;
        }
    }
}
