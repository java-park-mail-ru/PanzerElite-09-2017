package ru.mail.park.controllers;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mail.park.models.User;
import ru.mail.park.services.UserService;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String LOGIN = "LOGIN";
    private static final String PASSWORD = "PASSWORD";
    private static final HttpHeaders REQUEST_HEADERS = new HttpHeaders();


    @BeforeClass
    public static void setHttpHeaders() {
        final List<String> origin = Collections.singletonList("http://localhost:8000");
        REQUEST_HEADERS.put(HttpHeaders.ORIGIN, origin);
        final List<String> contentType = Collections.singletonList("application/json");
        REQUEST_HEADERS.put(HttpHeaders.CONTENT_TYPE, contentType);
    }


    @Test
    public void testRegisterEmptyLoginAndPas() {
        final User user = new User(0, 0, 0, 0, "", "");
        final HttpEntity<User> requestEntity = new HttpEntity<>(user, REQUEST_HEADERS);

        final ResponseEntity<String> response = restTemplate.postForEntity("/api/user/register", requestEntity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Some conflict, cant create user.", response.getBody());
    }

    @Test
    public void testRegisterConflictUser() {
        final User emptyUser = new User();
        when(userService.getUserByLogin(anyString())).thenReturn(emptyUser);

        final User newProfile = new User(0, 0, 0, 0, LOGIN, PASSWORD);
        final HttpEntity<User> requestEntity = new HttpEntity<>(newProfile, REQUEST_HEADERS);

        final ResponseEntity<String> response = restTemplate.postForEntity("/api/user/register", requestEntity, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Some conflict, cant create user.", response.getBody());
    }

}
