package de.ravens.arima.pod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@ConfigurationPropertiesScan
@ComponentScan
public class BotStart {
    public static void main(String[] args) {
        var app = SpringApplication.run(BotStart.class, args);
    }
}
