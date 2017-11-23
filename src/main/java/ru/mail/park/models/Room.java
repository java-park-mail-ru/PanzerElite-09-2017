package ru.mail.park.models;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.WebSocketSession;

public class Room {
    private Long id;
    private Player p1, p2;
    private MessageSender sender;

    public Room(WebSocketSession s1, WebSocketSession s2) {
        this.id = (Long) s1.getAttributes().get("RoomId");
        p1 = new Player(s1, (Integer) s1.getAttributes().get("UserId"), 50.0, 50.0);
        p2 = new Player(s2, (Integer) s2.getAttributes().get("UserId"), 50.0, -50.0);
        sender = new MessageSender();
    }

    public void getInstructions(ActionStates message, Integer uId) {
        if (uId.equals(p1.getId())) {
            p1.update(message);
        } else {
            p2.update(message);
        }
        sender.send(p1.getSession(), p1.getInstructionsOfPlayer(), true);
        sender.send(p1.getSession(), p2.getInstructionsOfPlayer(), false);
        sender.send(p2.getSession(), p1.getInstructionsOfPlayer(), false);
        sender.send(p2.getSession(), p2.getInstructionsOfPlayer(), true);
    }


}
