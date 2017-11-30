package ru.mail.park.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
//CHECKSTYLE:OFF
public class Coords {
    public Double x;
    public Double y;

    @JsonCreator
    public Coords(
            @JsonProperty("x") Double x,
            @JsonProperty("y") Double y
    ) {
        this.x = x;
        this.y = y;
    }


}
