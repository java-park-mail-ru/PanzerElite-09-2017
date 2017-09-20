package ru.mail.park.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.models.User;
import ru.mail.park.services.UserService;
import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService = new UserService();

    private static final String SESSIONKEY = "user";
    private static final String URL = "*";
    private static final Integer AGE = 3600;

    @CrossOrigin(origins = URL, maxAge = AGE)
    @RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> loginUser(@RequestBody User body, HttpSession httpSession) {
        final ResponseEntity<String> res = new ResponseEntity<>(userService.login(body));
        setHttpSession( res, httpSession, body);
        return res;
    }

    @CrossOrigin(origins = URL, maxAge = AGE)
    @RequestMapping(path = "/register", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> registerUser(@RequestBody User body, HttpSession httpSession) {
        final ResponseEntity<String> res = new ResponseEntity<>( userService.register(body));
        setHttpSession(res, httpSession, body);
        return res;
    }

    @CrossOrigin(origins = URL, maxAge = AGE)
    @RequestMapping(path = "/getuser", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getUser( HttpSession httpSession) {
        final ResponseEntity<String> answer;
        final User user = ( User) httpSession.getAttribute(SESSIONKEY);
        if( user != null) {
            answer = new ResponseEntity<>( user.getUser().toString(), HttpStatus.OK);
        } else {
            answer = new ResponseEntity<>( HttpStatus.UNAUTHORIZED);
        }
        return answer;
    }

    @CrossOrigin(origins = URL, maxAge = AGE)
    @RequestMapping(path = "/logout", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> logoutUser( HttpSession httpSession) {
        final ResponseEntity<String> answer;
        if( httpSession.getAttribute(SESSIONKEY) != null) {
            httpSession.removeAttribute(SESSIONKEY);
            answer = new ResponseEntity<>( HttpStatus.OK);
        } else {
            answer = new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }
        return answer;
    }

    @CrossOrigin(origins = URL, maxAge = AGE)
    @RequestMapping(path = "/changepassword", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> changePasswordUser(@RequestBody User body, HttpSession httpSession) {
        final User user = (User) httpSession.getAttribute( SESSIONKEY);
        body.setLogin( user.getLogin());
        final ResponseEntity<String> res = new ResponseEntity<>( userService.changePassword(body));
        setHttpSession( res, httpSession, body);
        return res;
    }

    public void setHttpSession( ResponseEntity<String> res, HttpSession httpSession, User body) {
        if( res.getStatusCode() == HttpStatus.OK) {
            httpSession.setAttribute( SESSIONKEY, body);
        }
    }
}
