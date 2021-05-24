package de.ravens.arima.pod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class BotStart {
    public static void main(String[] args) {
        var app = SpringApplication.run(BotStart.class, args);
    }
}
