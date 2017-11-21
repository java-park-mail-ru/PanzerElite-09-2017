package ru.mail.park.controllers.websocket;


import com.sun.istack.internal.NotNull;
import ru.mail.park.models.HandleException;
import ru.mail.park.models.User;
import ru.mail.park.models.WebSocketMessage;

public abstract class SocketMessageHandler<T extends WebSocketMessage> {
    @NotNull
    private final Class<T> clazz;

    public SocketMessageHandler(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public void handleMessage(@NotNull WebSocketMessage message, @NotNull User forUser) throws HandleException {
        try {
            handle(clazz.cast(message), forUser);
        } catch (ClassCastException ex) {
            throw new HandleException("Can't read incoming message of type " + message.getClass(), ex);
        }
    }

    public abstract void handle(@NotNull T message, @NotNull User forUser) throws HandleException;
}
