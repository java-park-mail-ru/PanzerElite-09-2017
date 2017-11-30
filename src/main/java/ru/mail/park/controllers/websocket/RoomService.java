package ru.mail.park.controllers.websocket;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import ru.mail.park.models.Room;
import ru.mail.park.models.ActionStates;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class RoomService {

    private Queue<WebSocketSession> queue;
    private ConcurrentHashMap<Long, Room> lobbies;
    private AtomicLong idgen;

    private RoomService() {
        queue = new LinkedList<>();
        idgen = new AtomicLong(255);
        lobbies = new ConcurrentHashMap<>();
    }

    public void add(WebSocketSession s) {
        queue.add(s);
        if (queue.size() > 1) {
            System.out.println("2 players found");
            Long rId = idgen.getAndIncrement();
            WebSocketSession s1 = (WebSocketSession) queue.poll();
            s1.getAttributes().put("RoomId", rId);
            WebSocketSession s2 = (WebSocketSession) queue.poll();
            s2.getAttributes().put("RoomId", rId);
            lobbies.put(rId, new Room(s1, s2));
        }
    }

    public void getMessage(ActionStates message, WebSocketSession s) {
        if (!lobbies.isEmpty()) {

            lobbies.get((Long) s.getAttributes().get("RoomId")).getInstructions(message, (Integer) s.getAttributes().get("UserId"));
        }
    }

}
