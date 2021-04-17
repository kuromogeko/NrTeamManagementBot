package de.ravens.arima.pod.runtime;

import discord4j.core.GatewayDiscordClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RuntimeConfiguration implements CommandLineRunner {
    private final GatewayDiscordClient gatewayDiscordClient;

    public RuntimeConfiguration(GatewayDiscordClient gatewayDiscordClient, ApplicationContext context){
        this.gatewayDiscordClient = gatewayDiscordClient;
    }

    @Override
    public void run(String... args) throws Exception {
        this.gatewayDiscordClient.onDisconnect().block();
    }
}
