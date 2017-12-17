package ru.mail.park.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    private Integer id;
    private Double rank;
    private Integer position;
    private String login;
    private String password;

    public User() {

    }

    @JsonCreator
    public User(

            @JsonProperty("id") int id,
            @JsonProperty("position") int position,
            @JsonProperty("rank") double rank,
            @JsonProperty("login") String login,
            @JsonProperty("password") String password
    ) {
        this.position = position;
        this.rank = rank;
        this.id = id;
        this.login = login;
        this.password = password;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;

    }

    public String getLogin() {
        return login;
    }


    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public Double getRank() {
        return rank;
    }

    public void setRank(Double rank) {
        this.rank = rank;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
