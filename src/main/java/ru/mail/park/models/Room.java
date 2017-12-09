package ru.mail.park.models;

import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;

public class Room {
    private Long id;
    private Player p1;
    private Player p2;
    private MessageSender sender;
    private ArrayList<GameObject> map;
    private GameLoop gl;

    public Room(WebSocketSession s1, WebSocketSession s2) {
        final Double startPosition = 50.0;
        System.out.println("im in room construcotr");
        this.id = (Long) s1.getAttributes().get("RoomId");
        mapInit();
        p1 = new Player(s1, (Integer) s1.getAttributes().get("UserId"), startPosition, startPosition, map);
        p2 = new Player(s2, (Integer) s2.getAttributes().get("UserId"), startPosition, -1 * startPosition, map);
        map.add(new GameObject(p1.getCoords().x, p1.getCoords().y, 6, 6, p1.getId()));
        map.add(new GameObject(p2.getCoords().x, p2.getCoords().y, 6, 6, p2.getId()));
        sender = new MessageSender();
        gl = new GameLoop(this);
        System.out.println("im in room construcotr2");
        Thread myThready = new Thread(gl);
        myThready.start();
        System.out.println("im in room construcotr3");

    }

    private void mapInit() {
        map = new ArrayList<>(17);
//        map.add(new GameObject(p1.getCoords().x, p1.getCoords().y, 6, 6, p1.getId()));
//        map.add(new GameObject(p2.getCoords().x, p2.getCoords().y, 6, 6, p2.getId()));
//        map.add(new GameObject(150.0, 150.0, 6, 6, p1.getId()));
//        map.add(new GameObject(150.0, -150.0, 6, 6, p2.getId()));
        map.add(new GameObject(0.0, 0.0, 57, 58));
        map.add(new GameObject(-136.0, 88.0, 18, 17));
        map.add(new GameObject(-48.0, 108.0, 18, 17));
        map.add(new GameObject(-20.0, 56.0, 18, 17));
        map.add(new GameObject(-136.0, -20.0, 18, 17));
        map.add(new GameObject(68.0, 40.0, 18, 17));
        map.add(new GameObject(100.0, 40.0, 18, 17));
        map.add(new GameObject(132.0, 40.0, 18, 17));
        map.add(new GameObject(-100.0, 24.0, 32, 20));
        map.add(new GameObject(-128.0, -88.0, 32, 20));
        map.add(new GameObject(-20.0, -92.0, 20, 32));
        map.add(new GameObject(32.0, 104.0, 32, 20));
        map.add(new GameObject(140.0, 84.0, 20, 32));
        map.add(new GameObject(-84.0, 64.0, 42, 25));
        map.add(new GameObject(-56.0, -40.0, 25, 42));
    }


    public void getInstructions(ActionStates message, Integer uid) {
        if (uid.equals(p1.getId())) {
            p1.updateActionStates(message);
        } else {
            p2.updateActionStates(message);
        }
    }

    public void updateAndSend() {
        p1.update();
        p2.update();
        ReturningInstructions r1 = p1.getInstructionsOfPlayer();
        r1.setHP(p2.getOpHP());
        ReturningInstructions r2 = p2.getInstructionsOfPlayer();
        r2.setHP(p1.getOpHP());
        sender.send(p1.getSession(), r1, true);
        sender.send(p1.getSession(), r2, false);
        sender.send(p2.getSession(), r1, false);
        sender.send(p2.getSession(), r2, true);
    }

    public void stopGame() {
        System.out.println("stop game");
        gl.stop();
        try {
            p1.getSession().close();
        } catch (Exception e) {

        }
        try {
            p2.getSession().close();
        } catch (Exception e) {

        }

    }

}
