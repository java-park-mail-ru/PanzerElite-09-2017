package ru.mail.park.models;

//CHECKSTYLE:OFF
//cause of coords

public class GameObject {
    public Double x;
    public Double y;
    public Integer height;
    public Integer width;
    public Integer id;

    public GameObject(Double x, Double y, Integer height, Integer width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.id = -999;
    }

    public GameObject(Double x, Double y, Integer height, Integer width, Integer id) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.id = id;
    }

}