package ru.mail.park.controllers.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.mail.park.models.ActionStates;
import ru.mail.park.models.User;

import javax.validation.constraints.NotNull;
import java.io.IOException;

import static org.springframework.web.socket.CloseStatus.SERVER_ERROR;

public class SocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketHandler.class);
    private static final CloseStatus ACCESS_DENIED = new CloseStatus(4500, "Not logged in. Access denied");
    private SocketMessageHandlerManager handlerManager;
    private ObjectMapper objectMapper;
    private static final String SESSIONKEY = "user";
    private RoomService roomService;


    public SocketHandler(@NotNull SocketMessageHandlerManager manager,
                         ObjectMapper objectMapper, RoomService rs) {
        roomService = rs;
        this.handlerManager = manager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        final User user = checkUser(session);
        if (user != null) {
            session.getAttributes().put("UserId", user.getId());
            roomService.add(session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if (!session.isOpen()) {
            return;
        }
        if (checkUser(session) == null) {
            return;
        }
        handleMessage(session, message);
    }

    private User checkUser(WebSocketSession session) {
        final User user = (User) session.getAttributes().get(SESSIONKEY);
        if (user == null) {
            LOGGER.warn("User requested websocket is not registred or not logged in. Openning websocket session is denied.");
            closeSessionSilently(session, ACCESS_DENIED);
            return null;
        }
        return user;
    }


    @SuppressWarnings("OverlyBroadCatchBlock")
    private void handleMessage(WebSocketSession session, TextMessage text) {
        final ActionStates message;
        try {
            message = objectMapper.readValue(text.getPayload(), ActionStates.class);
            //System.out.println(message.getForward() + " " + message.getBackward());
            roomService.getMessage(message, session);
        } catch (IOException ex) {
            LOGGER.error("wrong json format at game response", ex);
        }
        return;
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
        //        LOGGER.warn("Websocket transport problem", throwable);
        closeSessionSilently(webSocketSession, ACCESS_DENIED);
    }

    private void closeSessionSilently(@NotNull WebSocketSession session, @Nullable CloseStatus closeStatus) {
        final CloseStatus status = closeStatus;
        if (status == null) {
            closeStatus = SERVER_ERROR;
        }
        try {
            if (session.getAttributes().get("RoomId") != null) {
                roomService.destroyRoom((Long) session.getAttributes().get("RoomId"));
            } else {
                roomService.removeFromQueue(session);
            }
            session.close(status);
        } catch (Exception ignore) {
            LOGGER.debug("Ignore", ignore);
        }

    }
}