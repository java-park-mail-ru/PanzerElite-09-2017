package ru.mail.park.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;

public class ReturningInstructions {
    private Boolean me;
    private Double angle;
    private Double x;
    private Double y;
    private Double turretAngle;
    private Integer cameraType;
    private Boolean fire;


    @JsonCreator
    public ReturningInstructions(
            @JsonProperty("me") Boolean me,
            @JsonProperty("coords") Coords coords,
            @JsonProperty("angle") Double angle,
            @JsonProperty("turretAngle") Double turretAngle,
            @JsonProperty("cameraType") Integer cameraType,
            @JsonProperty("fire") Boolean fire
    ) {
        this.me = me;
        this.x = coords.x;
        this.y = coords.y;
        this.angle = angle;
        this.cameraType = 0;
        this.fire = false;
        this.turretAngle = turretAngle;

    }




    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public Double getTurretAngle() {
        return turretAngle;
    }

    public void setTurretAngle(Double turretAngle) {
        this.turretAngle = turretAngle;
    }

    public Integer getCameraType() {
        return cameraType;
    }

    public void setCameraType(Integer cameraType) {
        this.cameraType = cameraType;
    }

    public Boolean getFire() {
        return fire;
    }

    public void setFire(Boolean fire) {
        this.fire = fire;
    }

    public Boolean getMe() {
        return me;
    }

    public void setMe(Boolean me) {
        this.me = me;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}