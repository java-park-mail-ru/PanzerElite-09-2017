package ru.mail.park.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class Player {
    private Integer id;
    private Coords coords;
    private Double angle;
    private Double turretAngle;
    private WebSocketSession session;


    public Player(WebSocketSession s, Integer id, Double x, Double y) {
        session = s;
        this.id = id;
        coords = new Coords(x, y);
        this.angle = -0.5 * Math.PI;
        this.turretAngle = 0.0;
    }

    private void moveForward() {
        this.coords.y += 0.3 * Math.cos(this.angle);
        this.coords.x += 0.3 * Math.sin(this.angle);
    }

    private void moveBackward() {
        this.coords.y -= 0.3 * Math.cos(this.angle);
        this.coords.x -= 0.3 * Math.sin(this.angle);
    }

    private void turnRight() {
        this.angle += 0.005 * Math.PI;
    }

    private void turnLeft() {
        this.angle -= 0.005 * Math.PI;
    }

    private void turnTurretRight() {
        this.turretAngle += 0.008 * Math.PI;
    }

    private void turnTurretLeft() {
        this.turretAngle -= 0.008 * Math.PI;
    }

    public void update(ActionStates actionStates) {
        System.out.println(actionStates.getForward());
        if (actionStates.getForward()) {
            this.moveForward();
        }
        if (actionStates.getBackward()) {
            this.moveBackward();
        }
        if (actionStates.getRight()) {
            this.turnRight();

        }
        if (actionStates.getLeft()) {
            this.turnLeft();

        }
        if (actionStates.getTurretLeft()) {
            this.turnTurretLeft();
        }
        if (actionStates.getTurretRight()) {
            this.turnTurretRight();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
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

    public ReturningInstructions getInstructionsOfPlayer() {
        return new ReturningInstructions(true, coords, angle, turretAngle, 0, false);
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }
}
