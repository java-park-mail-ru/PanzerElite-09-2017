package ru.mail.park.controllers.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import ru.mail.park.models.Room;
import ru.mail.park.models.ActionStates;
import ru.mail.park.services.UserService;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

//CHECKSTYLE:OFF
@Service
public final class RoomService {

    private final Queue<WebSocketSession> queue = new LinkedList<>();;
    private final ConcurrentHashMap<Long, Room> lobbies = new ConcurrentHashMap<>();;
    private final AtomicLong idgen=  new AtomicLong(255);;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    private RoomService( UserService usr) {
        userService = usr;
    }

    public void add(WebSocketSession s) {
        queue.add(s);
        if (queue.size() > 1) {
            log.warn("2 players found");
            Long rId = idgen.getAndIncrement();
            WebSocketSession s1 = (WebSocketSession) queue.poll();
            s1.getAttributes().put("RoomId", rId);
            WebSocketSession s2 = (WebSocketSession) queue.poll();
            s2.getAttributes().put("RoomId", rId);
            lobbies.put(rId, new Room(s1, s2, userService));
        }
    }

    public void destroyRoom(Long id) {
        log.warn("destroy room");
        lobbies.get(id).stopGame();
        lobbies.remove(id);
    }
    public void removeFromQueue(WebSocketSession session) {
        queue.remove(session);
    }

    public void getMessage(ActionStates message, WebSocketSession s) {
        if (!lobbies.isEmpty()) {

            lobbies.get((Long) s.getAttributes().get("RoomId")).getInstructions(message, (Integer) s.getAttributes().get("UserId"));
        }
    }

}
