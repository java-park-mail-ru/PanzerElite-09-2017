package ru.mail.park.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionStates {
    private Boolean forward;
    private Boolean backward;
    private Boolean left;
    private Boolean right;
    private Boolean turretLeft;
    private Boolean turretRight;

    @JsonCreator
    public ActionStates(
            @JsonProperty("forward") boolean forward,
            @JsonProperty("backward") boolean backward,
            @JsonProperty("left") boolean left,
            @JsonProperty("right") boolean right,
            @JsonProperty("turretLeft") boolean turretLeft,
            @JsonProperty("turretRight") boolean turretRight
    ) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.turretLeft = turretLeft;
        this.turretRight = turretRight;
    }

    public Boolean getForward() {
        return forward;
    }

    public void setForward(Boolean forward) {
        this.forward = forward;
    }

    public Boolean getBackward() {
        return backward;
    }

    public void setBackward(Boolean backward) {
        this.backward = backward;
    }

    public Boolean getLeft() {
        return left;
    }

    public void setLeft(Boolean left) {
        this.left = left;
    }

    public Boolean getRight() {
        return right;
    }

    public void setRight(Boolean right) {
        this.right = right;
    }

    public Boolean getTurretLeft() {
        return turretLeft;
    }

    public void setTurretLeft(Boolean turretLeft) {
        this.turretLeft = turretLeft;
    }

    public Boolean getTurretRight() {
        return turretRight;
    }

    public void setTurretRight(Boolean turretRight) {
        this.turretRight = turretRight;
    }


}
