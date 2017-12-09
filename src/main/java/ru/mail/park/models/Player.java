package ru.mail.park.models;

import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;

//CHECKSTYLE:OFF
public class Player {
    private Integer id;
    private Coords coords;
    private Coords bulletCoords;
    private Double angle;
    private Double turretAngle;
    private WebSocketSession session;
    private ActionStates actionStates;
    private ActionStates DeprecatedMovemants;
    private int cameraType;
    //    ScheduledExecutorService sh;
    private ArrayList<GameObject> map;


    public Player(WebSocketSession s, Integer id, Double x, Double y, ArrayList map) {
        this.cameraType = 0;
        this.map = map;
        this.DeprecatedMovemants = new ActionStates(false, false, false, false, false, true, false, false);
        this.bulletCoords = new Coords(0.0, 0.0);
        session = s;
        this.id = id;
        coords = new Coords(x, y);
        this.angle = -0.5 * Math.PI;
        this.turretAngle = 0.0;
        this.actionStates = new ActionStates(false, false, false, false, false, false, false, false);
//        sh = Executors.newScheduledThreadPool(1);
    }

    public void updateActionStates(ActionStates actionStates) {
        this.actionStates = actionStates;
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

    public void update() {
        if (actionStates.getForward()) {
            DeprecatedMovemants.setBackward(false);
            if (!DeprecatedMovemants.getForward()) {
                moveForward();
            }
            if (tankCollisionWithMap()) {
                DeprecatedMovemants.setForward(true);
            } else {
                DeprecatedMovemants.setForward(false);
            }
        }
        if (actionStates.getBackward()) {
            DeprecatedMovemants.setForward(false);
            if (!DeprecatedMovemants.getBackward()) {
                moveBackward();
            }
            if (tankCollisionWithMap()) {
                DeprecatedMovemants.setBackward(true);
            } else {
                DeprecatedMovemants.setBackward(false);
            }
        }
        if (actionStates.getRight()) {
            if (!DeprecatedMovemants.getRight()) {
                turnRight();
            }
            if (tankCollisionWithMap()) {
                DeprecatedMovemants.setRight(true);
            } else {
                DeprecatedMovemants.setRight(false);
            }
        }
        if (actionStates.getLeft()) {
            if (!DeprecatedMovemants.getLeft()) {
                turnLeft();
            }
            if (tankCollisionWithMap()) {
                DeprecatedMovemants.setLeft(true);
            } else {
                DeprecatedMovemants.setLeft(false);
            }
        }
        if (actionStates.getTurretLeft()) {
            this.turnTurretLeft();
        }
        if (actionStates.getTurretRight()) {
            this.turnTurretRight();
        }
        if (actionStates.getChangeCamera()) {
            actionStates.setChangeCamera(false);
            this.cameraType++;
            this.cameraType %= 3;
        }
        if (actionStates.getFire()) {
            bulletCoords = fireCollision();
        }
        if (map.get(16).id.equals(id)) {
            map.get(16).x = coords.x;
            map.get(16).y = coords.y;
        } else {
            map.get(15).x = coords.x;
            map.get(15).y = coords.y;
        }
    }


    private boolean pointInPolygon(Double x, Double y, GameObject h) {
        Double leftx = (h.x - h.height / 2) * 1.0;
        Double rightx = leftx + h.height;
        if (x < rightx && x > leftx) {
            Double lefty = (h.y - h.width / 2) * 1.0;
            Double righty = lefty + h.width;
            if (y < righty && y > lefty) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Coords> getTankPoints() {
        ArrayList<Coords> angles = new ArrayList<>(4);
        angles.add(new Coords(this.coords.x + 3.9 * Math.abs(Math.sin(this.angle)),
                this.coords.y + 3.9 * Math.abs(Math.cos(this.angle))));
        angles.add(new Coords(this.coords.x - 3.9 * Math.abs(Math.sin(this.angle)),
                this.coords.y - 3.9 * Math.abs(Math.cos(this.angle))));
        angles.add(new Coords(this.coords.x + 3.9 * Math.abs(Math.sin(this.angle)),
                this.coords.y - 3.9 * Math.abs(Math.cos(this.angle))));
        angles.add(new Coords(this.coords.x - 3.9 * Math.abs(Math.sin(this.angle)),
                this.coords.y + 3.9 * Math.abs(Math.cos(this.angle))));
        return angles;
    }

    private boolean tankCollisionWithMap() {
        boolean flag = false;
        final ArrayList<Coords> angles = getTankPoints();
        outerloop:
        for (GameObject gameObject : this.map) {
            if (!gameObject.id.equals(id)) {
                for (Coords c : angles) {
                    flag = pointInPolygon(c.x, c.y, gameObject);
                    if (flag) {
                        break outerloop;
                    }
                }
            }
        }
        return flag;
    }

    private Coords fireCollision() {
        Integer cnt = 0;
        Coords currentCoords = new Coords(this.coords.x, this.coords.y);
        final Double currentAngle = turretAngle;
        while (!this.isBulletInHouse(currentCoords) && cnt < 10000) {
            currentCoords.x += 0.1 * Math.sin(currentAngle);
            currentCoords.y += 0.1 * Math.cos(currentAngle);
            cnt++;
        }
        if (cnt < 10000) {
            return currentCoords;
        }
        return new Coords(0.0, 0.0);
    }

    private boolean isBulletInHouse(Coords c) {
        boolean flag = false;
        outerloop:
        for (GameObject gameObject : this.map) {
            if (!gameObject.id.equals(id)) {
                flag = pointInPolygon(c.x, c.y, gameObject);
                if (flag) {
                    break outerloop;
                }
            }
        }
        return flag;
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
        ReturningInstructions ret = new ReturningInstructions(true, coords, bulletCoords, angle, turretAngle, cameraType, actionStates.getFire());
        actionStates.setFire(false);
        return ret;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }
}
