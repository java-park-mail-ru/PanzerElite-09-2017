package ru.mail.park.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class MessageSender {
    private ObjectMapper mapper;
    private String result;

    MessageSender() {
        mapper = new ObjectMapper();
    }

    private TextMessage getMessage(ReturningInstructions returningInstructions) {
        try {
            result = mapper.writeValueAsString(returningInstructions);
            return (new TextMessage(result));

        } catch (Exception e) {
            return null;
        }

    }

    public void send(WebSocketSession session, ReturningInstructions returning, Boolean flag) {
        try {
            returning.setMe(flag);
            if(!flag) {
                returning.setFire(false);
            }
            session.sendMessage(getMessage(returning));
        } catch (Exception e) {
        }
    }
}
