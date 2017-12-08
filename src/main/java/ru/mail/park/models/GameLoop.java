package ru.mail.park.models;


import javax.annotation.PostConstruct;
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
            System.out.println("try in run");//TODO выводится два ращза почему?
            mainCycle();
        } finally {
            System.out.println("Mechanic executor terminated");
        }
    }

    public void stop() {
        Thread.currentThread().interrupt();
        System.out.println("im in stop function");
    }
    private void mainCycle() {
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
                    System.out.println("game loop interupted");
                    return;
                }
            } catch (RuntimeException e) {
                System.out.println("catch v cikle");
            }
        }
    }
}