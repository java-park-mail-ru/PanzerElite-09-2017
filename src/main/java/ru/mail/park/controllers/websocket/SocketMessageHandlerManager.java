package ru.mail.park.controllers.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.mail.park.models.HandleException;
import ru.mail.park.models.User;
import ru.mail.park.models.ActionStates;


import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Service
public class SocketMessageHandlerManager {
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketMessageHandlerManager.class);
    private final Map<Class<?>, SocketMessageHandler<?>> handlers = new HashMap<>();


    public void handle(@NotNull ActionStates message, @NotNull User forUser) throws HandleException {

        final SocketMessageHandler<?> messageHandler = handlers.get(message.getClass());
        if (messageHandler == null) {
            throw new HandleException("No handler for message of " + message.getClass().getName() + " type");
        }
        messageHandler.handleMessage(message, forUser);
        LOGGER.trace("Message handled: type =[" + message.getClass().getName() + ']');
    }

}