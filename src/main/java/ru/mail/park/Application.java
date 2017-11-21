package ru.mail.park;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import ru.mail.park.WebsocketConfiguration;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
        SpringApplication.run(new Class[]{WebsocketConfiguration.class, Application.class}, args);
    }
}
