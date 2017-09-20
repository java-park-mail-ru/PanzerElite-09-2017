package ru.mail.park.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

public class User {
    private String id;
    private String login;
    private String password;

    public User() {

    }

    @JsonCreator
    public User(
            @JsonProperty("id") String id,
            @JsonProperty("login") String login,
            @JsonProperty("password") String password
    ) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public JSONObject getUser() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("login", login);
        jsonObject.put("password", password);
        return jsonObject;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
