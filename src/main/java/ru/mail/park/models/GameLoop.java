package ru.mail.park.models;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.time.Clock;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GameLoop implements Runnable {
    private static final long STEP_TIME = 40;

    private final Room gameMechanics;


    private Clock clock = Clock.systemDefaultZone();

    private Executor tickExecutor = Executors.newSingleThreadExecutor();

    public GameLoop(Room gameMechanics) {
        this.gameMechanics = gameMechanics;
    }

    @PostConstruct
    public void initAfterStartup() {
        System.out.println("im in postConstruct");
        tickExecutor.execute(this);
        this.run();
    }

    @Override
    public void run() {
        try {
            tickExecutor.execute(this);
            System.out.println("try in run");
            mainCycle();
        } finally {
            System.out.println("Mechanic executor terminated");
        }
    }

    private void mainCycle() {
        long lastFrameMillis = STEP_TIME;
        while (true) {
            try {
                final long before = clock.millis();

                gameMechanics.updateAndSend();

                final long after = clock.millis();
                try {
                    final long sleepingTime = Math.max(0, STEP_TIME - (after - before));
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    System.out.println("Mechanics thread was interrupted");
                }

                if (Thread.currentThread().isInterrupted()) {
//                    gameMechanics.reset();
                    return;
                }
                final long afterSleep = clock.millis();
                lastFrameMillis = afterSleep - before;
            } catch (RuntimeException e) {
//                LOGGER.error("Mechanics executor was reseted due to exception", e);
//                gameMechanics.reset();
                System.out.println("catch v cikle");

            }
        }
    }
}