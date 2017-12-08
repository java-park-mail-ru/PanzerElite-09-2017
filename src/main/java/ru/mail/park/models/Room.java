package ru.mail.park.models;

import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;

public class Room {
    private Long id;
    private Player p1;
    private Player p2;
    private MessageSender sender;
    private ArrayList<House> map;
    private GameLoop gl;

    public Room(WebSocketSession s1, WebSocketSession s2) {
        final Double startPosition = 50.0;
        System.out.println("im in room construcotr");
        this.id = (Long) s1.getAttributes().get("RoomId");
        mapInit();
        p1 = new Player(s1, (Integer) s1.getAttributes().get("UserId"), startPosition, startPosition, map);
        p2 = new Player(s2, (Integer) s2.getAttributes().get("UserId"), startPosition, -1 * startPosition, map);
        sender = new MessageSender();
        gl = new GameLoop(this);
        System.out.println("im in room construcotr2");
        Thread myThready = new Thread(gl);
        myThready.start();
        System.out.println("im in room construcotr3");

    }
    private void mapInit() {
        map = new ArrayList<>(15);
        map.add(new House(0 ,0 , 57, 58));
        map.add(new House(-136 ,88 , 18, 17));
        map.add(new House(-48 ,108 , 18, 17));
        map.add(new House(-20 ,56, 18, 17));
        map.add(new House(-136 ,-20 , 18, 17));
        map.add(new House(68 ,40 , 18, 17));
        map.add(new House(100 ,40, 18, 17));
        map.add(new House(132 ,40 , 18, 17));
        map.add(new House(-100 ,24 , 32, 20));
        map.add(new House(-128 ,-88 , 32, 20));
        map.add(new House(-20 ,-92 , 20, 32));
        map.add(new House(32 ,104 , 32, 20));
        map.add(new House(140 ,84 , 20, 32));
        map.add(new House(-84 ,64 , 42, 25));
        map.add(new House(-56 ,-40 , 25, 42));
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
        ReturningInstructions r2 = p2.getInstructionsOfPlayer();
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
        try{
            p2.getSession().close();
        } catch (Exception e) {

        }

    }

}
