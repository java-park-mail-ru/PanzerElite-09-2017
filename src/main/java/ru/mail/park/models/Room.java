package ru.mail.park.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;
import ru.mail.park.services.UserService;

import java.util.ArrayList;

public class Room {
    private Long id;
    private Player p1;
    private Player p2;
    private MessageSender sender;
    private ArrayList<GameObject> map;
    private GameLoop gl;
    private final UserService userService;
    private Thread myThread;


    public Room(WebSocketSession s1, WebSocketSession s2, UserService usr) {
        userService = usr;
        ///
        final Double startPosition = 50.0;
        System.out.println("im in room construcotr");
        this.id = (Long) s1.getAttributes().get("RoomId");
        mapInit();
        p1 = new Player(s1, (Integer) s1.getAttributes().get("UserId"), startPosition, startPosition, map);
        p2 = new Player(s2, (Integer) s2.getAttributes().get("UserId"), startPosition, -1 * startPosition, map);
        map.add(new GameObject(p1.getCoords().x, p1.getCoords().y, 6, 6, p1.getId()));
        map.add(new GameObject(p2.getCoords().x, p2.getCoords().y, 6, 6, p2.getId()));
        sender = new MessageSender();
        gl = new GameLoop(this);
        System.out.println("im in room construcotr2");
        myThread = new Thread(gl);
        myThread.start();
        System.out.println("im in room construcotr3");

    }

    private void mapInit() {
        map = new ArrayList<>(17);
        map.add(new GameObject(0.0, 0.0, 57, 58));
        map.add(new GameObject(-136.0, 88.0, 18, 17));
        map.add(new GameObject(-48.0, 108.0, 18, 17));
        map.add(new GameObject(-20.0, 56.0, 18, 17));
        map.add(new GameObject(-136.0, -20.0, 18, 17));
        map.add(new GameObject(68.0, 40.0, 18, 17));
        map.add(new GameObject(100.0, 40.0, 18, 17));
        map.add(new GameObject(132.0, 40.0, 18, 17));
        map.add(new GameObject(-100.0, 24.0, 32, 20));
        map.add(new GameObject(-128.0, -88.0, 32, 20));
        map.add(new GameObject(-20.0, -92.0, 20, 32));
        map.add(new GameObject(32.0, 104.0, 32, 20));
        map.add(new GameObject(140.0, 84.0, 20, 32));
        map.add(new GameObject(-84.0, 64.0, 42, 25));
        map.add(new GameObject(-56.0, -40.0, 25, 42));
//        map.add(new GameObject(-250.0, 0.0, 500, 2));
//        map.add(new GameObject(250.0, 0.0, 500, 2));
//        map.add(new GameObject(0.0, -250.0, 2, 500));
//        map.add(new GameObject(0.0, 250.0, 2, 500));
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
        r1.setHP(p2.getOpHP());
        ReturningInstructions r2 = p2.getInstructionsOfPlayer();
        r2.setHP(p1.getOpHP());
       final boolean flag =  gameState(r1, r2);
        if (!sender.send(p1.getSession(), r1, true)) {
            stopGame();
        }
        sender.send(p1.getSession(), r2, false);
        sender.send(p2.getSession(), r1, false);
        if (!sender.send(p2.getSession(), r2, true)) {
            stopGame();
        }
        if(flag) {
            stopGame();
        }
    }

    private boolean gameState(ReturningInstructions r1, ReturningInstructions r2) {
        if (p1.getOpHP() <= 0) {
            User u = (User) p1.getSession().getAttributes().get("user");
            userService.IncrementFrags((User) p1.getSession().getAttributes().get("user"));
            userService.IncrementDeaths((User) p2.getSession().getAttributes().get("user"));

            r1.setVictory(1);
            r2.setVictory(-1);
            if (p2.getOpHP() <= 0) {
                r1.setVictory(0);
                r2.setVictory(0);
                userService.IncrementFrags((User) p1.getSession().getAttributes().get("user"));
                userService.IncrementDeaths((User) p2.getSession().getAttributes().get("user"));
                userService.IncrementFrags((User) p2.getSession().getAttributes().get("user"));
                userService.IncrementDeaths((User) p1.getSession().getAttributes().get("user"));
            }
            return true;
        } else if (p2.getOpHP() <= 0) {
            User u = (User) p1.getSession().getAttributes().get("user");
            userService.IncrementFrags((User) p2.getSession().getAttributes().get("user"));
            userService.IncrementDeaths((User) p1.getSession().getAttributes().get("user"));
            r1.setVictory(-1);
            r2.setVictory(1);
            return true;
        }
        return false;
    }

    public void stopGame() {
        System.out.println("stop game");
        gl.stop();
        myThread.interrupt();
        try {
            p1.getSession().close();
        } catch (Exception e) {

        }
        try {
            p2.getSession().close();
        } catch (Exception e) {

        }

    }

}
