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

    private TextMessage getMessage(ReturningInstructions r) {
        try {
            result = mapper.writeValueAsString(r);
            return (new TextMessage(result));

        } catch (Exception e) {
            return null;
        }

    }

    public void send(WebSocketSession s , ReturningInstructions r, Boolean flag) {
        try {
            r.setMe(flag);
            s.sendMessage(getMessage(r));
        } catch (Exception e) {

        }
    }
}
