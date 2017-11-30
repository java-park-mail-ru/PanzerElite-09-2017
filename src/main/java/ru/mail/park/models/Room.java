package ru.mail.park.models;

import org.springframework.web.socket.WebSocketSession;

public class Room {
    private Long id;
    private Player p1;
    private Player p2;
    private MessageSender sender;

    public Room(WebSocketSession s1, WebSocketSession s2) {
        final Double startPosition = 50.0;
        System.out.println("im in room construcotr");
        this.id = (Long) s1.getAttributes().get("RoomId");
        p1 = new Player(s1, (Integer) s1.getAttributes().get("UserId"), startPosition, startPosition);
        p2 = new Player(s2, (Integer) s2.getAttributes().get("UserId"), startPosition, -1 * startPosition);
        sender = new MessageSender();
        GameLoop gl = new GameLoop(this);
        System.out.println("im in room construcotr2");
        Thread myThready = new Thread(gl);
        myThready.start();
        System.out.println("im in room construcotr3");

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
        sender.send(p1.getSession(), p1.getInstructionsOfPlayer(), true);
        sender.send(p1.getSession(), p2.getInstructionsOfPlayer(), false);
        sender.send(p2.getSession(), p1.getInstructionsOfPlayer(), false);
        sender.send(p2.getSession(), p2.getInstructionsOfPlayer(), true);
    }


}
