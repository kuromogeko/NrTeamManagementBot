package de.ravens.arima.pod.core;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import reactor.core.publisher.Mono;

public class LoginHandler {
    public static Mono<GatewayDiscordClient> login(DiscordClient client){
        return client.login();
    }
}
